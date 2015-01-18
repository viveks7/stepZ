package vivek.dev.hplus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import static android.util.FloatMath.sqrt;

public class accelServe extends Service implements SensorEventListener {

    private SensorManager sManage;
    private Sensor sAccel;
    CharSequence txt;
    float a=0, b=0, c=0, old_acc=0, differnce=0;
    long fileName=0, t;
    String fName;
    OutputStreamWriter mOutWriter;
    FileOutputStream fout;
    File mFile;
    private Looper mSLoop;
    private ServiceHandler mSHandler;
    double ti;
    private float[] array;
    private float peak,low;
    private int flag=0, is_true_peak=0, is_true_low=0, change=0;
    TextView tv;




    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg){

        }
    }
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mSLoop = thread.getLooper();
        mSHandler = new ServiceHandler(mSLoop);
        fileName = System.currentTimeMillis();
        fName = Long.toString(fileName);
        try {
            mFile = new File("/sdcard/" + fName + ".txt");
            mFile.createNewFile();
            fout = new FileOutputStream((mFile));
            mOutWriter = new OutputStreamWriter(fout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sAccel = sManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManage.registerListener(this, sAccel, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        Message msg=mSHandler.obtainMessage();
        msg.arg1=startId;
        mSHandler.sendMessage(msg);
        t=System.currentTimeMillis();
        return START_STICKY;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mSenseor=sensorEvent.sensor;
        if(mSenseor.getType()==Sensor.TYPE_ACCELEROMETER){
            float x=sensorEvent.values[0];
            float y= sensorEvent.values[1];
            float z= sensorEvent.values[2];
            long d=System.currentTimeMillis();
            long diff=(d-t);
            ti=(((double)diff/(double)1000));
            float new_acc=sqrt((x*x)+(y*y)+(z*z));

            if((new_acc-old_acc)>0.0){
                if(is_true_low<6){
                    is_true_low=0;
                }else{
                    low=old_acc;
                    is_true_low=0;
                    if((peak-low)>8 && (peak-low)<15){
                        data.status=1;
                    }else if((peak-low)>35 && (peak-low)<75){
                        data.status=2;
                    }else if((peak-low)<8){
                        data.status=0;
                    }
                }
               /* if(is_true_peak==5){
                    is_true_peak=0;
                    peak=old_acc;
                }*/
                is_true_peak++;
            }else if((new_acc-old_acc)<0.0){
                if(is_true_peak<6){
                    is_true_peak=0;
                }else{
                    peak=old_acc;
                    is_true_peak=0;

                }
               /* if(is_true_low==5){
                    is_true_low=0;
                    low=
                }*/
                is_true_low++;
            }
            /*if(i==10){
                i=0;
            }
            array[i]=k;

            if(count==10){
                peak=setPeak(array);
                count=0;
                i=1;
                flag=1;
            }
            if(k>peak) {
                if (((array[i] - array[i - 1]) > 2) && flag == 1) {
                    if(set==0 && flag==1) {
                        set=1;
                        if(set==1) {
                            count = 0;
                            i = 1;
                            set=0;
                        }
                    }
                    if(set!=1){
                        step++;
                    }
                }
            }


            count++;
            i++;
            flag=1;*/

            //if((x-a)>1 || (y-b)>1 || (z-c)>1 || (x-a)<-1 || (y-b)<-1 || (z-c)<-1) {

                txt = new_acc +" "+data.status+" "+(peak-low)+"\n";
                try {
                    mOutWriter.append(txt);
                }catch(Exception e){
                    e.printStackTrace();
                }
                old_acc=new_acc;
                data.old_status=data.status;


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this,"Stoping...", Toast.LENGTH_SHORT).show();
        sManage.unregisterListener(this);
        //save();
        try{
            mOutWriter.close();
            fout.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public float setPeak(float array[]){
        float p=0;
        int i=1;
        //p=array[0];
        for(i=1;i<11;i++){
            p=p+array[i];
        }
        return p;
    }
}
