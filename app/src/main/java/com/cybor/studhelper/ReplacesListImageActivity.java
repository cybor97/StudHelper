package com.cybor.studhelper;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class ReplacesListImageActivity extends Activity implements Runnable, View.OnClickListener
{//TODO:Implement
    Thread replacesImageInitilizer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replaces_image_layout);
        findViewById(R.id.back_button).setOnClickListener(this);
        replacesImageInitilizer = new Thread(this);
        replacesImageInitilizer.start();
    }

    @Override
    public void run()
    {
        final File replacesImageFile = Common.getReplacesImageFile(this);
        final Configuration configuration = Configuration.getInstance();
        while (!Thread.interrupted() &&//Die fast if user wants it!
                (!replacesImageFile.exists() ||
                        new Duration(new DateTime(replacesImageFile.lastModified()), DateTime.now()).getStandardDays() > 1) &&//Reasons to download file
                Utils.checkServerConnection(this)) //But only if has server connection
        {
            String group = Configuration.getInstance().getGroup();
            if (group != null)
                try
                {
                    Document document = Jsoup.connect(getString(R.string.replaces_list_url)).get();
                    Utils.downloadFile(document.getElementsByClass("aligncenter").attr("src"), replacesImageFile.getPath());
                    configuration.setLessonsImageRemoveNeeded(false);
                } catch (IOException e)
                {
                    Log.e("GetLessonsImageFile", e.toString());
                }
        }
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ImageView replacesIV = (ImageView) findViewById(R.id.replaces_iv);
                replacesIV.setImageURI(Uri.fromFile(replacesImageFile));
                replacesIV.setOnTouchListener(new OnTouch_Scale());
                ((ContentLoadingProgressBar) findViewById(R.id.loading_bar)).hide();
            }
        });
    }

    @Override
    protected void onPause()
    {
        replacesImageInitilizer.interrupt();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        replacesImageInitilizer.interrupt();
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_back_leave);
    }

}
