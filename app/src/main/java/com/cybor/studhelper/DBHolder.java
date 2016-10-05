package com.cybor.studhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

class DBHolder extends SQLiteOpenHelper
{
    private Context _context;

    public DBHolder(Context context)
    {
        super(context, "StudHelperDB", null, 1);
        _context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(_context.getString(R.string.configuration_table_create_string));
        db.execSQL(_context.getString(R.string.lessons_table_create_string));
        db.execSQL(_context.getString(R.string.groups_table_create_string));
        ContentValues values = new ContentValues();
        values.put("ID", 0);
        values.put("LessonsBeginTime", new DateTime(1970, 1, 1, 8, 30).getMillis());
        values.put("LessonDuration", 90 * 60 * 1000);
        db.insert("Configuration", null, values);

        for (Lesson current :
                Lesson.getDefaultLessons(_context))
            addLesson(db, current);
    }

    public List<Lesson> getLessons(int weekday)
    {
        ArrayList<Lesson> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("Lessons", null, "Weekday = " + weekday, null, null, null, null);
        if (cursor.moveToFirst())
        {
            int idColumnIndex = cursor.getColumnIndex("ID"),//DO NOT try to optimize! It's optimal.
                    nameColumnIndex = cursor.getColumnIndex("Name"),//We REALLY don't need to call it in loop.
                    weekdayColumnIndex = cursor.getColumnIndex("Weekday"),
                    breakDurationColumnIndex = cursor.getColumnIndex("BreakDuration");
            do
            {
                DateTime lessonBeginTime = result.size() == 0 ? Configuration.getInstance().getLessonsBeginTime() :
                        result.get(result.size() - 1).getBreakEndTime();
                result.add(new Lesson(
                        cursor.getInt(idColumnIndex),
                        cursor.getString(nameColumnIndex),
                        cursor.getInt(weekdayColumnIndex),
                        lessonBeginTime,
                        new Duration(cursor.getLong(breakDurationColumnIndex))));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public void addLesson(Lesson lesson)
    {
        addLesson(getWritableDatabase(), lesson);
    }

    public void addLesson(SQLiteDatabase db, Lesson lesson)
    {
        ContentValues values = new ContentValues();
        values.put("Name", lesson.name);
        values.put("Weekday", lesson.weekday);
        values.put("BreakDuration", lesson.breakDuration.getMillis());
        db.insert("Lessons", null, values);
    }

    public void setLesson(Lesson lesson)
    {
        if (lesson.getID() != -1)
        {
            ContentValues values = new ContentValues();
            values.put("ID", lesson.getID());
            values.put("Name", lesson.name);
            values.put("Weekday", lesson.weekday);
            values.put("BreakDuration", lesson.breakDuration.getMillis());
            getWritableDatabase().update("Lessons", values, "ID=" + lesson.getID(), null);
        }
    }

    public void initConfiguration()
    {
        Cursor cursor = getReadableDatabase().query("Configuration", null, null, null, null, null, null);
        cursor.moveToFirst();
        Configuration configuration = Configuration.getInstance();
        configuration.setLessonsBeginTime(new DateTime(cursor.getLong(cursor.getColumnIndex("LessonsBeginTime"))));
        configuration.setLessonDuration(new Duration(cursor.getLong(cursor.getColumnIndex("LessonDuration"))));
        configuration.setGroup(cursor.getString(cursor.getColumnIndex("AcademicGroup")));
        configuration.setLessonsImageRemoveNeeded(cursor.getInt(cursor.getColumnIndex("LessonsImageRemoveNeeded")) == 1);
        cursor.close();
    }

    public void setConfiguration(Configuration configuration)
    {
        ContentValues values = new ContentValues();
        values.put("ID", 0);
        values.put("LessonsBeginTime", configuration.getLessonsBeginTime().getMillis());
        values.put("LessonDuration", configuration.getLessonDuration().getMillis());
        values.put("AcademicGroup", configuration.getGroup());
        values.put("LessonsImageRemoveNeeded", configuration.isLessonsImageRemoveNeeded());
        getWritableDatabase().update("Configuration", values, "ID=" + 0, null);
    }

    public void setGroups(List<Group> groups)
    {
        getWritableDatabase().delete("Groups", null, null);
        for (Group current : groups)
        {
            ContentValues values = new ContentValues();
            values.put("ID", groups.indexOf(current));
            values.put("Name", current.name);
            values.put("URL", current.url);
            getWritableDatabase().insert("Groups", null, values);
        }
    }

    public List<Group> getGroups()
    {
        ArrayList<Group> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("Groups", null, null, null, null, null, null);
        if (cursor.moveToFirst())
        {
            int groupNameColumnIndex = cursor.getColumnIndex("Name");
            int groupURLColumnIndex = cursor.getColumnIndex("URL");
            do
                result.add(new Group(cursor.getString(groupNameColumnIndex), cursor.getString(groupURLColumnIndex)));
            while (cursor.moveToNext());
        }
        cursor.close();
        return result;

    }


    public void removeLesson(Lesson lesson)
    {
        if (lesson.getID() != -1)
            getWritableDatabase().delete("Lessons", "ID=" + lesson.getID(), null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
