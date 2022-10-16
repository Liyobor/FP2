package com.example.fingerprint2;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import timber.log.Timber;

public class SelectActivity extends AppCompatActivity {

    DataHandler dataHandler = new DataHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }


        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_select);
        ListView listViewRecordsList = findViewById(R.id.listViewRecordsList);

        Thread pullRecord = new Thread(() ->{
            ConnectionManager.pullRecord(dataHandler);
            ArrayList<String> info = dataHandler.info;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, info);
            runOnUiThread(() -> listViewRecordsList.setAdapter(adapter));

        });
        Thread displayListOfRecords = new Thread(()->{
            try {
                pullRecord.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(()->
                    listViewRecordsList.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                        Toast.makeText(
                                SelectActivity.this,
                                "caseOID：" + dataHandler.caseOID + "\n"+
                                        "account："+ dataHandler.account,
                                Toast.LENGTH_LONG).show();
                        Bundle bundle = new Bundle();
                        Intent intent=new Intent();
                        intent.setClass(SelectActivity.this,DataDisplayActivity.class);
                        bundle.putInt("caseOID",dataHandler.caseOID);
                        bundle.putInt("account",dataHandler.account);
                        intent.putExtras(bundle);
                        this.startActivity(intent);
                    })
            );

        }

        );
        pullRecord.start();
        displayListOfRecords.start();
    }




    @Override
    protected void onResume(){
        super.onResume();

    }
}

