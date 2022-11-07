package com.example.fingerprint2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataItem> {
    private final ArrayList<DataItem> objects;

    public CustomAdapter(Context context, int textViewResourceId, ArrayList<DataItem>objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row, null);
        }


        DataItem i = objects.get(position);

        if (i != null) {
            TextView caseNumView = (TextView) v.findViewById(R.id.caseNum);
            TextView patientView = (TextView) v.findViewById(R.id.patient);
            TextView birthdayView = (TextView) v.findViewById(R.id.birthday);

            if (caseNumView != null){
                caseNumView.setText(i.getCaseNum());
            }
            if (patientView != null){
                patientView.setText(i.getPatient());
            }
            if (birthdayView != null){
                birthdayView.setText(i.getBirthday());
            }

        }

        return v;
    }
}
