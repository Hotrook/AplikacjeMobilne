package frost.arkanoid;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import static android.R.attr.value;
import static android.R.attr.x;
import static android.R.attr.y;
import static java.lang.Math.abs;

public class GameActivity extends AppCompatActivity  implements SensorEventListener{


    private GameView gameView;
    private Handler handler;
    private Paint paint;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastUpdate;
    private float[] valuesAccelerometer;
    private float[] rotationMatrix;
    private float[] valuesMagneticField;
    private Sensor mSensor2;

    private float init_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();

        display.getSize( size );

        gameView = new GameView( this, size.x, size.y );

        setContentView(gameView);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Handler h = new Handler(Looper.getMainLooper());
                h.postDelayed(this, 16 );
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        gameView.update();
                        gameView.invalidate();

                    }
                });
            }
        });
        thread.start();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor2 = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        lastUpdate = 0;
        init_z = -100;
    }


    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
        mSensorManager.unregisterListener(this);
    }



    @Override
    protected void onResume(){
        super.onResume();
        gameView.resume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensor2, SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public boolean onTouchEvent( MotionEvent event ){
        float x = event.getX();
        float y = event.getY();

        switch( event.getAction() ){
            case MotionEvent.ACTION_DOWN:
                gameView.spodek.startMoving((int) x);
                break;

            case MotionEvent.ACTION_MOVE:
                gameView.spodek.startMoving((int) x);
                break;

            case MotionEvent.ACTION_UP:
                gameView.spodek.stopMoving();
                gameView.tap();
                break;
        }

        return super.onTouchEvent(event);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        long currTime =System.currentTimeMillis();

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if ((currTime - lastUpdate) >= 20) {
            if( abs( y ) > 1.2f){
                if( y < 0 ){
                    gameView.spodek.startMovingFromAccelerometer( -1 );
                }
                else{
                    gameView.spodek.startMovingFromAccelerometer( 1 );
                }
            }
            else{
                gameView.spodek.stopMovingFromAcceleromer();
            }


            if( init_z == -100 ){
                init_z = z;
            }

            if( z - init_z > 1.5 ){
                gameView.ball.increaseSpeed();
            }
            else{
                gameView.ball.stopIncreaseSpeed();
            }
            lastUpdate = currTime;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
