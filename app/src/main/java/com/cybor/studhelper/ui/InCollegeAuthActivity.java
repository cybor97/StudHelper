package com.cybor.studhelper.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.cybor.studhelper.R;
import com.cybor.studhelper.data.Configuration;
import com.cybor.studhelper.utils.InCollegeAPIUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class InCollegeAuthActivity extends Activity implements View.OnClickListener
{
    private EditText userNameET, passwordET;
    private Executor executor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incollege_auth_layout);

        userNameET = (EditText) findViewById(R.id.username_et);
        passwordET = (EditText) findViewById(R.id.password_et);

        findViewById(R.id.login_button).setOnClickListener(this);

        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.login_button:
                findViewById(R.id.waiting_bar).setVisibility(VISIBLE);
                findViewById(R.id.login_button).setVisibility(GONE);
                final String hostName = Configuration.getInstance().getInCollegeHostName();
                executor.execute(() ->
                {
                    if (InCollegeAPIUtils.authorize(this,
                            hostName,
                            userNameET.getText().toString(),
                            passwordET.getText().toString()))
                        runOnUiThread(() ->
                        {
                            setResult(1);
                            onBackPressed();
                        });
                    else runOnUiThread(() -> setResult(0));
                    runOnUiThread(() ->
                    {
                        findViewById(R.id.waiting_bar).setVisibility(GONE);
                        findViewById(R.id.login_button).setVisibility(VISIBLE);
                    });
                });
                break;
        }
    }
}
