package cn.bluemobi.dylan.step.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bmobJava.MyUser;
import cn.bluemobi.dylan.step.step.service.StepService;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Call;

import static android.text.TextUtils.isEmpty;
//SharedPreferences setting;

public class SignIn extends AppCompatActivity {
    /*判断用户是否第一次登录
     * 若是第一次则保存用户进数据库*/
    SharedPreferences sharedPreferences;
    Boolean user_first;

    //验证码url
    private String codeUrl = "http://jwxw.gzcc.cn/CheckCode.aspx";
    //登录url
    private String loginUrl = "http://jwxw.gzcc.cn/default2.aspx";
    private String name;
    private String password;
    private EditText AccountNumber;//帐号
    private EditText LoginPassword;//密码
    private EditText et_code;
    private ImageView iv_code;
    private Button Login;
    private TextView New_user_registration;
    private TextView Forget_the_password;
    public static boolean UserBuffering = true;

    StepService stepService = new StepService();
    MyUser myUser = new MyUser();

    private void Initialization() {
        AccountNumber = (EditText) findViewById(R.id.AccountNumber);
        LoginPassword = (EditText) findViewById(R.id.LoginPassword);
        et_code = (EditText) findViewById(R.id.et_code);
        iv_code = (ImageView) findViewById(R.id.iv_code);

        Login = (Button) findViewById(R.id.Login);
        New_user_registration = (TextView) findViewById(R.id.New_user_registration);
        Forget_the_password = (TextView) findViewById(R.id.Forget_the_password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        Bmob.initialize(this, "df877b92936d74812d2b67a4c6c8c43d");
        Initialization();
        addListener();
        initCode();

        if (UserBuffering) {
            MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
            if (userInfo != null) {
                Intent intent1 = new Intent(SignIn.this, MainActivity.class);
                startActivity(intent1);
                finish();
            } else {
            }
        } else {
        }
    }

    private void addListener() {
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                name=AccountNumber.getText().toString();
//                password=LoginPassword.getText().toString();
//                if(isEmpty(AccountNumber.getText()) || isEmpty(AccountNumber.getText())){
//                    Toast.makeText(getApplicationContext(),
//                            "用户名称或密码不能为空",Toast.LENGTH_SHORT).show();
//                }else {
//                    BmobUser.loginByAccount(name, password, new LogInListener<MyUser>() {
//                        @Override
//                        public void done(MyUser user, BmobException e) {
//                            if(user!=null){
//                                MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
//                                if(userInfo!=null){
//                                    stepService.CURRENT_STEP=userInfo.getStep();
//                                }
//                                Intent intent=new Intent(SignIn.this,MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else {
//                                Toast.makeText(getApplicationContext(),
//                                        "用户登陆失败",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }

                final String stuId = AccountNumber.getText().toString();
                final String stuPsw = LoginPassword.getText().toString();
                String code = et_code.getText().toString();
                OkHttpUtils.post()
                        .url(loginUrl)
                        .addParams("__VIEWSTATE", "dDwxNTMxMDk5Mzc0Ozs+1m5zK+m2r3JHbmoorhWV2gLXNgc=")
                        .addParams("txtUserName", stuId)
                        .addParams("TextBox2", stuPsw)
                        .addParams("txtSecretCode", code)
                        .addParams("RadioButtonList1", "%D1%A7%C9%FA")
                        .addParams("Button1", "")
                        .addHeader("Host", "jwxw.gzcc.cn")
                        .addHeader("Referer", "http://jwxw.gzcc.cn/default2.aspx")
                        .build()
                        .connTimeOut(5000)
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e) {
                            }

                            @Override
                            public void onResponse(String response) {
                                if (response.contains("验证码不正确")) {
                                    initCode();
                                    et_code.setText("");
                                    Toast.makeText(getApplicationContext(), "验证码不正确",
                                            Toast.LENGTH_SHORT).show();
                                } else if (response.contains("密码错误")) {
                                    initCode();
                                    et_code.setText("");
                                    LoginPassword.setText("");
                                    Toast.makeText(getApplicationContext(), "密码错误",
                                            Toast.LENGTH_SHORT).show();
                                } else if (response.contains("用户名不存在")) {
                                    initCode();
                                    et_code.setText("");
                                    AccountNumber.setText("");
                                    Toast.makeText(getApplicationContext(), "用户名不存在",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    BmobUser.loginByAccount(stuId, stuPsw, new LogInListener<MyUser>() {
                                        @Override
                                        public void done(MyUser user, BmobException e) {
                                            if (user != null) {
                                                MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
                                                if (userInfo != null) {
                                                    stepService.CURRENT_STEP = userInfo.getStep();
                                                }
                                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                MyUser bu = new MyUser();
                                                bu.setUsername(stuId);
                                                bu.setPassword(stuPsw);
                                                bu.setStep(0);
                                                bu.setRanking(0);
                                                bu.signUp(new SaveListener<MyUser>() {
                                                    @Override
                                                    public void done(MyUser s, BmobException e) {
                                                        if (e == null) {
                                                            MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
                                                            if (userInfo != null) {
                                                                stepService.CURRENT_STEP = userInfo.getStep();
                                                            }
                                                            Toast.makeText(getApplicationContext(), "注册成功",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(SignIn.this, MainActivity.class);
                                                            startActivity(intent1);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "注册失败",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });


//                                    /*这里判断是否第一次登录*/
//                                    sharedPreferences = getSharedPreferences("sharedPreferences", 0);
//                                    user_first = sharedPreferences.getBoolean("FIRST", true);
//                                    if (user_first) {
//                                        //第一次登录，正常加载
//                                        MyUser bu = new MyUser();
//                                        bu.setUsername(stuId);
//                                        bu.setPassword(stuPsw);
//                                        bu.setStep(0);
//                                        bu.setRanking(0);
//                                        //注意：不能用save方法进行注册
//                                        bu.signUp(new SaveListener<MyUser>() {
//                                            @Override
//                                            public void done(MyUser s, BmobException e) {
//                                                if (e == null) {
//                                                    MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
//                                                    if (userInfo != null) {
//                                                        stepService.CURRENT_STEP = userInfo.getStep();
//                                                    }
//                                                    Toast.makeText(getApplicationContext(), "注册成功",
//                                                            Toast.LENGTH_SHORT).show();
//                                                    Intent intent1 = new Intent(SignIn.this, MainActivity.class);
//                                                    startActivity(intent1);
//                                                    finish();
//                                                } else {
//                                                    Toast.makeText(getApplicationContext(), "注册失败",
//                                                            Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        BmobUser.loginByAccount(stuId, stuPsw, new LogInListener<MyUser>() {
//                                            @Override
//                                            public void done(MyUser user, BmobException e) {
//                                                if (user != null) {
//                                                    MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
//                                                    if (userInfo != null) {
//                                                        stepService.CURRENT_STEP = userInfo.getStep();
//                                                    }
//                                                    Intent intent = new Intent(SignIn.this, MainActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                } else {
//                                                    Toast.makeText(getApplicationContext(),
//                                                            "用户登陆失败", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                        //如果不是第一次登录，直接跳转到下一个界面
//                                        Intent intent2 = new Intent(SignIn.this, MainActivity.class);
//                                        startActivity(intent2);
//                                        finish();
//                                    }
//                                    sharedPreferences.edit().putBoolean("FIRST", false).commit();
                                }
                            }
                        });


            }
        });
        New_user_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user_registration = new Intent();
                user_registration.setClass(SignIn.this, Registration.class);
                startActivity(user_registration);
                finish();
            }
        });
        Forget_the_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeUrl += '?';
                //修改url后重庆请求验证码
                initCode();
            }
        });
    }

    private void initCode() {
        OkHttpUtils
                .get()
                .url(codeUrl)
                .build()
                .connTimeOut(5000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        iv_code.setImageBitmap(response);
                    }
                });
    }
}
