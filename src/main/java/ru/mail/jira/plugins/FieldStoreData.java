/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.List;

/**
 * It is database data that associated with a field.
 * 
 * @author Andrey Markelov
 */
public class FieldStoreData
{
    /**
     * Groups.
     */
    private List<String> groups;

    /**
     * Is visible to assignee only?
     */
    private boolean visibleToAssigneeOnly;

    /**
     * Is visible to other?
     */
    private boolean visibleToOther;

    /**
     * Is visible to reporter only?
     */
    private boolean visibleToReporterOnly;

    /**
     * Constructor.
     */
    public FieldStoreData(
        boolean visibleToOther,
        boolean visibleToAssigneeOnly,
        boolean visibleToReporterOnly,
        List<String> groups)
    {
        this.visibleToOther = visibleToOther;
        this.visibleToAssigneeOnly = visibleToAssigneeOnly;
        this.visibleToReporterOnly = visibleToReporterOnly;
        this.groups = groups;
    }

    public List<String> getGroups()
    {
        return groups;
    }

    public boolean isVisibleToAssigneeOnly()
    {
        return visibleToAssigneeOnly;
    }

    public boolean isVisibleToOther()
    {
        return visibleToOther;
    }

    public boolean isVisibleToReporterOnly()
    {
        return visibleToReporterOnly;
    }

    @Override
    public String toString()
    {
        return "FieldStoreData[groups=" + groups + ", visibleToOther="
           + visibleToOther + ", visibleToReporterOnly=" + visibleToReporterOnly + ", visibleToAssigneeOnly="
           + visibleToAssigneeOnly + "]";
    }
}
