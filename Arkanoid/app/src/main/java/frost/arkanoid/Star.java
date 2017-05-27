package frost.arkanoid;

import java.util.Random;

/**
 * Created by seba on 14.05.2017.
 */

public class Star {

    private int x;
    private int y;
    private int r;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;
    private int speed;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Star(int screenX, int screenY ){
        minX = minY = 0;
        maxX = screenX;
        maxY = screenY;

        Random generator = new Random();

        speed = generator.nextInt( 2 ) + 1;
        x = generator.nextInt( screenX );
        y = generator.nextInt( screenY );

    }

    public void update(){
        y += speed;

        if( y > maxY ){
            y = 0;
            Random generator = new Random();
            x = generator.nextInt( maxX );
            speed = generator.nextInt(2) + 1;
        }
    }

    public float getStarWidth(){
        float minY = 1.0f;
        float maxY = 4.0f;

        Random generator = new Random();
        float finalX = generator.nextFloat()*( maxY - minY ) + minY;
        return finalX;
    }
}
