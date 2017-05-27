package frost.arkanoid;

import android.graphics.Rect;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by seba on 13.05.2017.
 */

public class Spodek {

    private int x;
    private int y;
    private int width;
    private int height;

    private int maxX;
    private int minX = 0;

    private int maxY = 0;

    private int goal;

    private int speed ;
    private Rect spodek;
    private boolean moving;
    private boolean touchMoving;
    private boolean accMoving;




    public Spodek(int screenX, int screenY ){
        maxX = screenX;
        minX = 0;
        width = screenX/4;
        height = screenY/25;
        x = screenX/2;
        maxY = screenY;
        moving = false;
        speed = screenX/90;
        spodek = new Rect( x - width/2 , screenY - height,   x + width/2, screenY);
    }


    public Rect getSpodek() {
        return spodek;
    }

    public void update( ){
         if( moving ) {
             if (goal < spodek.centerX()) {
                 spodek.right -= speed;
                 spodek.left -= speed;
             }
             if (goal > spodek.centerX()) {
                 spodek.right += speed;
                 spodek.left += speed;
             }

             if (spodek.right > maxX) {
                 spodek.right = maxX;
                 spodek.left = maxX - width;
             }

             if (spodek.left < minX) {
                 spodek.left = minX;
                 spodek.right = minX + width;
             }
         }
    }

    public void startMoving(int x) {
        moving = true;
        touchMoving = true;
        goal = x;
    }

    public void stopMoving() {
        moving = false;
        touchMoving = false;
    }

    public boolean isMoving() {
        return moving;
    }

    public int getGoal() {
        return goal;
    }

    public void stopMovingFromAcceleromer() {
        accMoving = false;
        if( !touchMoving ){
            moving = false;
        }
    }

    public void startMovingFromAccelerometer(int i) {
        accMoving = true;
        moving = true;
        if( !touchMoving ){
            goal = i == 1 ? maxX : 0;
        }

    }

    public void changeWidth(int i) {
        if( i == 1){
            width = min( maxX/4 + 100, width + 100);
        }
        else{
            width = max( maxX/4 - 100, width - 100);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                width = maxX/4;
            }
        }, 5000);
    }

    public void setWidth(int level) {
        width = maxX/(4 + level);
        speed = maxX/90 + level/2;
        spodek.right = maxX/2 + width/2;
        spodek.left = maxX/2 - width/2;
        Log.d("RIGHT", String.valueOf(spodek.right));
        Log.d("LEFT ", String.valueOf(spodek.left));
        Log.d("max ", String.valueOf(maxX/2));

    }
}
