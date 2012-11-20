/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * Implementation <code>PluginData</code>.
 * 
 * @author Andrey Markelov
 */
public class PluginDataImpl
    implements PluginData
{
    /**
     * PlugIn key.
     */
    private static final String PLUGIN_KEY = "MAIL_RU_ENABLED_FOR_GROUPS_FIELDS";

    /**
     * Plug-In settings factory.
     */
    private final PluginSettingsFactory pluginSettingsFactory;

    /**
     * Constructor.
     */
    public PluginDataImpl(
        PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public FieldStoreData getFieldData(String cfId)
    {
        String strData = getStringProperty(cfId + ".data");
        if (strData == null || strData.length() == 0)
        {
            return new FieldStoreData(false, new ArrayList<String>());
        }

        int inx = strData.indexOf("||");
        if (inx > 0)
        {
            String boolStr = strData.substring(0, inx);
            String groupsStr = strData.substring(inx + 2);

            return new FieldStoreData(Boolean.parseBoolean(boolStr), strToList(groupsStr));
        }
        else
        {
            return new FieldStoreData(false, new ArrayList<String>());
        }
    }

    private String getStringProperty(String key)
    {
        return (String) pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).get(key);
    }

    private String listToString(List<String> list)
    {
        StringBuilder sb = new StringBuilder();

        if (list != null)
        {
            for (String item : list)
            {
                sb.append(item).append("&");
            }
        }

        return sb.toString();
    }

    private void setStringProperty(String key, String value)
    {
        pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).put(key, value);
    }

    @Override
    public void storeFieldData(
        String cfId,
        FieldStoreData data)
    {
        setStringProperty(cfId + ".data", data.isVisibleToOther() + "||" + listToString(data.getGroups()));
    }

    private List<String> strToList(String str)
    {
        List<String> list = new ArrayList<String>();

        if (str == null || str.length() == 0)
        {
            return list;
        }

        StringTokenizer st = new StringTokenizer(str, "&");
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }

        return list;
    }
}
