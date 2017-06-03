package com.cybor.studhelper.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.cybor.studhelper.R;
import com.cybor.studhelper.data.Configuration;
import com.cybor.studhelper.utils.Common;
import com.cybor.studhelper.utils.OnTouch_Scale;
import com.cybor.studhelper.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class LessonsListImageActivity extends Activity implements Runnable, View.OnClickListener
{
    Thread lessonsImageInitilizer;
    String group;
    private boolean lessonsRemoveNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lessons_image_layout);
        findViewById(R.id.back_button).setOnClickListener(this);
        lessonsImageInitilizer = new Thread(this);
        lessonsImageInitilizer.start();
    }

    @Override
    public void run()
    {
        final File lessonsImageFile = Common.getLessonsImageFile(this);
        final Configuration configuration = Configuration.getInstance();
        while (!Thread.interrupted() &&//Die fast if user wants it!
                (!lessonsImageFile.exists() ||
                        new Duration(new DateTime(lessonsImageFile.lastModified()), DateTime.now()).getStandardDays() > 1 ||
                        isLessonsImageRemoveNeeded(configuration)) &&//Reasons to download file
                Utils.checkServerConnection(this)) //But only if has server connection
        {
            runOnUiThread(() -> group = Configuration.getInstance().getGroup());
            if (group != null)
                try
                {
                    Document document = Jsoup.connect(getString(R.string.groups_menu_url)).get();
                    Elements data = document.getElementsByAttributeValue("style", "font-size: large;");
                    String groupURL = null;
                    for (Element current : data)
                        if (current.text().equals(group))
                            groupURL = current.parent().parent().attr("href");
                    if (groupURL != null)
                    {
                        document = Jsoup.connect(groupURL).get();
                        Utils.downloadFile(document.getElementsByClass("aligncenter").attr("src"), lessonsImageFile.getPath());
                        runOnUiThread(() -> configuration.setLessonsImageRemoveNeeded(false));
                    }
                } catch (IOException e)
                {
                    Log.e("GetLessonsImageFile", e.toString());
                }
        }
        runOnUiThread(() ->
        {
            ImageView lessonsIV = (ImageView) findViewById(R.id.lessons_iv);
            lessonsIV.setImageURI(Uri.fromFile(lessonsImageFile));
            lessonsIV.setOnTouchListener(new OnTouch_Scale());
            ((ContentLoadingProgressBar) findViewById(R.id.loading_bar)).hide();
        });
    }

    @Override
    protected void onPause()
    {
        lessonsImageInitilizer.interrupt();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        lessonsImageInitilizer.interrupt();
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

    public boolean isLessonsImageRemoveNeeded(Configuration configuration)
    {
        runOnUiThread(() -> lessonsRemoveNeeded = configuration.isLessonsImageRemoveNeeded());
        return lessonsRemoveNeeded;
    }
}
