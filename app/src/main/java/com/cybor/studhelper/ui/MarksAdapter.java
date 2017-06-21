package com.cybor.studhelper.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cybor.studhelper.R;
import com.cybor.studhelper.data.Mark;

import java.text.DateFormat;

import static com.cybor.studhelper.data.Mark.PASSED;


public class MarksAdapter extends ArrayAdapter<Mark>
{

    public MarksAdapter(@NonNull Context context, @NonNull Mark[] objects)
    {
        super(context, R.layout.mark_vh, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.mark_vh, parent, false);

        Mark item = getItem(position);
        ((TextView) view.findViewById(R.id.subject_tv)).setText(item == null ?
                "" :
                item.getSubjectName());

        ((TextView) view.findViewById(R.id.date_tv)).setText(item == null || item.getDate() == null ?
                "" :
                DateFormat.getDateInstance(DateFormat.FULL).format(item.getDate()));

        TextView markValueTV = (TextView) view.findViewById(R.id.mark_value_tv);
        if (item != null)
        {
            byte mark = item.getMarkValue();
            markValueTV.setTextColor(mark > 3 || mark == PASSED ? Color.GREEN : mark < 3 ? Color.RED : Color.GRAY);
            markValueTV.setText(item.getMarkValueString());
        } else markValueTV.setText("");
        return view;
    }
}
