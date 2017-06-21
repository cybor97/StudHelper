package com.cybor.studhelper.data;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Configuration extends RealmObject
{
    private static Configuration instance = new Configuration();
    private static Realm realm;
    @PrimaryKey
    public int id = 1;
    public long lessonsBeginTime;

    private long lessonDuration;

    private long breakDuration = Duration.standardMinutes(10).getMillis();//TODO:Implement

    private long longBreakDuration = Duration.standardMinutes(10).getMillis();//TODO:Implement

    private boolean lessonsImageRemoveNeeded;

    private String group;

    private String inCollegeHostName = "192.168.1.1";

    private String token = "";

    public Configuration()
    {
    }

    public static Configuration getInstance()
    {
        return instance;
    }

    public static void init(Context context)
    {
        if ((realm = Realm.getDefaultInstance())
                .where(Configuration.class)
                .count() == 0)
            realm.executeTransaction(transaction -> realm.copyToRealm(new Configuration()));
        else
            Configuration.instance = realm.where(Configuration.class).findFirst();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        realm.executeTransaction(transaction -> this.group = group);
        if (realm != null) realm.copyToRealm(this);
    }

    public boolean isLessonsImageRemoveNeeded()
    {
        return lessonsImageRemoveNeeded;
    }

    public void setLessonsImageRemoveNeeded(boolean lessonsImageRemoveNeeded)
    {
        realm.executeTransaction(transaction -> this.lessonsImageRemoveNeeded = lessonsImageRemoveNeeded);
        if (realm != null) realm.copyToRealm(this);
    }

    public Duration getLessonDuration()
    {
        return new Duration(lessonDuration);
    }

    public void setLessonDuration(Duration lessonDuration)
    {
        realm.executeTransaction(transaction -> this.lessonDuration = lessonDuration.getMillis());
        if (realm != null) realm.copyToRealm(this);
    }

    public DateTime getLessonsBeginTime()
    {
        return new DateTime(lessonsBeginTime);
    }

    public void setLessonsBeginTime(DateTime lessonBeginTime)
    {
        realm.executeTransaction(transaction -> this.lessonsBeginTime = lessonBeginTime.getMillis());
        if (realm != null) realm.copyToRealm(this);
    }

    public Duration getBreakDuration()
    {
        return new Duration(breakDuration);
    }

    public void setBreakDuration(Duration breakDuration)
    {
        realm.executeTransaction(transaction -> this.breakDuration = breakDuration.getMillis());
    }

    public Duration getLongBreakDuration()
    {
        return new Duration(longBreakDuration);
    }

    public void setLongBreakDuration(Duration longBreakDuration)
    {
        realm.executeTransaction(transaction -> this.longBreakDuration = longBreakDuration.getMillis());
    }

    public String getInCollegeHostName()
    {
        return inCollegeHostName;
    }

    public void setInCollegeHostName(String inCollegeHostName)
    {
        Realm.getDefaultInstance().executeTransaction(realm -> this.inCollegeHostName = inCollegeHostName);
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        Realm.getDefaultInstance().executeTransaction(realm -> this.token = token);
    }
}
