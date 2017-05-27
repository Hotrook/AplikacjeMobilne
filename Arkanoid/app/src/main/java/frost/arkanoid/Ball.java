package frost.arkanoid;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by seba on 14.05.2017.
 */

public class Ball {


    private int x;
    private int y;
    private int r;


    private int maxX;
    private int minX;


    private int maxY;

    private boolean mutable;

    private int minY;

    private float verticalMove;
    private float horizontalMove;

    private int speed;
    private int regularSpeed;
    private int superFastSpeed;

    private Rect collider;
    private boolean magicMode;

    public Ball( int screenX, int screenY ){
        maxX = screenX;
        minY = minX = 0;
        maxY = screenY;
        r = 20;
        x = screenX/2;
        y = 600 - r;
        speed = 8;
        regularSpeed = 8;
        superFastSpeed = 12;
        collider = new Rect( x - r , y - r , x + r, y + r );
        verticalMove = 1;
        horizontalMove = 0;
        magicMode = false;

    }


    public void update(){

        x += horizontalMove * speed;
        y += verticalMove * speed ;

        if( x - r < minX ){
            x = minX + r;
            switchHorizontal();
        }
        if( x + r > maxX ){
            x = maxX - r;
            switchHorizontal();
        }
        if( y - r < minY ){
            y = minY + r;
            switchVertical();
        }
        if( y + r > maxY ){
            switchVertical();
        }

        updateCollider();

    }

    private void updateCollider() {
        collider.left = x - r;
        collider.right = x + r;
        collider.top = y - r;
        collider.bottom = y + r;
    }

    private void switchVertical() {
        verticalMove *= -1;
    }

    private void switchHorizontal() {
        horizontalMove *= -1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public Rect getCollider() {
        return collider;
    }

    public void collisionWithSpodek(Spodek spodek) {
        verticalMove = -1 * abs(verticalMove);
        if( spodek.isMoving() ){
            Random generator = new Random();

            if( spodek.getGoal() < spodek.getSpodek().centerX() ){
                horizontalMove -= generator.nextFloat()*0.5f + 0.1f;
                horizontalMove = (float) max( horizontalMove, -0.95 );
            }
            else {
                horizontalMove += generator.nextFloat()*0.5f+ 0.1f;
                horizontalMove = (float) min( horizontalMove, 0.95 );
            }
            verticalMove = (float) (sqrt( 1.0 - pow( horizontalMove, 2 ) ) * -1.0);

        }
    }




    public void collisionWithBlock(Rect rect) {

        if( y > rect.bottom || y < rect.top ){
            switchVertical();
        }
        else if( x < rect.left || x > rect.right ){
            switchHorizontal();
        }
        mutable = false;
    }

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public void stopIncreaseSpeed() {
        speed = regularSpeed;
    }

    public void increaseSpeed() {
        speed = regularSpeed+5;

    }

    public void changeSpeed() {

        magicMode = true;
        speed = 15;
        r = 30;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                speed = regularSpeed;
                magicMode = false;
                r = 20;
            }
        }, 5000);
    }

    public int getColor(){
        if( magicMode ){
            Random generator = new Random();
            int R = generator.nextInt(255);
            int G = generator.nextInt(255);
            int B = generator.nextInt(255);
            return  (255 & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
        }
        else
            return Color.GREEN;
    }

    public void setSpeed(int speed) {
        regularSpeed = 8 + speed/2;
        this.speed = regularSpeed;
    }
}
