package com.cybor.studhelper.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.cybor.studhelper.R;
import com.cybor.studhelper.data.Configuration;
import com.cybor.studhelper.data.Lesson;
import com.cybor.studhelper.data.LessonState;
import com.cybor.studhelper.utils.Utils;

import org.joda.time.DateTime;

import java.util.List;

import io.realm.Realm;

public class MainWidget extends AppWidgetProvider
{
    private static List<Lesson> lessons;
    private static Thread updater;
    private static RemoteViews widget;

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (updater == null || !updater.isAlive())
        {
            updater = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (!Thread.interrupted())
                        try
                        {
                            appWidgetManager.updateAppWidget(appWidgetIds, updateDisplayData(context));
                            Thread.sleep(1000);
                        } catch (InterruptedException e)
                        {
                            Log.i("Interrupt", "Interrupted exception received. Finishing...");
                            break;
                        }
                }
            });
            updater.start();
        }
    }

    RemoteViews updateDisplayData(final Context context)
    {
        if (widget == null)
            widget = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        Configuration.init(context);
        if (lessons == null)
            lessons = Realm.getDefaultInstance()
                    .where(Lesson.class)
                    .equalTo("weekday", DateTime.now().getDayOfWeek())
                    .findAll();
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
