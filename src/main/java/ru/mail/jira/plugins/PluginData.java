/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

/**
 * Interface for working with Plug-In data.
 * 
 * @author Andrey Markelov
 */
public interface PluginData
{
    /**
     * Get data associated with field.
     */
    FieldStoreData getFieldData(String cfId);

    /**
     * Store data associated with field.
     */
    void storeFieldData(String cfId, FieldStoreData data);
}
