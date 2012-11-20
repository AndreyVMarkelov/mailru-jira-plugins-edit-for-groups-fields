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
     * Is visible to other?
     */
    private boolean visibleToOther;

    /**
     * Constructor.
     */
    public FieldStoreData(
        boolean visibleToOther,
        List<String> groups)
    {
        this.visibleToOther = visibleToOther;
        this.groups = groups;
    }

    public List<String> getGroups()
    {
        return groups;
    }

    public boolean isVisibleToOther()
    {
        return visibleToOther;
    }

    @Override
    public String toString()
    {
        return "FieldStoreData[groups=" + groups + ", visibleToOther="
            + visibleToOther + "]";
    }
}
