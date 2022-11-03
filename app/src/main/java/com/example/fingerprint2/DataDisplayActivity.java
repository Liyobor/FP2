package com.example.fingerprint2;

import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;


public class DataDisplayActivity extends AppCompatActivity {


    ArrayList<String> info = new ArrayList<>();
    ArrayList<String> formatList = new ArrayList<>(Arrays.asList("姓名:","醫事機構:","醫師姓名:","醫事機構:","科別:"));
    ArrayList<String> procedureFormatList = new ArrayList<>();
    String procedure = "";
    ArrayList<String> prescriptionFormatList = new ArrayList<>();
    String prescription = "";
    InputStream data;


//    variables from DataDecryptActivity


    private String result1;
    private String result2;
    private String result3;
    private String result4;
    private String PrivateKeyPath;

    private JSONObject JsonInfo;
    private String cert;
    private String mrData;
    private String aesKey;
    private String iv;

    private String path = "/data"+"/data/com.example.myapplication/files/";

    private int caseOID;
    private int account;

    private void displayRecord(){
        info.add(11,"病情摘要");
        ListView listViewData = findViewById(R.id.listViewData);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, info);
        listViewData.setAdapter(adapter);
    }

    private void parseXml(InputStream raw){

        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            data = raw;
            xmlPullParser.setInput(data,"utf-8");
            int eventType = xmlPullParser.getEventType();
            int nameCount = 0;
            int effectiveTimeCount = 0;
            int formatListCount = 0;
            int resultCount =0;
            int count1 = 0;
            int count2 = 0;
            while (eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        switch (xmlPullParser.getName()) {
                            case "recordTarget":
                                break;
                            case "name":
                                if (nameCount <= 4) {
                                    String name = xmlPullParser.nextText();
                                    info.add(formatList.get(nameCount) + name);
                                    nameCount += 1;
                                }
                                break;
                            case "birthTime":
                                String birthTime = xmlPullParser.getAttributeValue(null, "value");
                                info.add("出生日期 : " + birthTime);

                                break;
                            case "effectiveTime":
                                String effectiveTime = xmlPullParser.getAttributeValue(null, "value");
                                if (effectiveTimeCount == 0) {
                                    info.add("列印日期 : " + effectiveTime);
                                    effectiveTimeCount += 1;
                                } else {
                                    info.add("門診日期 : " + effectiveTime);
                                }

                                break;
                            case "administrativeGenderCode":
                                String gender = xmlPullParser.getAttributeValue(null, "displayName");
//                            Log.e("data",gender);
                                if (gender.equals("Male")) {
                                    info.add("性別:男");
                                } else {
                                    info.add("性別:女");
                                }
                                break;
                            case "patient":
                                if (xmlPullParser.getAttributeValue(null, "classCode").equals("PSN")) {
                                    xmlPullParser.nextTag();
                                    String id = xmlPullParser.getAttributeValue(null, "extension");
                                    info.add("身分證字號 : " + id);
                                }

                                break;
                            case "time":
                                String time = xmlPullParser.getAttributeValue(null, "value");
                                info.add("醫事紀錄時間:" + time);
                                break;
                            case "title":
                                switch (xmlPullParser.nextText()) {
                                    case "診斷":
                                        StringBuilder diagnosis = new StringBuilder();
                                        while (true) {
                                            xmlPullParser.nextTag();
                                            if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
                                                break;
                                            } else if (xmlPullParser.getName().equals("paragraph")) {
                                                diagnosis.append(xmlPullParser.nextText());
                                                diagnosis.append("\n\n");
                                            }
                                        }
                                        info.add("診斷:" + diagnosis);
                                        break;
                                    case "主觀描述":
                                        StringBuilder subjective = new StringBuilder();
                                        while (true) {
                                            xmlPullParser.nextTag();
                                            if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
                                                break;
                                            } else if (xmlPullParser.getName().equals("paragraph")) {
                                                subjective.append(xmlPullParser.nextText());
                                                subjective.append("\n\n");
                                            }
                                        }
                                        info.add("主觀描述:\n" + subjective);
                                        break;
                                    case "客觀描述":
                                        StringBuilder Objective = new StringBuilder();
                                        while (true) {
                                            xmlPullParser.nextTag();
                                            if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
                                                break;
                                            } else if (xmlPullParser.getName().equals("paragraph")) {
                                                Objective.append(xmlPullParser.nextText());
                                                Objective.append("\n\n");
                                            }
                                        }
                                        info.add("客觀描述:\n" + Objective);
                                }
                                break;
                            case "thead":
                                xmlPullParser.nextTag();
                                xmlPullParser.nextTag();
                                while (!xmlPullParser.getName().equals("tr")) {
                                    String text = xmlPullParser.nextText();
                                    Timber.e(text);
                                    if (formatListCount == 0) {
                                        procedureFormatList.add(text);
                                    } else {
                                        prescriptionFormatList.add(text);
                                    }
                                    xmlPullParser.nextTag();
                                }
                                formatListCount = 1;
                                break;
                            case "tbody":
                                if (resultCount == 0) {
                                    info.add("處置項目");
                                } else {
                                    info.add("處方");
                                }
                                xmlPullParser.nextTag();
                                xmlPullParser.nextTag();
                                while (!xmlPullParser.getName().equals("tr")) {
                                    if (xmlPullParser.getName().equals("td")) {
                                        String text = xmlPullParser.nextText();
                                        if (resultCount == 0) {
                                            Timber.e(text);
                                            procedure += procedureFormatList.get(count1) + " : " + text + "\n";
                                            count1 += 1;
                                        } else {

                                            prescription += prescriptionFormatList.get(count2) + " : " + text + "\n";
                                            Timber.e(text);
                                            count2 += 1;
                                        }

                                    }
                                    xmlPullParser.nextTag();
                                }
                                if (resultCount == 0) {
                                    info.add(procedure);
                                } else {
                                    info.add(prescription);
                                }
                                resultCount = 1;
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }

            data.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        Bundle bundle = getIntent().getExtras();
        caseOID = bundle.getInt("caseOID",-1);
        Timber.i("caseOID in dataDisplay = %s",caseOID);
        account = bundle.getInt("account",-1);
        Timber.i("account in dataDisplay = %s",account);
        path = bundle.getString("privateKeyPath","error");
        Timber.i("path = %s",path);

        Thread thread1 = new Thread(mutiThread1);
        thread1.start();
        try {
            thread1.join(8000);

            new Thread(() -> {
                parseXml(data);
                runOnUiThread(this::displayRecord);
            }
            ).start();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        set fab onclick action
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> sign());



    }



//    fetch and send when invoking the function
    public void sign(){
        Toast.makeText(this,"sign",Toast.LENGTH_LONG).show();
        Thread thread1 = new Thread(mutiThread1);
        thread1.start();
        try {
            thread1.join(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Thread thread2 = new Thread(mutiThread2);
//        thread2.start();
    }

    //接收來自伺服器的資料
//    private void fetch(){
//        Thread thread1 = new Thread(mutiThread1);
//        thread1.start();
//    }
//    //傳送結果給伺服器
//    private void send(){
//        Thread thread2 = new Thread(mutiThread2);
//        thread2.start();
//    }


    //convert File type to String type
    private String StreamToString(InputStream keyStream) throws IOException {
        Timber.i("FileInputStream");
//        Timber.i("stream = %s", keyStream);
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(keyStream);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
//        Timber.i("out.toString() = %s",out.toString());
        return out.toString();
    }






    //使用私鑰解密
    private String privateKeyEncrypt(String Data, InputStream keyStream) throws Exception {
        String stringBefore= StreamToString(keyStream);
//        String stringBefore=FileToString(key);
        String Key = stringBefore
                .replaceAll("\\n", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .trim();
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecX509 = new PKCS8EncodedKeySpec(rsa.decryptBASE64(Key));
        RSAPrivateKey privatekey = (RSAPrivateKey) kf.generatePrivate(keySpecX509);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privatekey);

        byte[] EncData= cipher.doFinal(Data.getBytes(StandardCharsets.UTF_8));
        return rsa.encryptBASE64(EncData);
    }

    //創建json的格式並封裝內容
    private JSONArray packageResult(String EncResult, String account, String documentOID) {
        JSONArray object = new JSONArray();
        object.put(account);                     //在php提取資料庫中對應的公鑰
        object.put(documentOID);                 //私鑰加密過
        object.put(EncResult);                   //私鑰加密過
        return object;
    }

    //使用私鑰解密
    private String privateKeyDecrypt(String encData, String stringBefore) {

//        String stringBefore= StreamToString(keyStream);
//        Timber.i("encData = %s",encData);

        try{
            String Key = stringBefore
                    .replaceAll("\\n", "")
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .trim();
            Timber.i("Key = %s",Key);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpecX509 = new PKCS8EncodedKeySpec(rsa.decryptBASE64(Key));
//            Timber.i("Key = %s", new String(rsa.decryptBASE64(Key), StandardCharsets.ISO_8859_1));
            RSAPrivateKey privatekey = (RSAPrivateKey) kf.generatePrivate(keySpecX509);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privatekey);

//            Timber.i("bytes = %s", new String(rsa.decryptBASE64(encData), StandardCharsets.ISO_8859_1));

            Timber.i("bytes = %s", new String(java.util.Base64.getDecoder().decode(encData)));
//            rsa.decryptBASE64()
//            Timber.i("bytes = %s", new String(Base64.decode(encData,Base64.NO_PADDING), StandardCharsets.UTF_8));
            byte[] decData= cipher.doFinal(rsa.decryptBASE64(encData));
//            byte[] decData= cipher.doFinal(encData.getBytes());

            return new String(decData);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }


    //對稱解密
    private String aes_decrypt(String sSrc,String key,String ivs) throws Exception {
        byte[] raw = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec iv = new IvParameterSpec(ivs.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);// 先用base64解密
        byte[] original = cipher.doFinal(encrypted1);
        return new String(original);
    }




    private final Runnable mutiThread2 = new Runnable() {

        final StringBuilder response = new StringBuilder();

        @Override
        public void run() {
            Looper.prepare();
            try {
                URL url = new URL("http://140.136.151.70/download/getDocument.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setUseCaches(false);
                con.connect();

                String one="1";                                                     //放入簽章結果 1:成功 其他值:失敗
                String documentOID=Integer.toString(caseOID);                       //病例的OID caseOID row in file_info on database
                String userAccount=Integer.toString(account);                       //使用者帳號 account row in account_info on database
//                File file = new File(path, "private.key");

                InputStream fileStream = getContentResolver().openInputStream(Uri.parse(path));



                String result=privateKeyEncrypt(one, fileStream);                         //加密簽章結果
                String OID=privateKeyEncrypt(documentOID, fileStream);                    //加密病例的OID
                JSONArray Package = packageResult(result, userAccount, OID);        //封裝回傳的三個資料

                try(OutputStream os = con.getOutputStream()) {                      //傳輸
                    byte[] input = Package.toString().getBytes();
                    os.write(input, 0, input.length);
                }catch (Exception e){
//                    text1.setText(e.toString());
                }
                try(BufferedReader br = new BufferedReader(                         //回傳php的結果
                        new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
//                    text2.setText(response);
                }catch (Exception e) {
//                    text3.setText(e.toString());
                }
            }catch (Exception e) {
//                text2.setText(e.toString());
            }

            runOnUiThread(() -> {

            });
        }
    };


    private static String base64decode(String string) {
        byte[] bytes = java.util.Base64.getDecoder().decode(string);
        return new String(bytes,StandardCharsets.UTF_8);
    }




    private final Runnable mutiThread1 = new Runnable() {                                 //這個執行緒負責抓取接收json跟解密

        @Override
        public void run() {
            Looper.prepare();
            try {                                                                   //連線php
                URL url = new URL("http://140.136.151.70/download/fetchDocument.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setUseCaches(false);
                con.connect();

                JSONArray documentOID = new JSONArray();
                Timber.i("account = %s",account);
                Timber.i("caseOID = %s",caseOID);
                documentOID.put(account);                        //放入使用者帳號 account row in account_info on database
                documentOID.put(caseOID);                        //放入資料庫裡caseOID的值 以抓取對應的病例

                try(OutputStream os = con.getOutputStream()) {                     //傳輸
                    byte[] input = documentOID.toString().getBytes();
                    os.write(input, 0, input.length);
                }catch (Exception e){
//                    text1.setText(e.toString());
                }
                /*try(BufferedReader br = new BufferedReader(                       //回傳php的結果
                        new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    text2.setText(response);
                }catch (Exception e) {
                    text3.setText(e.toString());
                }*/

                int responseCode = con.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {            //抓取來自php的json檔案
                    InputStream inputStream = con.getInputStream();
//                    Timber.i("inputStream = %s",inputStream);



                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 8);
                    String line;
//                    Timber.i("line = %s",bufferedReader.readLine());
                    line = bufferedReader.readLine();
//                    while (line != null) {    //解析json內容
                        JSONArray dataJson = new JSONArray(line);
                        int i = dataJson.length() - 1;
                        JsonInfo = dataJson.getJSONObject(i);


//                        Timber.i("JsonInfo = %s",JsonInfo);


                    String signature = base64decode(JsonInfo.getString("signature"));
                    mrData = JsonInfo.getString("aes_encrypt_mrData");


                    aesKey = JsonInfo.getString("rsa_encrypt_aesKey");

                    iv = JsonInfo.getString("rsa_encrypt_iv");
                    result1 = mrData;
                    result2 = aesKey;
                    result3 = iv;





//                    Timber.i("signature = %s",signature);
//                        Timber.i("result1 = %s",result1);
//                        Timber.i("result2 = %s",result2);
//                        Timber.i("result3 = %s",result3);


//                    }

//                    path =  "/data"+"/data/com.android.externalstorage.documents/document/1511-181D%3A10666_private.key";
//                    private String path = "/data"+"/data/com.example.myapplication/files/";
//                    Timber.i("result1 length = %s",result1.length());
//                    Timber.i("result1 = %s",base64decode(result1));
//                    byte[] decodeResult = java.util.Base64.getDecoder().decode(result1);
//                    Timber.i("path = %s",path);



                    InputStream fileStream = getContentResolver().openInputStream(Uri.parse(path));
                    String stringBefore= StreamToString(fileStream);
//                    Timber.i("stringBefore = %s",stringBefore);




//                    Timber.i("result = %s",result);
//                    File file = new File(path);




//                    Timber.i("new File = %s",file);
//                    Timber.i("canRead = %s",file.canRead());



//                    Timber.i()

                    aesKey = privateKeyDecrypt(aesKey, stringBefore);           //解密對稱金鑰
                    iv = privateKeyDecrypt(iv, stringBefore);                   //解密iv
                    result1 = aes_decrypt(result1, aesKey, iv);         //對稱解密病例

                    Timber.i("result1 = %s",result1);
                    InputStream targetStream = new ByteArrayInputStream(result1.getBytes());

                    data = targetStream;








                    inputStream.close();
                }
            } catch (Exception e) {
                result4 = e.toString();
            }

//            runOnUiThread(() -> {
//                text1.setText(result1);
//                text2.setText(result2);
//                text3.setText(result3);
//                text4.setText(result4);
//            });
        }
    };

}