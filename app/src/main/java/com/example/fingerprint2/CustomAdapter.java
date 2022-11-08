package com.example.fingerprint2;

import android.content.Context;
import android.graphics.Color;
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
            v = inflater.inflate(R.layout.row_listvew_decrypt, null);
        }


        DataItem i = objects.get(position);

        if (i != null) {
            TextView caseNumView = v.findViewById(R.id.caseNum);
            TextView patientView = v.findViewById(R.id.patient);
            TextView birthdayView = v.findViewById(R.id.birthday);

            if (caseNumView != null){
                caseNumView.setText(i.getCaseNum());
                if(i.canDecrypt()){
                    caseNumView.setTextColor(Color.BLACK);
                }else{
                    caseNumView.setTextColor(Color.GRAY);
                }

            }
            if (patientView != null){
                patientView.setText(i.getPatient());

                if(i.canDecrypt()){
                    patientView.setTextColor(Color.BLACK);
                }else{
                    patientView.setTextColor(Color.GRAY);
                }
            }
            if (birthdayView != null){
                birthdayView.setText(i.getBirthday());
                if(i.canDecrypt()){
                    birthdayView.setTextColor(Color.BLACK);
                }else{
                    birthdayView.setTextColor(Color.GRAY);
                }
            }

        }

        return v;
    }
}
