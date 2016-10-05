package com.cybor.studhelper;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import org.joda.time.DateTime;

import java.util.List;

public class MainWidget extends AppWidgetProvider
{
    private static List<Lesson> lessons;

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        appWidgetManager.updateAppWidget(appWidgetIds, updateDisplayData(context));
    }

    RemoteViews updateDisplayData(Context context)
    {
        if (lessons == null)
            lessons = new DBHolder(context).getLessons(DateTime.now().getDayOfWeek());
        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        LessonState state = Lesson.getState(lessons);
        String durationString = "", lessonName = "", beforeName = "";
        Integer number = 0;
        if (state.getState() != LessonState.FREE_TIME)
        {
            Lesson lesson = state.getLesson();
            durationString = Utils.getFormattedDuration(lesson.getTimeToEnd());
            lessonName = lesson.name;
            if (lessonName == null) lessonName = "";
            number = lessons.indexOf(lesson) + 1;
            if (lessons.indexOf(lesson) < lessons.size() - 1)
            {
                String name = lessons.get(number).name;
                beforeName = context.getString(R.string.before) + " " + (name != null ? name : number + 1);
            }
        }
        switch (state.getState())
        {
            case LessonState.FREE_TIME:
                final String freeTime = context.getString(R.string.free_time);
                widget.setTextViewText(R.id.time_tv, freeTime);
                widget.setTextViewText(R.id.info_tv, freeTime);
                break;
            case LessonState.LESSON:
                widget.setTextViewText(R.id.time_tv, context.getString(R.string.lesson_time).replace("TIME", durationString));
                widget.setTextViewText(R.id.info_tv, context.getString(R.string.lesson_info).replace("NUMBER", number.toString()).replace("NAME", lessonName));
                break;
            case LessonState.BREAK:
                widget.setTextViewText(R.id.time_tv, context.getString(R.string.break_time).replace("TIME", durationString));
                widget.setTextViewText(R.id.info_tv, context.getString(R.string.break_info).replace("NUMBER", number.toString()).replace("NAME", lessonName).replace("BEFORE", beforeName));
                break;
        }
        return widget;
    }

}
