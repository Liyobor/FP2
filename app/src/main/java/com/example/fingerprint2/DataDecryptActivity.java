package com.example.fingerprint2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

public class DataDecryptActivity extends AppCompatActivity {

    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;

    private String result1;
    private String result2;
    private String result3;
    private String result4;
    private  String PrivateKeyPath;

    private JSONObject info;
    private String cert;
    private String mrData;
    private String aesKey;
    private String iv;

    private final String path = "/data"+"/data/com.example.myapplication/files/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_decrypt);
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        text1 = (TextView) findViewById(R.id.messageText1);
        text2 = (TextView) findViewById(R.id.messageText2);
        text3 = (TextView) findViewById(R.id.messageText3);
        text4 = (TextView) findViewById(R.id.messageText4);


    }


    //??????????????????????????????
    public void fetch(View v){
        Thread thread1 = new Thread(mutiThread1);
        thread1.start();
    }
    //????????????????????????
    public void send(View v){
        Thread thread2 = new Thread(mutiThread2);
        thread2.start();
    }



    //sha256?????? ???????????????
    public void hash(View v) throws Exception {
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = shaDigest.digest(result1.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            final String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        text3.setText(hexString.toString());
    }
    //????????????
    public String aes_decrypt(String sSrc,String key,String ivs) throws Exception {
        byte[] raw = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec iv = new IvParameterSpec(ivs.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);// ??????base64??????
        byte[] original = cipher.doFinal(encrypted1);
        return new String(original);
    }
    //???????????? ????????????
    public String aes_encrypt(String sSrc,String key,String ivs) throws Exception {
        byte[] raw = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec iv = new IvParameterSpec(ivs.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        return rsa.encryptBASE64(original);
    }
    //??????json????????????????????????
    public JSONArray packageResult(String EncResult, String account, String documentOID) {
        JSONArray object = new JSONArray();
        object.put(account);                     //???php?????????????????????????????????
        object.put(documentOID);                 //???????????????
        object.put(EncResult);                   //???????????????
        return object;
    }
    //??????xml?????????????????????
    public void packageXML(String sha256Digest, String SignatureValue, String X509Certificate, String EncapsulatedTimeStamp, String SignatureSerialNumber, String HCASerialNumber){
        try {
            File file = new File(path, "??????????????????????????????.xml");

//            String path = this.getFilesDir().getPath()+"data/com.example.myapplication/files/";
//            Timber.i("path = %s", path);
//            File file = new File(, "??????????????????????????????.xml");
            String stringBefore = FileToString(file);
            Document doc = DocumentHelper.parseText(stringBefore);
            Element root = doc.getRootElement();

            Element Digest = root.element("Signature").element("SignedInfo").element("Reference").element("DigestValue");
            Digest.setText(sha256Digest);
            Element Signature = root.element("Signature").element("SignatureValue");
            Signature.setText(SignatureValue);
            Element Certificate = root.element("Signature").element("KeyInfo").element("X509Data").element("X509Certificate");
            Certificate.setText(X509Certificate);
            Element TimeStamp = root.element("Signature").element("Object").element("QualifyingProperties").element("UnsignedProperties").element("UnsignedSignatureProperties").element("SignatureTimeStamp").element("EncapsulatedTimeStamp");
            TimeStamp.setText(EncapsulatedTimeStamp);

            root.addAttribute("Id", SignatureSerialNumber);
            Element Reference = root.element("Signature").element("SignedInfo").element("Reference");
            Reference.addAttribute("URI", SignatureSerialNumber);
            Element SignatureNumber = root.element("Signature");
            SignatureNumber.addAttribute("Id", HCASerialNumber);
            Element Properties = root.element("Signature").element("Object").element("QualifyingProperties");
            Properties.addAttribute("Target", HCASerialNumber);


            result4= doc.asXML();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
    //convert File type to String type
    public String FileToString(File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
        return out.toString();
    }
    //??????????????????
    public String privateKeyDecrypt(String encData, File key) throws Exception {
        String stringBefore=FileToString(key);
        String Key = stringBefore
                .replaceAll("\\n", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .trim();
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecX509 = new PKCS8EncodedKeySpec(rsa.decryptBASE64(Key));
        RSAPrivateKey privatekey = (RSAPrivateKey) kf.generatePrivate(keySpecX509);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privatekey);
        byte[] decData= cipher.doFinal(rsa.decryptBASE64(encData));

        return new String(decData);
    }
    //??????????????????
    public String privateKeyEncrypt(String Data, File key) throws Exception {
        String stringBefore=FileToString(key);
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
    //???????????????????????? ????????????????????????????????????
    public void getPrivateKeyPath(View v)
    {
        PrivateKeyPath= (String) text1.getText();
    }
    private final Runnable mutiThread1 = new Runnable() {                                 //?????????????????????????????????json?????????
        @Override
        public void run() {
            Looper.prepare();
            try {                                                                   //??????php
                URL url = new URL("http://140.136.151.70/download/fetchDocument.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setUseCaches(false);
                con.connect();

                JSONArray documentOID = new JSONArray();
                documentOID.put(123);                                               //????????????????????? account row in account_info on database
                documentOID.put(1);                                                //??????????????????caseOID?????? ????????????????????????

                try(OutputStream os = con.getOutputStream()) {                     //??????
                    byte[] input = documentOID.toString().getBytes();
                    os.write(input, 0, input.length);
                }catch (Exception e){
                    text1.setText(e.toString());
                }
                /*try(BufferedReader br = new BufferedReader(                         //??????php?????????
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

                if (responseCode == HttpURLConnection.HTTP_OK) {            //????????????php???json??????
                    InputStream inputStream = con.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 8);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {    //??????json??????
                        JSONArray dataJson = new JSONArray(line);
                        int i = dataJson.length() - 1;
                        info = dataJson.getJSONObject(i);
                        mrData = info.getString("aes_encrypt_mrData");
                        aesKey = info.getString("rsa_encrypt_aesKey");
                        iv = info.getString("rsa_encrypt_iv");
                        result1 = mrData;
                        result2 = aesKey;
                        result3 = iv;
                    }
                    File file = new File(path, "private.key");

                    aesKey = privateKeyDecrypt(aesKey, file);           //??????????????????
                    iv = privateKeyDecrypt(iv, file);                   //??????iv
                    result1 = aes_decrypt(result1, aesKey, iv);         //??????????????????
                    inputStream.close();
                }
            } catch (Exception e) {
                result4 = e.toString();
            }

            runOnUiThread(() -> {
                text1.setText(result1);
//                    text2.setText(result2);
//                    text3.setText(result3);
                text4.setText(result4);
            });
        }
    };

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

                String one="1";                                                     //?????????????????? 1:?????? ?????????:??????
                String documentOID="1";                                             //?????????OID caseOID row in file_info on database
                String account="123";                                               //??????????????? account row in account_info on database
                File file = new File(path, "private.key");



                String result=privateKeyEncrypt(one, file);                         //??????????????????
                String OID=privateKeyEncrypt(documentOID, file);                    //???????????????OID
                JSONArray Package = packageResult(result, account, OID);            //???????????????????????????

                try(OutputStream os = con.getOutputStream()) {                      //??????
                    byte[] input = Package.toString().getBytes();
                    os.write(input, 0, input.length);
                }catch (Exception e){
                    text1.setText(e.toString());
                }
                try(BufferedReader br = new BufferedReader(                         //??????php?????????
                        new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    text2.setText(response);
                }catch (Exception e) {
                    text3.setText(e.toString());
                }
            }catch (Exception e) {
                text2.setText(e.toString());
            }

            runOnUiThread(() -> {

            });
        }
    };

    public void jump(View view) {
        Intent intent = new Intent(this,DataDisplayActivity.class);
        startActivity(intent);
    }
}