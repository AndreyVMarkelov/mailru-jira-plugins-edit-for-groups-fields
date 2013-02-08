/*
 * Created by Andrey Markelov 19-11-2012. Copyright Mail.Ru Group 2012. All
 * rights reserved.
 */
package ru.mail.jira.plugins;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.UserProjectHistoryManager;
import com.atlassian.plugin.PluginAccessor;


/**
 * This class contains utility static methods.
 * 
 * @author Andrey Markelov
 */
public class Utils
{
    private static final String CF_RIGHTS_CLASS_NAME = "ru.mail.jira.plugins.settings.IMailRuCFRights";
    private static final String CF_RIGHTS_METHOD_CAN_EDIT_NAME = "canEdit";
    private static final String CF_RIGHTS_METHOD_CAN_VIEW_NAME = "canView";

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    private static Object cfRightsInstance;

    /**
     * Private constructor.
     */
    private Utils()
    {
    }

    /**
     * Get base URL from HTTP request.
     */
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":"
            + req.getServerPort() + req.getContextPath());
    }

    private static Object getCfRightsClass()
    {
        if (cfRightsInstance == null)
        {
            PluginAccessor pluginAccessor = ComponentManager.getInstance()
                .getPluginAccessor();
            Class<?> mailRuCfRightsClass;
            try
            {
                mailRuCfRightsClass = pluginAccessor.getClassLoader()
                    .loadClass(CF_RIGHTS_CLASS_NAME);
            }
            catch (ClassNotFoundException e)
            {
                log.info("Utils::getCfRightsClass - ClassNotfoundException "
                    + CF_RIGHTS_CLASS_NAME
                    + " not found. It is possible that plugin is turned off");
                return null;
            }
            cfRightsInstance = ComponentManager
                .getOSGiComponentInstanceOfType(mailRuCfRightsClass);
            if (cfRightsInstance == null)
            {
                log.info("Utils::getCfRightsClass - Class "
                    + CF_RIGHTS_CLASS_NAME
                    + ". Method getOSGiComponentInstanceOfType failed to load component");
            }
        }
        return cfRightsInstance;
    }

    private static boolean canEditCF(User user, String cfId, Project project)
    {
        return getPermission(user, cfId, project,
            CF_RIGHTS_METHOD_CAN_EDIT_NAME, "canEditCF");
    }

    private static boolean canViewCF(User user, String cfId, Project project)
    {
        return getPermission(user, cfId, project,
            CF_RIGHTS_METHOD_CAN_VIEW_NAME, "canViewCF");
    }

    /**
     * adds "canView" and "canEdit" keys to map
     */
    public static void addViewAndEditParameters(Map<String, Object> params,
        String cfId)
    {
        boolean canEdit = (Boolean) params.get("canEdit");
        boolean canView = (Boolean) params.get("canView");
        
        // standard instrumentary has priority agains external plugin
        if (!canEdit || !canView)
        {
            if (canEdit)
            {
                params.put("canView", true);
            }
            else
            {
                UserProjectHistoryManager userProjectHistoryManager = ComponentManager
                    .getComponentInstanceOfType(UserProjectHistoryManager.class);
                JiraAuthenticationContext authCtx = ComponentManager
                    .getInstance().getJiraAuthenticationContext();
                User currentUser = authCtx.getLoggedInUser();
                Project currentProject = userProjectHistoryManager
                    .getCurrentProject(Permissions.BROWSE, currentUser);

                boolean canEditExternal = Utils.canEditCF(currentUser,
                    cfId, currentProject);
                params.put("canEdit", canEditExternal);
                if (canEditExternal)
                {
                    params.put("canView", true);
                }
                else
                {
                    if (!canView)
                    {
                        params.put("canView", Utils.canViewCF(currentUser,
                            cfId, currentProject));
                    }
                }

            }
        }
    }

    private static boolean getPermission(User user, String cfId,
        Project project, String externalMethodName, String internalMethodName)
    {
        Object cfRights = getCfRightsClass();

        // occurs only if class not found or not load
        // it's possible that parent plugin was disabled manually
        // so we should return true
        if (cfRights == null)
        {
            return true;
        }

        Method[] methods = cfRights.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (externalMethodName.equals(methods[i].getName()))
            {
                Boolean result = Boolean.FALSE;
                try
                {
                    result = (Boolean) methods[i].invoke(cfRights, user, cfId,
                        project);
                }
                catch (IllegalArgumentException e)
                {
                    log.error(getErrorMessage(user, cfId, project,
                        internalMethodName, externalMethodName,
                        "IllegalArgumentException"));
                }
                catch (IllegalAccessException e)
                {
                    log.error(getErrorMessage(user, cfId, project,
                        internalMethodName, externalMethodName,
                        "IllegalAccessException"));
                }
                catch (InvocationTargetException e)
                {
                    log.error(getErrorMessage(user, cfId, project,
                        internalMethodName, externalMethodName,
                        "InvocationTargetException"));
                    cfRightsInstance = null; // set instance to null it's
                                             // possible that class was disabled
                }

                return result;
            }
        }
        return false;
    }

    private static String getErrorMessage(User user, String cfId,
        Project project, String internalMethodName, String externalMethodName,
        String exception)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Utils::");
        sb.append(internalMethodName);
        sb.append(" - Class ");
        sb.append(CF_RIGHTS_CLASS_NAME);
        sb.append(". ");
        sb.append(exception);
        sb.append(" occured invoking ");
        sb.append(externalMethodName);
        sb.append(" method ");

        return sb.toString();
    }
}
