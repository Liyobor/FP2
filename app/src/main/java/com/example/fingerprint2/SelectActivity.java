package com.example.fingerprint2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.BuildConfig;

import timber.log.Timber;

public class SelectActivity extends AppCompatActivity {

    DataHandler dataHandler = new DataHandler();

    private Integer selectIndex;



    private void selectActivityForResult(){
        Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
        picker.setType("*/*");
        selectActivityResultLauncher.launch(picker);
    }

    ActivityResultLauncher<Intent> selectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Timber.i("result = %s",result);
                    if (result.getData() != null) {
//                        Timber.i("getDataString = %s",result.getData().getDataString());
                        Timber.i("data uri = %s",result.getData().getData());

//                        String privateKeyUri = getRealPathFromURI(result.getData().getData());
                        Bundle bundle = new Bundle();
                        Intent intent=new Intent();
                        intent.setClass(SelectActivity.this,DataDisplayActivity.class);
                        bundle.putInt("caseOID",dataHandler.caseOIDList.get(selectIndex));
                        bundle.putString("account",dataHandler.accountList.get(selectIndex));
                        bundle.putString("privateKeyPath",result.getData().getDataString());

                        GlobalInformation.caseOID = dataHandler.caseOIDList.get(selectIndex);
                        GlobalInformation.account = dataHandler.accountList.get(selectIndex);
                        GlobalInformation.privateKeyPath = result.getData().getDataString();

                        intent.putExtras(bundle);
                        this.startActivity(intent);
                    }
                }
            });







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
            CustomAdapter adapter = new CustomAdapter(this,R.layout.row_listvew_decrypt, dataHandler.dataSelectDisplayList);
//            ArrayList<String> info = dataHandler.info;

//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, info);
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
                                "caseOID：" + dataHandler.caseOIDList.get(arg2) + "\n"+
                                        "account："+ dataHandler.accountList.get(arg2),
                                Toast.LENGTH_LONG).show();
//                        Timber.i("arg0 = %s",arg0);
//                        Timber.i("arg1 = %s",arg1);
//                        Timber.i("arg2 = %s",arg2);
//                        Timber.i("arg3 = %s",arg3);
                        Timber.i("dataHandler.caseOIDList.get(arg2) = %s",dataHandler.caseOIDList.get(arg2));
                        Timber.i("dataHandler.accountList.get(arg2) = %s",dataHandler.accountList.get(arg2));
//                        Bundle bundle = new Bundle();
//                        Intent intent=new Intent();
//                        intent.setClass(SelectActivity.this,DataDisplayActivity.class);
                        selectIndex = arg2;
//                        bundle.putInt("caseOID",dataHandler.caseOIDList.get(arg2));
//                        bundle.putInt("account",dataHandler.accountList.get(arg2));
//                        intent.putExtras(bundle);
                        if(GlobalInformation.privateKeyPath==null) {
                            selectActivityForResult();
                        } else {
                            Bundle bundle = new Bundle();
                            Intent intent=new Intent();
                            intent.setClass(SelectActivity.this,DataDisplayActivity.class);
                            bundle.putInt("caseOID",GlobalInformation.caseOID);
                            bundle.putString("account",GlobalInformation.account);
                            bundle.putString("privateKeyPath",GlobalInformation.privateKeyPath);
                            intent.putExtras(bundle);
                            this.startActivity(intent);
                        };



//                        this.startActivity(intent);
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

