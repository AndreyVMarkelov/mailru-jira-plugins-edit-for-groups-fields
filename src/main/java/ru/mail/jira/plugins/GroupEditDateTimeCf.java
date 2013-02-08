/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.Map;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.DateTimeCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;

/**
 * Datetime field.
 * 
 * @author Andrey Markelov
 */
public class GroupEditDateTimeCf
    extends DateTimeCFType
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
     * Constructor.
     */
    public GroupEditDateTimeCf(
        CustomFieldValuePersister customFieldValuePersister,
        GenericConfigManager genericConfigManager,
        JiraAuthenticationContext authenticationContext,
        PluginData data,
        GroupManager grMgr)
    {
        super(customFieldValuePersister, genericConfigManager, authenticationContext);
        this.data = data;
        this.grMgr = grMgr;
    }

    @Override
    public Map<String, Object> getVelocityParameters(
        Issue issue,
        CustomField field,
        FieldLayoutItem fieldLayoutItem)
    {
        FieldStoreData fieldData = data.getFieldData(field.getId());
        User user = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();
        boolean canEdit = false;
        boolean canView = fieldData.isVisibleToOther();
        if (fieldData.getGroups() != null && !fieldData.getGroups().isEmpty())
        {
            for (String group : fieldData.getGroups())
            {
                Group grObj = grMgr.getGroupObject(group);
                if (grObj != null)
                {
                    if (grMgr.isUserInGroup(user, grObj))
                    {
                        canEdit = true;
                        break;
                    }
                }
            }
        }

        if (canEdit)
        {
            canView = true;
        }
        else
        {
            if (issue != null && issue.getKey() != null)
            {
                User aUser = issue.getAssigneeUser();
                User rUser = issue.getReporterUser();

                if ((aUser != null && fieldData.isVisibleToAssigneeOnly() && aUser.equals(user)) ||
                    (rUser != null && fieldData.isVisibleToReporterOnly() && rUser.equals(user)))
                {
                    canEdit = true;
                    canView = true;
                }
            }
            else
            {
                if (fieldData.isVisibleToReporterOnly())
                {
                    canEdit = true;
                    canView = true;
                }
            }
        }

        Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);
        params.put("canEdit", canEdit);
        params.put("canView", canView);
        
        Utils.addViewAndEditParameters(params, field.getId());

        return params;
    }
}
