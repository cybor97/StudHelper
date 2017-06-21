package com.cybor.studhelper.data;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;

public class Mark extends RealmObject
{
    public static final byte ABSENT = -1,
            PASSED = -2,
            UNPASSED = -3,
            BLANK = -4;

    @SerializedName("MarkValue")
    private byte markValue;
    @SerializedName("TicketNumber")
    private byte ticketNumber;
    @SerializedName("StatementResultDate")
    private Date date;
    @SerializedName("SubjectName_STUDENT_MODE")
    private String subjectName;


    public byte getMarkValue()
    {
        return markValue;
    }

    public void setMarkValue(byte markValue)
    {
        Realm.getDefaultInstance().executeTransaction(realm -> this.markValue = markValue);
    }

    public byte getTicketNumber()
    {
        return ticketNumber;
    }

    public void setTicketNumber(byte ticketNumber)
    {
        Realm.getDefaultInstance().executeTransaction(realm -> this.ticketNumber = ticketNumber);
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        Realm.getDefaultInstance().executeTransaction(realm -> this.date = date);
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        Realm.getDefaultInstance().executeTransaction(realm -> this.subjectName = subjectName);
    }

    public String getMarkValueString()
    {
        switch (markValue)
        {
            case ABSENT:
                return "Н/Б";
            case PASSED:
                return "[З]";
            case UNPASSED:
                return "Н/А";
            case BLANK:
                return " ";
            default:
                return Byte.toString(markValue);
        }
    }
}
