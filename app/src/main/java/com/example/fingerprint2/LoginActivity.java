package com.example.fingerprint2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import java.util.concurrent.Executor;

import timber.log.Timber;



public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101010;
//    ImageView imageViewLogin;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Button mBtnLogin;
    private Button mBtnCreateaccount;

    private EditText EditTextname;
    private EditText EditTextpassword;

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
        Timber.i("account = %s",GlobalInformation.account);
        Timber.i("privateKeyPath = %s",GlobalInformation.privateKeyPath);
        Timber.i("oid = %s",GlobalInformation.caseOID);
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
               tv2.setText("??????:??????"+user.getEmail()+"??????!");
               email=user.getEmail();
           }else {
               tv2.setText("??????:????????????!"+task.getException());
           }
        }));
*/

//        imageViewLogin=findViewById(R.id.imageView);

//        BiometricManager biometricManager = BiometricManager.from(this);
//        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
//            case BiometricManager.BIOMETRIC_SUCCESS:
//                Timber.d("App can authenticate using biometrics.");
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//
//                ToastController.showToast(this,"FingerPrint sensor Not exist");
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                ToastController.showToast(this,"Sensor not avail or busy");
//
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                // Prompts the user to create credentials that your app accepts.
//                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
//                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
//                startActivityForResult(enrollIntent, REQUEST_CODE);
//                break;
//        }

//        executor = ContextCompat.getMainExecutor(this);
//        biometricPrompt = new BiometricPrompt(LoginActivity.this,
//                executor, new BiometricPrompt.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode,
//                                              @NonNull CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//
//                ToastController.showToast(getApplicationContext(),"Authentication error: " + errString);
//
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(
//                    @NonNull BiometricPrompt.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
////                startActivity(new Intent(LoginActivity.this, DataDecryptActivity.class));
//                startActivity(new Intent(LoginActivity.this, SelectActivity.class));
//                ToastController.showToast(getApplicationContext(),"login success");
//
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//                ToastController.showToast(getApplicationContext(),"Authentication failed");
//
//            }
//        });

//        promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                .setTitle("Biometric login for my app")
//                .setSubtitle("Log in using your biometric credential")
//                .setNegativeButtonText("Use account password")
//                .build();


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

        EditTextname = findViewById(R.id.et_1);
        EditTextpassword = findViewById(R.id.et_2);

        new Thread(() -> {

            UserDao userDao = new UserDao();



            int msg = 0;

            SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(getApplicationContext());
            KeyStoreHelper keyStoreHelper = new KeyStoreHelper(getApplicationContext(), preferencesHelper);

//            if password was encrypted
//            String encryptedPassword = userDao.login(EditTextname.getText().toString(),
//                    EditTextpassword.getText().toString());
//            if(!encryptedPassword.equals("") && keyStoreHelper.decrypt(encryptedPassword).equals(EditTextpassword.getText().toString())){
//                msg = 1;
//            }

//            if password was not encrypted
            String password = userDao.login(EditTextname.getText().toString(),
                    EditTextpassword.getText().toString());
            if(!password.equals("") && password.equals(EditTextpassword.getText().toString())){
                msg = 1;
            }

            hand1.sendEmptyMessage(msg);


        }).start();

    }
//    final Handler hand1 = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg) {
//
//
//        }
//    };


    Handler hand1 = new Handler(Looper.getMainLooper(), message -> {
        if(message.what == 1)
        {
//            ToastController.showToast(getApplicationContext(),"acc = "+EditTextname.getText().toString()+"\npass = "+EditTextpassword.getText().toString());
            ToastController.showToast(getApplicationContext(),"????????????");
            sendUserToNextActivity();

        }
        else
        {
//            ToastController.showToast(getApplicationContext(),"acc = "+EditTextname.getText().toString()+"\npass = "+EditTextpassword.getText().toString());
            ToastController.showToast(getApplicationContext(),"????????????");

        }
        return true;
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy");
    }
}