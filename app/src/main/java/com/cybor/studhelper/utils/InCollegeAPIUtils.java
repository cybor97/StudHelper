package com.cybor.studhelper.utils;

import android.app.Activity;
import android.widget.Toast;

import com.cybor.studhelper.R;
import com.cybor.studhelper.StudHelperApplication;
import com.cybor.studhelper.data.Configuration;
import com.cybor.studhelper.data.Mark;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class InCollegeAPIUtils
{
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    public static List<Mark> getMarks(Activity context, String hostName, String token)
    {
        try
        {
            Request request = new Request.Builder()
                    .url("http://" + hostName + "/Data")
                    .post(RequestBody.create(null,
                            String.format("Action=GetStudyResults&token=%s", URLEncoder.encode(token, "UTF-8"))))
                    .build();
            try
            {
                Response response = StudHelperApplication
                        .httpClient
                        .newCall(request)
                        .execute();
                ResponseBody body = response.body();
                String responseString = body == null ? "" : body.string();
                if (response.code() == HttpURLConnection.HTTP_OK)
                {
                    List<Mark> result = gson.fromJson(responseString, new TypeToken<ArrayList<Mark>>()
                    {
                    }.getType());
                    Collections.sort(result, (c1, c2) ->
                    {
                        if (c1 == null || c2 == null ||
                                c1.getDate() == null ||
                                c2.getDate() == null)
                            return 0;
                        long t1 = c1.getDate().getTime();
                        long t2 = c2.getDate().getTime();
                        return t1 < t2 ? 1 : t1 > t2 ? -1 : 0;
                    });
                    return result;
                } else
                    context.runOnUiThread(() -> Toast.makeText(context,
                            responseString,
                            Toast.LENGTH_LONG));
                if (body != null)
                    body.close();
            } catch (IOException e)
            {
                context.runOnUiThread(() -> Toast
                        .makeText(context, R.string.network_error, Toast.LENGTH_LONG)
                        .show());
            }
        } catch (UnsupportedEncodingException e)
        {
            context.runOnUiThread(() -> Toast
                    .makeText(context, R.string.inner_error, Toast.LENGTH_LONG)
                    .show());
        }
        return null;
    }

    public static boolean authorize(Activity context, String hostName, String userName, String password)
    {
        Request request = new Request.Builder()
                .url("http://" + hostName + "/Auth")
                .post(RequestBody.create(null,
                        String.format("Action=SignIn&UserName=%1s&Password=%2s", userName, password)))
                .build();
        try
        {
            Response response = StudHelperApplication
                    .httpClient
                    .newCall(request)
                    .execute();
            ResponseBody body = response.body();
            String responseString = body == null ? "" : body.string();
            if (response.code() == HttpURLConnection.HTTP_OK)
            {
                context.runOnUiThread(() -> Configuration.getInstance().setToken(responseString));
                return true;
            } else
                context.runOnUiThread(() -> Toast.makeText(context,
                        responseString,
                        Toast.LENGTH_LONG));
            if (body != null)
                body.close();
        } catch (IOException e)
        {
            context.runOnUiThread(() -> Toast
                    .makeText(context, R.string.network_error, Toast.LENGTH_LONG)
                    .show());
        }
        return false;
    }

}
