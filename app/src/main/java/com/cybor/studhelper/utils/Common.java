package com.cybor.studhelper.utils;

import android.content.Context;

import java.io.File;

public class Common
{
    public static File getLessonsImageFile(Context context)
    {
        return new File(context.getCacheDir().getPath() + "/lessons.jpg");
    }

    public static File getReplacesImageFile(Context context)
    {
        return new File(context.getCacheDir().getPath() + "/replaces.jpg");
    }
}
