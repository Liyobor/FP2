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

public class CreateActivity extends AppCompatActivity {

    Activity context=this;
    Button b1,b2;
    TextView tv1;
    EditText et1,et2=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        b1 = findViewById(R.id.btn_create_account_2);
        b2 = findViewById(R.id.btn_back);
        et1 = findViewById(R.id.et_1);
        et2 = findViewById(R.id.et_2);

        tv1 = findViewById(R.id.tv_1);
    }

    public void register (View view) {
        String cusername = et1.getText().toString();
        String cpassword = et2.getText().toString();

        if (cusername.length() < 2 || cpassword.length() < 2) {


            ToastController.showToast(getApplicationContext(),"輸入資訊不符合要求請重新輸入");

            return;

        }


        User user = new User();

        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(getApplicationContext(), preferencesHelper);
        user.setUsername(cusername);

//        register with plaintext
        user.setPassword(cpassword);

//        register with encrypted text
//        user.setPassword(keyStoreHelper.encrypt(cpassword));



        new Thread(() -> {

            int msg = 0;

            UserDao userDao = new UserDao();

            User uu = userDao.findUser(user.getUsername());

            if (uu != null) {
                msg = 1;
            }

            boolean flag = userDao.register(user);
            if (flag) {
                msg = 2;
            }
            hand.sendEmptyMessage(msg);

        }).start();
    }
//    final Handler hand = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg) {
//            if(msg.what == 0)
//            {
//                ToastController.showToast(getApplicationContext(),"註冊失敗");
//            }
//            if(msg.what == 1)
//            {
//
//                ToastController.showToast(getApplicationContext(),"帳號已存在");
//
//
//            }
//            if(msg.what == 2)
//            {
//                //startActivity(new Intent(getApplication(),MainActivity.class));
//
//                Intent intent = new Intent();
//                //將想要傳遞的資料用putExtra封裝在intent中
//                intent.putExtra("a","註冊");
//                setResult(RESULT_CANCELED,intent);
//                finish();
//            }
//
//        }
//    };


    Handler hand = new Handler(Looper.getMainLooper(), message -> {
        if(message.what == 0)
        {
            ToastController.showToast(getApplicationContext(),"註冊失敗");
        }
        if(message.what == 1)
        {

            ToastController.showToast(getApplicationContext(),"帳號已存在");


        }
        if(message.what == 2)
        {
            //startActivity(new Intent(getApplication(),MainActivity.class));
            Intent intent = new Intent();
            //將想要傳遞的資料用putExtra封裝在intent中
            intent.putExtra("a","註冊");
            setResult(RESULT_CANCELED,intent);
            finish();
        }
        return true;
    });

    public void backlogin(View view) {
        Intent intent = new Intent(CreateActivity.this,LoginActivity.class);
        startActivity(intent);
    }

        /*mAuth=FirebaseAuth.getInstance();

        b2.setOnClickListener(view -> {
            Intent intent= new Intent(CreateActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        b1.setOnClickListener(view -> mAuth.createUserWithEmailAndPassword(
                et1.getText().toString(),
                et2.getText().toString()).addOnCompleteListener(context, task -> {
            if(task.isSuccessful()){
                FirebaseUser user=mAuth.getCurrentUser();
                assert user != null;
                tv1.setText("結果:"+user.getEmail()+"註冊成功!");
            }else{
                tv1.setText("結果:註冊失敗!"+task.getException());
            }
        }));
*/
}
