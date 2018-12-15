package cn.bluemobi.dylan.step.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bmobJava.MyUser;
import cn.bluemobi.dylan.step.step.UpdateUiCallBack;
import cn.bluemobi.dylan.step.step.service.StepService;
import cn.bluemobi.dylan.step.step.utils.SharedPreferencesUtils;
import cn.bluemobi.dylan.step.view.StepArcView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 记步主页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SignIn flag_sign=new SignIn();
    StepService stepService=new StepService();
    MyUser myUser=new MyUser();

    private TextView tv_data;
    private TextView tv_set;
    private TextView tv_isSupport;

    private TextView UploadingData;
    private TextView rankings;

    private StepArcView cc;
    private SharedPreferencesUtils sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //进行初始化操作Bmob：
        Bmob.initialize(this, "df877b92936d74812d2b67a4c6c8c43d");

        assignViews();
        initData();
        addListener();
    }

    private void assignViews() {
        tv_data = (TextView) findViewById(R.id.tv_data);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_isSupport = (TextView) findViewById(R.id.tv_isSupport);
        cc = (StepArcView) findViewById(R.id.cc);
        UploadingData=(TextView)findViewById(R.id.UploadingData);
        rankings=(TextView)findViewById(R.id.rankings);
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        tv_isSupport.setText("计步中...");
        setupService();
    }

    private boolean isBind = false;
    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void addListener() {
        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);
        UploadingData.setOnClickListener(this);
        rankings.setOnClickListener(this);
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
            cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepService.getStepCount());
            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
                    cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
                }
            });
        }
        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, SetPlanActivity.class));
                break;
            case R.id.tv_data:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.UploadingData:
                MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
                if(userInfo!=null){
                    String ID=userInfo.getObjectId();
                    Integer stepnumber=stepService.CURRENT_STEP;
                    myUser.setStep(stepnumber);
                    myUser.update(ID, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(getApplicationContext(), "更新成功"
                                        , Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "更新失败 "
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "获取用户信息失败，请从新上传！ "
                            , Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.rankings:
                startActivity(new Intent(this, Rankings.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.AddFriends:
                finish();
                break;
            case R.id.Signout:
                flag_sign.UserBuffering=false;
                Intent signout=new Intent();
                signout.setClass(MainActivity.this,SignIn.class);
                startActivity(signout);
                finish();
                break;
            case R.id.ClearStep:
                MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
                if(userInfo!=null){
                    myUser.setStep(0);
                    stepService.CURRENT_STEP=0;
                    String ID=userInfo.getObjectId();
                    myUser.update(ID, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(getApplicationContext(), "用户步数重新置0成功！"
                                        , Toast.LENGTH_SHORT).show();
                             }else{
                              }
                          }
                    });
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
