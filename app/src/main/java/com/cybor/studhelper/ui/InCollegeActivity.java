package com.cybor.studhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.cybor.studhelper.R;
import com.cybor.studhelper.data.Configuration;


public class InCollegeActivity extends Activity implements View.OnClickListener
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incollege_layout);

        findViewById(R.id.marks_button).setOnClickListener(this);
        findViewById(R.id.chat_button).setOnClickListener(this);

        if (Configuration.getInstance().getToken().isEmpty())
            startActivityForResult(new Intent(this, InCollegeAuthActivity.class), 0);
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.marks_button:
                startActivity(new Intent(this, MarksActivity.class));
                break;
            case R.id.back_button:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_back_leave);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0 && resultCode != 1)
            onBackPressed();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
