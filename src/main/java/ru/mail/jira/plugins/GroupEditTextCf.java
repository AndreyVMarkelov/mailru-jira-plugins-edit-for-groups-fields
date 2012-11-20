/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.Map;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

/**
 * Text field.
 * 
 * @author Andrey Markelov
 */
public class GroupEditTextCf
    extends TextCFType
{
    /**
     * Plugin data.
     */
    private final PluginData data;

    /**
     * Constructor.
     */
    public GroupEditTextCf(
        CustomFieldValuePersister customFieldValuePersister,
        GenericConfigManager genericConfigManager,
        PluginData data)
    {
        super(customFieldValuePersister, genericConfigManager);
        this.data = data;
    }

    @Override
    public Map<String, Object> getVelocityParameters(
        Issue issue,
        CustomField field,
        FieldLayoutItem fieldLayoutItem)
    {
        return super.getVelocityParameters(issue, field, fieldLayoutItem);
    }
}
