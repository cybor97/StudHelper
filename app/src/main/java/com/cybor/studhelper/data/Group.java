package com.cybor.studhelper.data;

import io.realm.Realm;
import io.realm.RealmObject;

public class Group extends RealmObject
{
    public String name;
    public String url;

    public Group(String name, String url)
    {
        this.name = name;
        this.url = url;
    }

    public Group()
    {

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        Realm.getDefaultInstance().executeTransaction(transaction -> this.name = name);
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        Realm.getDefaultInstance().executeTransaction(transaction -> this.url = url);
    }
}
