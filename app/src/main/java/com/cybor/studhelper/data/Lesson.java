package com.cybor.studhelper.data;

import android.content.Context;

import com.cybor.studhelper.R;
import com.cybor.studhelper.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class Lesson extends RealmObject
{
    public String name;
    private int id = -1;
    private int weekday;
    private long beginTimeStamp;
    private long breakDurationStamp;

    public Lesson()
    {

    }

    public Lesson(Integer id, String name, int weekday, DateTime beginTime, Duration breakDuration)
    {
        this(name, weekday, beginTime, breakDuration);
        this.id = id;
    }

    public Lesson(String name, int weekday, DateTime beginTime, Duration breakDuration)
    {
        this(weekday, beginTime, breakDuration);
        this.name = name;
    }

    public Lesson(int weekday, DateTime beginTime, Duration breakDuration)
    {
        this.weekday = weekday;
        this.beginTimeStamp = beginTime.getMillis();
        this.breakDurationStamp = breakDuration.getMillis();
    }

    public static LessonState getState(List<Lesson> lessons)
    {
        Lesson[] _lessons = sort(lessons.toArray(new Lesson[lessons.size()]));
        if (_lessons.length == 0) return new LessonState(null, LessonState.FREE_TIME);
        else if (_lessons.length == 1)
            if (_lessons[0].isCurrent()) return new LessonState(_lessons[0], LessonState.LESSON);
            else return new LessonState(null, LessonState.FREE_TIME);
        else for (Lesson current : _lessons)
                if (current.isCurrent())
                    return new LessonState(current, LessonState.LESSON);
                else if (current.isCurrentBreak())
                    return new LessonState(current, LessonState.BREAK);
        return new LessonState(null, LessonState.FREE_TIME);
    }

    public static Lesson[] sort(Lesson[] lessons)
    {
        for (int n = 0; n < lessons.length - 1; n++)
        {
            boolean exchanged = false;
            for (int i = 0; i < lessons.length - n - 1; i++)
                if (lessons[i + 1].getBeginTime().isBefore(lessons[i].getBeginTime()))
                {
                    Lesson tmp = lessons[i + 1];
                    lessons[i + 1] = lessons[i];
                    lessons[i] = tmp;
                    exchanged = true;
                }
            if (!exchanged) break;
        }
        return lessons;
    }

    public static List<Lesson> getDefaultLessons(Context context)
    {
        ArrayList<Lesson> result = new ArrayList<>();
        String[] lessonsData = context.getResources().getStringArray(R.array.default_lessons_time);
        for (int weekday = 1; weekday < 8; weekday++)
            for (String current : lessonsData)
            {
                String[] blocks = current.split(":");
                result.add(new Lesson(weekday,
                        new DateTime(1970, 1, 1,
                                Integer.parseInt(blocks[0]),
                                Integer.parseInt(blocks[1])),
                        blocks[2].equals("LONG_BREAK") ?
                                Configuration.getInstance().getLongBreakDuration() :
                                Configuration.getInstance().getBreakDuration()));
            }
        return result;
    }

    public int getID()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public DateTime getEndTime()
    {
        DateTime beginTime = new DateTime(beginTimeStamp);
        return beginTime.plus(Configuration.getInstance().getLessonDuration());
    }

    public DateTime getBeginTime()
    {
        return new DateTime(beginTimeStamp);
    }

    public Duration getBreakDuration()
    {
        return new Duration(breakDurationStamp);
    }

    public DateTime getBreakEndTime()
    {
        Duration breakDuration = new Duration(breakDurationStamp);
        return getEndTime().plus(breakDuration);
    }

    public Boolean isCurrent()
    {
        DateTime time = Utils.getCurrentTime();
        DateTime beginTime = new DateTime(beginTimeStamp);
        return time.isAfter(beginTime) && time.isBefore(getEndTime());
    }

    public Boolean isCurrentBreak()
    {
        DateTime time = Utils.getCurrentTime();
        return time.isAfter(getEndTime()) && time.isBefore(getEndTime().plus(getBreakDuration()));
    }

    public Duration getTimeToEnd()
    {
        DateTime time = Utils.getCurrentTime();
        if (isCurrent()) return new Duration(time, getEndTime());
        else if (isCurrentBreak()) return new Duration(time, getBreakEndTime());
        else return Duration.ZERO;
    }

}
