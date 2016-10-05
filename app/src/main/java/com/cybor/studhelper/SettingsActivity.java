package com.cybor.studhelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class SettingsActivity extends Activity implements View.OnClickListener
{
    TextView lessonsBeginTimeET, lessonDurationET;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        findViewById(R.id.back_button).setOnClickListener(this);
        lessonsBeginTimeET = (TextView) findViewById(R.id.lessons_begin_time_et);
        lessonDurationET = (TextView) findViewById(R.id.lesson_duration_et);
        lessonsBeginTimeET.setText(getIntent().getStringExtra("beginTime"));
        lessonDurationET.setText(getIntent().getStringExtra("lessonDuration"));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_button:
                onBackPressed();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (lessonsBeginTimeET.getText().length() > 0)
        {
            String[] data = lessonsBeginTimeET.getText().toString().split(":");
            Configuration.getInstance().setLessonsBeginTime(new DateTime(1970, 1, 1, Integer.parseInt(data[0]), Integer.parseInt(data[1])));
        }
        if (lessonDurationET.getText().length() > 0)
        {
            String[] data = lessonDurationET.getText().toString().split(":");
            Configuration.getInstance().setLessonDuration(new Duration((Integer.parseInt(data[0]) * 60 + Integer.parseInt(data[1])) * 60 * 1000));
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_back_leave);
    }
}
