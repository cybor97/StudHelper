package com.cybor.studhelper;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Configuration
{
    private static Configuration instance = new Configuration();

    public static Configuration getInstance()
    {
        return instance;
    }

    private static DBHolder holder;

    private DateTime lessonsBeginTime;

    private Duration lessonDuration;

    private boolean lessonsImageRemoveNeeded;

    private String group;

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
        if (holder != null) holder.setConfiguration(this);
    }

    public void setLessonsImageRemoveNeeded(boolean lessonsImageRemoveNeeded)
    {
        this.lessonsImageRemoveNeeded = lessonsImageRemoveNeeded;
        if (holder != null) holder.setConfiguration(this);
    }

    public boolean isLessonsImageRemoveNeeded()
    {
        return lessonsImageRemoveNeeded;
    }

    public Duration getLessonDuration()
    {
        return lessonDuration;
    }

    public DateTime getLessonsBeginTime()
    {
        return lessonsBeginTime;
    }

    public static void init(Context context)
    {
        DBHolder _holder = new DBHolder(context);
        _holder.initConfiguration();
        holder = _holder;
    }

    public void setLessonDuration(Duration lessonDuration)
    {
        this.lessonDuration = lessonDuration;
        if (holder != null) holder.setConfiguration(this);
    }

    public void setLessonsBeginTime(DateTime lessonBeginTime)
    {
        this.lessonsBeginTime = lessonBeginTime;
        if (holder != null) holder.setConfiguration(this);
    }

    private Configuration()
    {
    }
}
