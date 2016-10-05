package com.cybor.studhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    DBHolder dbHolder;
    List<Lesson> lessons;
    Thread displayDataUpdater, groupsListInitializer;
    TextView timeTV, infoTV;
    Spinner groupsSpinner;
    View lessonsButton, changesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        YandexMetrica.activate(getApplicationContext(), getString(R.string.yandex_api_key));
        YandexMetrica.enableActivityAutoTracking(getApplication());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dbHolder = new DBHolder(this);

        timeTV = (TextView) findViewById(R.id.time_tv);
        infoTV = (TextView) findViewById(R.id.info_tv);
        groupsSpinner = (Spinner) findViewById(R.id.groups_spinner);

        lessonsButton = findViewById(R.id.lessons_button);
        changesButton = findViewById(R.id.changes_button);

        lessonsButton.setOnClickListener(this);
        changesButton.setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);

        initDisplayUpdater();
        initGroupsList();
    }

    @Override
    protected void onPause()
    {
        displayDataUpdater.interrupt();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        displayDataUpdater.interrupt();
        super.onDestroy();
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.lessons_button:
                startActivity(new Intent(this, LessonsListImageActivity.class));
                break;
            case R.id.changes_button:
                startActivity(new Intent(this, ReplacesListImageActivity.class));
                break;
            case R.id.settings_button:
                startActivity(new Intent(this, SettingsActivity.class)
                        .putExtra("beginTime", Utils.getFormattedTime(Configuration.getInstance().getLessonsBeginTime()))
                        .putExtra("lessonDuration", Utils.getFormattedDuration(Configuration.getInstance().getLessonDuration(), false)));
                break;
        }
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initDisplayUpdater();
    }

    void initGroupsList()
    {
        groupsListInitializer = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                {
                    try
                    {
                        if (Utils.checkServerConnection(getApplicationContext()))
                        {
                            Document document = Jsoup.connect(getString(R.string.groups_menu_url)).get();
                            Elements data = document.getElementsByAttributeValue("style", "font-size: large;");
                            final ArrayList<Group> groups = new ArrayList<>();
                            for (Element current : data)
                                groups.add(new Group(current.text(), current.parent().parent().attr("href")));

                            if (dbHolder.getGroups() != groups)
                                dbHolder.setGroups(groups);
                        }
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                List<Group> localGroups = dbHolder.getGroups();
                                if (localGroups.size() > 0)
                                {
                                    Configuration config = Configuration.getInstance();
                                    if (config.getGroup() == null)
                                        config.setGroup(localGroups.get(0).name);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
                                    int currentGroupIndex = 0;
                                    for (Group current : localGroups)
                                    {
                                        adapter.add(current.name);
                                        if (current.name.equals(config.getGroup()))
                                            currentGroupIndex = localGroups.indexOf(current);
                                    }
                                    groupsSpinner.setAdapter(adapter);
                                    groupsSpinner.setSelection(currentGroupIndex, true);
                                    groupsSpinner.setOnItemSelectedListener(MainActivity.this);
                                    ((ContentLoadingProgressBar) findViewById(R.id.loading_bar)).hide();
                                    changesButton.setVisibility(View.VISIBLE);
                                    lessonsButton.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } catch (IOException e)
                    {
                    }
                }

            }
        });
        groupsListInitializer.start();
    }

    void initDisplayUpdater()
    {
        Configuration.init(this);
        lessons = dbHolder.getLessons(DateTime.now().getDayOfWeek());

        displayDataUpdater = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!Thread.currentThread().isInterrupted())
                    try
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateDisplayData();
                            }
                        });
                        Thread.sleep(500);
                    } catch (InterruptedException e)
                    {
                        break;
                    }
            }
        });
        displayDataUpdater.start();
    }

    void updateDisplayData()
    {
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
                beforeName = getString(R.string.before) + " " + (name != null ? name : number + 1);
            }
        }
        switch (state.getState())
        {
            case LessonState.FREE_TIME:
                final String freeTime = getString(R.string.free_time);
                timeTV.setText(freeTime);
                infoTV.setText(freeTime);
                break;
            case LessonState.LESSON:
                timeTV.setText(getString(R.string.lesson_time).replace("TIME", durationString));
                infoTV.setText(getString(R.string.lesson_info).replace("NUMBER", number.toString()).replace("NAME", lessonName));
                break;
            case LessonState.BREAK:
                timeTV.setText(getString(R.string.break_time).replace("TIME", durationString));
                infoTV.setText(getString(R.string.break_info).replace("NUMBER", number.toString()).replace("NAME", lessonName).replace("BEFORE", beforeName));
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (((Spinner) view.getParent()).getId() == R.id.groups_spinner)
            if (!((String) groupsSpinner.getSelectedItem()).isEmpty())
            {
                Configuration configuration = Configuration.getInstance();
                configuration.setGroup((String) groupsSpinner.getSelectedItem());
                configuration.setLessonsImageRemoveNeeded(true);
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
