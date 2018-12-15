package cn.bluemobi.dylan.step.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bmobJava.MyUser;
import cn.bluemobi.dylan.step.step.service.StepService;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.text.TextUtils.isEmpty;

public class Registration extends AppCompatActivity {
    StepService stepService=new StepService();
    MyUser myUser=new MyUser();
    SignIn flag_sign=new SignIn();

    protected EditText etRegName;
    protected EditText etRegPass;
    protected EditText etRegFirmPass;
    protected Button btnRegister;
    protected Button btnCancle;

    private void ControlAssociation() {
        etRegName = (EditText) findViewById(R.id.etRegName);
        etRegPass = (EditText) findViewById(R.id.etRegPass);
        etRegFirmPass = (EditText) findViewById(R.id.etRegFirmPass);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnCancle = (Button) findViewById(R.id.btnCancle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        ControlAssociation();
        Bmob.initialize(this,"df877b92936d74812d2b67a4c6c8c43d");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnRegister.setOnClickListener(new Confirmation_Registration());
        btnCancle.setOnClickListener(new Cancel_Registration());
    }

    public class Confirmation_Registration implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(isEmpty(etRegFirmPass.getText()) || isEmpty(etRegName.getText()) || isEmpty(etRegPass.getText())){
                Toast.makeText(getApplicationContext(),
                        "用户名称或密码不能为空",Toast.LENGTH_SHORT).show();
            }else{
                final String name=etRegName.getText().toString();
                String password=etRegPass.getText().toString();
                String firmpassword=etRegFirmPass.getText().toString();
                if(password.equals(firmpassword)) {
                    //用户注册
                    MyUser bu = new MyUser();
                    bu.setUsername(name);
                    bu.setPassword(password);
                    bu.setEmail(name + "@qq.com");
                    bu.setStep(0);
                    bu.setRanking(0);
                    //注意：不能用save方法进行注册
                    bu.signUp(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser s, BmobException e) {
                            if (e == null) {
                                MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
                                if(userInfo!=null){
                                    stepService.CURRENT_STEP=userInfo.getStep();
                                }
                                Intent intent=new Intent(Registration.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "注册成功",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "注册失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "密码错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Cancel_Registration implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            flag_sign.UserBuffering=false;
            Intent intent=new Intent(Registration.this,SignIn.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(Registration.this,SignIn.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
