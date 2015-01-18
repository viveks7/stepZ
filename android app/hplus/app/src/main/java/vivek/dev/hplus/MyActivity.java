package vivek.dev.hplus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MyActivity extends Activity {

    ImageView button;
    TextView tv, tv1, name;
    RelativeLayout rl3;
    ActionBar actionBar;
    Thread check_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        button = (ImageView) findViewById(R.id.button);
        tv=(TextView)findViewById(R.id.textView);
        tv1=(TextView)findViewById(R.id.tv1);
        name=(TextView)findViewById(R.id.about);
        rl3=(RelativeLayout)findViewById(R.id.rl3);
        actionBar=getActionBar();
        actionBar.hide();
        if(Build.VERSION.SDK_INT>20) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_status));
        }
        if (!isMyServiceRunning(accelServe.class)) {
            tv1.setText("Start");
        } else if(isMyServiceRunning(accelServe.class)){
            tv1.setText("Stop");
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state_listener();
            }

        });
    }

    public void state_listener(){
        check_state=new Thread(new Runnable() {
            @Override
            public void run() {
              if(Thread.interrupted()){
                  start_listener();
              }else{
                  start_listener();
              }
            }
        });

        Intent intent = new Intent(MyActivity.this, accelServe.class);
        if (!isMyServiceRunning(accelServe.class)) {
            startService(intent);
            tv1.setText("Stop");
            rl3.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.VISIBLE);
            check_state.start();
        } else if(isMyServiceRunning(accelServe.class)){
            tv1.setText("Start");
            rl3.setVisibility(View.VISIBLE);
            tv.setVisibility(View.INVISIBLE);
            tv.setText(" ");
            stopService(intent);
        }
    }

    public void start_listener(){

                    while(isMyServiceRunning(accelServe.class)) {
                        if (data.status == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText("Still....");
                                }
                            });
                            }else if (data.status == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText("Walking....");
                                }
                            });
                        } else if (data.status == 2) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText("Running....");
                                }
                            });

                        }
                    }



    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
   public void onPause(){
        super.onPause();
        if(isMyServiceRunning(accelServe.class)){
            try {
                check_state.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onResume(){
        super.onResume();
        if(isMyServiceRunning(accelServe.class)){
            try {
                check_state.notify();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


}
