package com.cybor.studhelper.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.cybor.studhelper.R;
import com.cybor.studhelper.data.Configuration;
import com.cybor.studhelper.data.Mark;
import com.cybor.studhelper.utils.InCollegeAPIUtils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MarksActivity extends Activity implements View.OnClickListener
{
    private Executor executor;
    private View waitingBar;
    private Configuration configuration;
    private ListView marksLV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_layout);

        findViewById(R.id.back_button).setOnClickListener(this);

        executor = Executors.newSingleThreadExecutor();
        waitingBar = findViewById(R.id.waiting_bar);
        configuration = Configuration.getInstance();
        marksLV = (ListView) findViewById(R.id.marks_lv);

        updateData();
    }

    private void updateData()
    {
        waitingBar.setVisibility(VISIBLE);

        final String hostName = configuration.getInCollegeHostName();
        final String token = configuration.getToken();
        executor.execute(() ->
        {
            final List<Mark> marks = InCollegeAPIUtils.getMarks(this, hostName, token);
            if (marks != null)
                runOnUiThread(() -> marksLV.setAdapter(new MarksAdapter(this, marks.toArray(new Mark[0]))));
            runOnUiThread(() -> waitingBar.setVisibility(GONE));
        });
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.back_button)
            onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_back_leave);
    }
}