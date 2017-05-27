package frost.arkanoid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

/**
 * Created by seba on 13.05.2017.
 */

public class GameView extends View   {
    
    private Paint paint;

    private int screenX;
    private int screenY;

    public boolean playing;
    private boolean pause;
    private boolean gameOver;
    private boolean initialMode;

    public Spodek spodek;
    public Ball ball;

    private int starNumber = 100;
    private ArrayList<Star> stars = new ArrayList<Star>();
    private int blockX = 10;
    private int blockY = 20;
    private Block[][] blocks;
    private int magicBallsSize = 50;
    private int magicBallsCount;
    private ArrayList<MagicBall> magicBalls = new ArrayList<MagicBall>();
    private int margin = 50;

    private int score;
    private int lives;
    private int level = 1;
    
    private RectF[] liveRects;

    private Rect liveCollider;
    SharedPreferences sharedPreferences;


    private Bitmap playButton;

    private Context context;
    private boolean win;

    public GameView(Context context, int screenX, int screenY) {
        super(context);


        this.screenX = screenX;
        this.screenY = screenY;

        this.context = context;
        initialMode = true;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);
        level = sharedPreferences.getInt("LEVEL", 1);
        init();

    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE );
        for( Star s : stars ) canvas.drawCircle( s.getX(), s.getY(), s.getStarWidth(), paint);

        if( !initialMode ) {
            paint.setColor(Color.RED);
            for (Block[] arr : blocks) {
                for (Block b : arr) {
                    if (b.isValid()) {
                        paint.setColor(b.getColor());
                        canvas.drawRect(b.getRect(), paint);
                    }
                }
            }
            paint.setStrokeWidth(15);

            for (MagicBall a : magicBalls) {
                paint.setColor(a.getColor());
                canvas.drawCircle(a.getX(), a.getY(), a.getR(), paint);

            }
            paint.setColor(Color.GREEN);

            canvas.drawRect(spodek.getSpodek(), paint);
            paint.setColor(ball.getColor());
            canvas.drawCircle(ball.getX(), ball.getY(), ball.getR(), paint);
            paint.setColor(Color.YELLOW);
            for (int i = 0; i < lives; i++) {
                canvas.drawRoundRect(liveRects[i], 10f, 10f, paint);
            }

            paint.setColor(Color.YELLOW);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Score: " + score, margin, 40, paint);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Level " + level, screenX / 2, 40, paint);

            if (pause || gameOver || win) {
                paint.setARGB(150, 0, 0, 0);
                canvas.drawRect(0, 0, screenX, screenY, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(150);

                if (pause) {
                    canvas.drawBitmap(playButton,
                            screenX / 2 - playButton.getWidth() / 2,
                            screenY / 2 - playButton.getHeight() / 2,
                            paint);
                }
                if (gameOver) {
                    paint.setTextAlign(Paint.Align.CENTER);

                    int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                    canvas.drawText("Game Over", canvas.getWidth() / 2, yPos, paint);
                }
                if (win) {
                    paint.setTextAlign(Paint.Align.CENTER);

                    int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                    canvas.drawText("You win level " + level, canvas.getWidth() / 2, yPos, paint);
                }
            }
        }
        else{
            paint.setARGB(150, 0, 0, 0);
            canvas.drawRect(0, 0, screenX, screenY, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(150);
            paint.setTextAlign(Paint.Align.CENTER);
            int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
            canvas.drawText("Play now!", canvas.getWidth() / 2, yPos, paint);
        }
    }


    public void pause() {
        pause = true;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putInt("LEVEL", level);
        e.commit();

    }

    public void resume() {
//        pause = false;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);
        level = sharedPreferences.getInt("LEVEL", 1);

    }



    public void update(){
        if( !pause && !gameOver ) {
            for (Star s : stars) {
                s.update();
            }
            if (Rect.intersects(liveCollider, ball.getCollider()) && !win ) {
                lives--;
                if( lives == 0 )gameOver = true;
            }

            spodek.update();
            ball.update();

            if (Rect.intersects(spodek.getSpodek(), ball.getCollider())) {
                ball.collisionWithSpodek(spodek);
            }

            ball.setMutable(true);

            for (Block[] arr : blocks) {
                for (Block b : arr) {
                    if (b.isValid()) {
                        if (Rect.intersects(b.getRect(), ball.getCollider())) {
                            if (ball.isMutable())
                                ball.collisionWithBlock(b.getRect());
                            if( b.isMagic() ){
                                MagicBall mb = new MagicBall( b.getRect().centerX(),
                                                              b.getRect().centerY());
                                int i = 0;
                                while( magicBalls.get(i).isVisible() ){
                                    i++;
                                }
                                magicBalls.set(i, mb);
                            }
                            b.destroy();
                            score++;
                            if( score == blockX * blockY ){
                                win = true;
                            }
                        }
                    }
                }
            }

            for( MagicBall a : magicBalls ){
                a.update();
                if( Rect.intersects( a.getCollider(), spodek.getSpodek() )){
                    a.destroy();
                    magicBallsCount--;
                    handleMagicCollision();
                }
            }
        }
    }

    private void handleMagicCollision() {
        lives = min(3, lives+1 );
    }




    private void addBlocksAndLiveRects() {
        blocks = new Block[blockX][blockY];
        int sizeX = (screenX - margin*2 - (blockY-1)*2)/blockY;
        int sizeY = (screenY/3 -(blockX-1)*2)/blockX;
        int currentX = margin;
        int currentY = margin;
        for( int i = 0 ; i < blockX ; i++ ){

            currentX = margin;
            for( int j = 0; j < blockY ; j++ ){
                blocks[ i ][ j ] = new Block( currentX, currentY, sizeX, sizeY, 0.1f);
                currentX += 2 + sizeX ;
            }
            currentY += 2 + sizeY;

        }

        liveRects = new RectF[3];
        liveRects[ 0 ] = new RectF( margin+(blockY-1)*(2+sizeX), 10, margin+(blockY-1)*(2+sizeX) + sizeX , 40 );
        liveRects[ 1 ] = new RectF( margin+(blockY-2)*(2+sizeX),
                10,
                margin+(blockY-2)*(2+sizeX)+sizeX,
                40 );
        liveRects[ 2 ] = new RectF( margin+(blockY-3)*(2+sizeX),
                10,
                margin+(blockY-3)*(2+sizeX)+sizeX,
                40 );
    }




    public void tap() {
        if( pause ){
            pause = false;
        }
        if( gameOver ){
            init();
        }
        if( win ) {
            level++;
            init();
        }
        if( initialMode ){
            initialMode = false;
        }
    }




    public void init(){
        gameOver = false;
        playing = true;
        pause = false;
        win = false;
        magicBallsCount = 0;

        blockX = level + 3;
        blockY = level + 8;

        for( int i = 0; i < 50 ; i++ ){
            MagicBall mb = new MagicBall(0,0);
            mb.destroy();
            magicBalls.add( mb );
        }
        spodek = new Spodek( screenX, screenY );
        ball = new Ball( screenX, screenY );

        if( stars.size() < starNumber ) {
            for (int i = 0; i < starNumber; i++) {
                Star s = new Star(screenX, screenY);
                stars.add(s);
            }
        }


        addBlocksAndLiveRects();
        score = 0 ;
        lives = 3 ;

        liveCollider = new Rect( 0, screenY, screenX, screenY + 50 );
        playButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.start);

        ball.setSpeed( level - 1) ;
        spodek.setWidth( level -1 );
    }
}
