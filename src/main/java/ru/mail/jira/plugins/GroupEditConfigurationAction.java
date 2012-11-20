/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ofbiz.core.entity.GenericValue;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;

/**
 * Administration page action.
 * 
 * @author Andrey Markelov
 */
public class GroupEditConfigurationAction
    extends JiraWebActionSupport
{
    /**
     * Unique ID.
     */
    private static final long serialVersionUID = -3592550109976082140L;

    /**
     * Application properties.
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Custom field manager.
     */
    private final CustomFieldManager cfMgr;

    /**
     * Plugin data.
     */
    private final PluginData data;

    /**
     * Fields.
     */
    private Map<String, FieldData> fields;

    /**
     * Constructor.
     */
    public GroupEditConfigurationAction(
        ApplicationProperties applicationProperties,
        CustomFieldManager cfMgr,
        PluginData data)
    {
        this.applicationProperties = applicationProperties;
        this.cfMgr = cfMgr;
        this.data = data;
        this.fields = new LinkedHashMap<String, FieldData>();
    }

    @Override
    public String doDefault()
    throws Exception
    {
        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList)
        {
            if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-text") ||
                cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-number") ||
                cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-user") ||
                cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-date") ||
                cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-datetime") ||
                cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-select"))
            {
                FieldData fData = new FieldData(cf.getId(), cf.getName());

                if (cf.isAllProjects())
                {
                    fData.setAllProjects(true);
                }
                else
                {
                    fData.setAllProjects(false);
                    List<String> fieldProjs = new ArrayList<String>();
                    List<GenericValue> projs = cf.getAssociatedProjects();
                    for (GenericValue proj : projs)
                    {
                        fieldProjs.add((String) proj.get("name"));
                    }

                    fData.setFieldProjs(fieldProjs);
                }

                if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-text"))
                {
                    fData.setCfType("edit-groups-cf.basictext");
                }
                else if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-number"))
                {
                    fData.setCfType("edit-groups-cf.basicnumber");
                }
                else if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-user"))
                {
                    fData.setCfType("edit-groups-cf.basicuser");
                }
                else if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-date"))
                {
                    fData.setCfType("edit-groups-cf.basicdate");
                }
                else if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-datetime"))
                {
                    fData.setCfType("edit-groups-cf.basicdatetime");
                }
                else if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.groupeditfiedls:edit-groups-cf-select"))
                {
                    fData.setCfType("edit-groups-cf.basicselect");
                }

                fData.setFieldStoreData(data.getFieldData(cf.getId()));

                fields.put(cf.getId(), fData);
            }
        }

        return SUCCESS;
    }

    /**
     * Get context path.
     */
    public String getBaseUrl()
    {
        return applicationProperties.getBaseUrl();
    }

    public Map<String, FieldData> getFields()
    {
        return fields;
    }

    /**
     * Check administer permissions.
     */
    public boolean hasAdminPermission()
    {
        User user = getLoggedInUser();
        if (user == null)
        {
            return false;
        }

        if (getPermissionManager().hasPermission(Permissions.ADMINISTER, getLoggedInUser()))
        {
            return true;
        }

        return false;
    }
}
