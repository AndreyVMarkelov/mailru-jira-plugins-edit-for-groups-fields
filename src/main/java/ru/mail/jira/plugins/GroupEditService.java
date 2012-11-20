/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.VelocityException;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.xsrf.XsrfTokenGenerator;
import com.atlassian.jira.util.I18nHelper;

/**
 * Plug-In REST service.
 * 
 * @author Andrey Markelov
 */
@Path("/groupsenabledfieldws")
public class GroupEditService
{
    /**
     * Plugin data.
     */
    private final PluginData data;

    /**
     * Group manager.
     */
    private final GroupManager grMgr;

    /**
     * Logger.
     */
    private final Logger log = Logger.getLogger(GroupEditService.class);

    /**
     * Constructor.
     */
    public GroupEditService(
        PluginData data,
        GroupManager grMgr)
    {
        this.data = data;
        this.grMgr = grMgr;
    }

    @POST
    @Path("/configurefield")
    @Produces({MediaType.APPLICATION_JSON})
    public Response configureField(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("GroupEditService::configureField - User is not logged");
            return Response.ok(i18n.getText("edit-groups-cf.error.notlogged")).status(401).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String atl_token = xsrfTokenGenerator.getToken(req);
        if (!xsrfTokenGenerator.generatedByAuthenticatedUser(atl_token))
        {
            log.error("GroupEditService::configureField - There is no token");
            return Response.ok(i18n.getText("edit-groups-cf.error.internalerror")).status(500).build();
        }

        String[] selUsers = req.getParameterValues("selgroups");
        String cfIdStr = req.getParameter("cfId");
        String uservisible = req.getParameter("uservisible");
        if (cfIdStr == null || cfIdStr.length() == 0)
        {
            log.error("GroupEditService::configureField - Required parameters are not set");
            return Response.ok(i18n.getText("edit-groups-cf.error.internalerror")).status(500).build();
        }

        boolean visibleToOther = (uservisible != null && Boolean.parseBoolean(uservisible)) ? true : false;
        FieldStoreData sfd;
        if (selUsers != null)
        {
            sfd = new FieldStoreData(visibleToOther, Arrays.asList(selUsers));
        }
        else
        {
            sfd = new FieldStoreData(visibleToOther, null);
        }
        data.storeFieldData(cfIdStr, sfd);

        return Response.ok().build();
    }

    @POST
    @Path("/initconfdialog")
    @Produces({MediaType.APPLICATION_JSON})
    public Response initConfigureDialog(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("GroupEditService::initConfigureDialog - User is not logged");
            return Response.ok(i18n.getText("edit-groups-cf.error.notlogged")).status(401).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String atl_token = xsrfTokenGenerator.getToken(req);
        if (!xsrfTokenGenerator.generatedByAuthenticatedUser(atl_token))
        {
            log.error("GroupEditService::initConfigureDialog - There is no token");
            return Response.ok(i18n.getText("edit-groups-cf.error.internalerror")).status(500).build();
        }

        String cfIdStr = req.getParameter("cfId");
        if (cfIdStr == null || cfIdStr.length() == 0)
        {
            log.error("GroupEditService::initConfigureDialog - Required parameters are not set");
            return Response.ok(i18n.getText("edit-groups-cf.error.internalerror")).status(500).build();
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("i18n", authCtx.getI18nHelper());
        params.put("baseUrl", Utils.getBaseUrl(req));
        params.put("groups", grMgr.getAllGroups());
        params.put("atl_token", atl_token);
        params.put("cfId", cfIdStr);
        params.put("storedData", data.getFieldData(cfIdStr));

        try
        {
            String body = ComponentAccessor.getVelocityManager().getBody("templates/", "editgroups_confdialog.vm", params);
            return Response.ok(new HtmlEntity(body)).build();
        }
        catch (VelocityException vex)
        {
            log.error("GroupEditService::initConfigureDialog - Velocity parsing error", vex);
            return Response.ok(i18n.getText("edit-groups-cf.error.internalerror")).status(500).build();
        }
    }
}
