/*
 * Created by Andrey Markelov 19-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import javax.servlet.http.HttpServletRequest;

/**
 * This class contains utility static methods.
 * 
 * @author Andrey Markelov
 */
public class Utils
{
    /**
     * Private constructor.
     */
    private Utils() {}

    /**
     * Get base URL from HTTP request.
     */
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath());
    }
}
