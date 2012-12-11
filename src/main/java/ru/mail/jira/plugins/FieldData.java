/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.List;

/**
 * It is data that associated with a field.
 * 
 * @author Andrey Markelov
 */
public class FieldData
{
    /**
     * Is all projects?
     */
    private boolean allProjects;

    /**
     * Custom field ID.
     */
    private String cfId;

    /**
     * Custom field name.
     */
    private String cfName;

    /**
     * Custom field basic type.
     */
    private String cfType;

    /**
     * Associated projects.
     */
    private List<String> fieldProjs;

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
    public FieldData(
        String cfId,
        String cfName)
    {
        this.cfId = cfId;
        this.cfName = cfName;
    }

    public String getCfId()
    {
        return cfId;
    }

    public String getCfName()
    {
        return cfName;
    }

    public String getCfType()
    {
        return cfType;
    }

    public List<String> getFieldProjs()
    {
        return fieldProjs;
    }

    public List<String> getGroups()
    {
        return groups;
    }

    public boolean isAllProjects()
    {
        return allProjects;
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

    public void setAllProjects(boolean allProjects)
    {
        this.allProjects = allProjects;
    }

    public void setCfType(String cfType)
    {
        this.cfType = cfType;
    }

    public void setFieldProjs(List<String> fieldProjs)
    {
        this.fieldProjs = fieldProjs;
    }

    public void setFieldStoreData(FieldStoreData storedData)
    {
        this.visibleToOther = storedData.isVisibleToOther();
        this.visibleToAssigneeOnly = storedData.isVisibleToAssigneeOnly();
        this.visibleToReporterOnly = storedData.isVisibleToReporterOnly();
        this.groups = storedData.getGroups();
    }

    public void setGroups(List<String> groups)
    {
        this.groups = groups;
    }

    public void setVisibleToAssigneeOnly(boolean visibleToAssigneeOnly)
    {
        this.visibleToAssigneeOnly = visibleToAssigneeOnly;
    }

    public void setVisibleToOther(boolean visibleToOther)
    {
        this.visibleToOther = visibleToOther;
    }

    public void setVisibleToReporterOnly(boolean visibleToReporterOnly)
    {
        this.visibleToReporterOnly = visibleToReporterOnly;
    }

    @Override
    public String toString()
    {
        return "FieldData[allProjects=" + allProjects + ", cfId=" + cfId
            + ", cfName=" + cfName + ", cfType=" + cfType + ", fieldProjs="
            + fieldProjs + ", groups=" + groups + ", visibleToAssigneeOnly="
            + visibleToAssigneeOnly + ", visibleToOther=" + visibleToOther
            + ", visibleToReporterOnly=" + visibleToReporterOnly + "]";
    }
}
