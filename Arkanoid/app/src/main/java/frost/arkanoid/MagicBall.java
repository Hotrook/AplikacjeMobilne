package frost.arkanoid;

import android.graphics.Rect;

import java.util.Random;

/**
 * Created by seba on 14.05.2017.
 */

public class MagicBall {

    private int x;
    private int y;
    private int r;
    private int speed;
    private int color;
    private boolean visible;

    public int getX() {
        return visible ? x : 100000;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public int getColor() {
        int R = generator.nextInt(255);
        int G = generator.nextInt(255);
        int B = generator.nextInt(255);
        return (255 & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
    }

    public Rect getCollider() {
        return collider;
    }

    private Random generator;
    private Rect collider;

    public MagicBall( int x, int y ){
        this.x = x;
        this.y = y;
        r = 20;
        visible = true;

        generator = new Random();
        speed = 1 + generator.nextInt(3);
        collider = new Rect( x - r, y - r, x + r, y + r);
    }

    public void update(){
        y += speed;
        collider.top = y - r;
        collider.bottom = y + r;
    }

    public void destroy(){
        visible = false;
    }


    public boolean isVisible() {
        return visible;
    }
}
