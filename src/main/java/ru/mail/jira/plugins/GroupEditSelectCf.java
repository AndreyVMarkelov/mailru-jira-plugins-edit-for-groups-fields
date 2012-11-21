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
import com.atlassian.jira.issue.customfields.converters.SelectConverter;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.security.groups.GroupManager;

/**
 * Select field.
 * 
 * @author Andrey Markelov
 */
public class GroupEditSelectCf
    extends SelectCFType
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
    public GroupEditSelectCf(
        CustomFieldValuePersister customFieldValuePersister,
        StringConverter stringConverter,
        SelectConverter selectConverter,
        OptionsManager optionsManager,
        GenericConfigManager genericConfigManager,
        JiraBaseUrls jiraBaseUrls,
        PluginData data,
        GroupManager grMgr)
    {
        super(
            customFieldValuePersister,
            stringConverter,
            selectConverter,
            optionsManager,
            genericConfigManager,
            jiraBaseUrls);
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
        boolean canEdit = false;
        boolean canView = fieldData.isVisibleToOther();
        if (fieldData.getGroups() != null && !fieldData.getGroups().isEmpty())
        {
            User user = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();
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

        Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);
        params.put("canEdit", canEdit);
        params.put("canView", canView);

        return params;
    }
}
