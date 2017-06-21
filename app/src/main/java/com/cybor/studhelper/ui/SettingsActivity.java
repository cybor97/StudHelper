package com.cybor.studhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cybor.studhelper.R;
import com.cybor.studhelper.data.Configuration;
import com.cybor.studhelper.utils.Utils;

public class SettingsActivity extends Activity implements View.OnClickListener
{
    TextView lessonsBeginTimeET, lessonDurationET,
            breakDurationET, longBreakDurationET,
            incollege_server_hostnameET;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        findViewById(R.id.back_button).setOnClickListener(this);

        Intent intent = getIntent();

        lessonsBeginTimeET = (TextView) findViewById(R.id.lessons_begin_time_et);
        lessonDurationET = (TextView) findViewById(R.id.lesson_duration_et);
        lessonsBeginTimeET.setText(intent.getStringExtra("beginTime"));
        lessonDurationET.setText(intent.getStringExtra("lessonDuration"));

        breakDurationET = (TextView) findViewById(R.id.break_duration_et);
        longBreakDurationET = (TextView) findViewById(R.id.long_break_duration_et);
        breakDurationET.setText(intent.getStringExtra("breakDuration"));
        longBreakDurationET.setText(intent.getStringExtra("longBreakDuration"));

        incollege_server_hostnameET = (TextView) findViewById(R.id.incollege_server_hostname_et);
        incollege_server_hostnameET.setText(intent.getStringExtra("inCollegeServerHostname"));
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
            Configuration.getInstance().setLessonsBeginTime(Utils.parseTime(lessonsBeginTimeET.getText().toString()));
        if (lessonDurationET.getText().length() > 0)
            Configuration.getInstance().setLessonDuration(Utils.parseDuration(lessonDurationET.getText().toString()));
        Configuration.getInstance().setInCollegeHostName(incollege_server_hostnameET.getText().toString());

        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_back_leave);
    }
}
