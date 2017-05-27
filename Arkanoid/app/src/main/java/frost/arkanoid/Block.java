package frost.arkanoid;

import android.graphics.Color;
import android.graphics.Rect;

import java.util.Random;


/**
 * Created by seba on 14.05.2017.
 */

public class Block {

    private boolean valid;
    private boolean magic;

    public boolean isMagic() {
        return magic;
    }

    private int col;

    private Rect rect;
    private int color;
    private float probability;
    private Random generator;

    public Block(int x, int y, int sizeX, int sizeY, float p ){
        rect = new Rect(x, y, x + sizeX, y + sizeY );
        valid = true;
        probability = p;

        col = Color.RED;

        generator = new Random();
        if( generator.nextFloat() < p )
            magic = true;
        else
            magic = false;
    }

    public void destroy(){
        valid = false;
    }

    public Rect getRect() {
        return rect;
    }

    public boolean isValid(){
        return valid;
    }

    public int getColor() {
        if( magic ){
            int R = generator.nextInt(255);
            int G = generator.nextInt(255);
            int B = generator.nextInt(255);
            return (255 & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
        }
        return col;
    }

}
