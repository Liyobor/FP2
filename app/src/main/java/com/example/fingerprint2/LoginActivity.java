package com.example.fingerprint2;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import timber.log.Timber;

class GlobalInformation {
    public static String account;
    public static Integer caseOID;
    public static String privateKeyPath;
}

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101010;
//    ImageView imageViewLogin;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Button mBtnLogin;
    private Button mBtnCreateaccount;

    EditText et1,et2;
//    FirebaseAuth mAuth;
    TextView tv2;

    Activity context=this;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Timber.uprootAll();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.activity_login);
        mBtnCreateaccount = findViewById(R.id.btn_create_account);
        mBtnCreateaccount.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,CreateActivity.class);
            startActivity(intent);
        });
        et1=findViewById(R.id.et_1);
        et2=findViewById(R.id.et_2);
        tv2=findViewById(R.id.tv_2);
        mBtnLogin = findViewById(R.id.btn_login);

//        mAuth=FirebaseAuth.getInstance()
/*
        mBtnLogin.setOnClickListener(view -> mAuth.signInWithEmailAndPassword(
                et1.getText().toString(),
                et2.getText().toString()).addOnCompleteListener(
                        context, task -> {
           if (task.isSuccessful()){
               sendUserToNextActivity();
               FirebaseUser user = mAuth.getCurrentUser();
               tv2.setText("結果:登入"+user.getEmail()+"成功!");
               email=user.getEmail();
           }else {
               tv2.setText("結果:登入失敗!"+task.getException());
           }
        }));
*/

//        imageViewLogin=findViewById(R.id.imageView);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Timber.d("App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "FingerPrint sensor Not exist", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Sensor not avail or busy", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
//                startActivity(new Intent(LoginActivity.this, DataDecryptActivity.class));
                startActivity(new Intent(LoginActivity.this, SelectActivity.class));
                Toast.makeText(getApplicationContext(),
                        "login success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();


//        imageViewLogin.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));
    }
    public void reg(View view){
        startActivity(new Intent(getApplicationContext(),SelectActivity.class));

    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(LoginActivity.this, SelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void login(View view){

        EditText EditTextname = findViewById(R.id.et_1);
        EditText EditTextpassword = findViewById(R.id.et_2);

        new Thread(() -> {

            UserDao userDao = new UserDao();

            boolean aa = userDao.login(EditTextname.getText().toString(),
                    EditTextpassword.getText().toString());
            int msg = 0;
            if(aa){
                msg = 1;
            }

            hand1.sendEmptyMessage(msg);


        }).start();

    }
    final Handler hand1 = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1)
            {
                Toast.makeText(getApplicationContext(),"登入成功",
                        Toast.LENGTH_LONG).show();
                        sendUserToNextActivity();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"登入失敗",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

}