package frost.chucknorrisandcats;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends Activity {

    private GridLayout gridLayout;
    private int columnCount;
    private int rowCount;
    private int imgCount;
    private int imgSize;
    private int imgDownloaded;
    private int downloadedImages;

    private Button catButton;
    private Button chuckNorrisButton;
    private String urlCN = "https://api.icndb.com/jokes/random";
    private TextView joke;

    private String imageUrl[];
    private ImageView img[][];
    private ProgressBar progressBar;
    private TextView downloadCount;
    private Boolean downloaded;

    private SharedPreferences mPrefs;

    private final String CAT_API = "http://thecatapi.com/api/images/get?format=xml&results_per_page=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeTitleBar();
        setContentView(R.layout.activity_main);

        init();

    }

    private void removeTitleBar() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor e = mPrefs.edit();
        e.putString("qoute", String.valueOf(joke.getText()));
        e.commit();
    }

    private void init() {
        setGridParameters();
        gridLayout = (GridLayout) findViewById(R.id.gallery);
        catButton = (Button) findViewById(R.id.catsButton);
        chuckNorrisButton = (Button) findViewById(R.id.chuckNorrisButton);
        joke = (TextView) findViewById(R.id.quote);
        img = new ImageView[rowCount][columnCount];
        imageUrl = new String[ imgCount ];
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        downloadCount = (TextView) findViewById(R.id.downloadCount);
        mPrefs = this.getSharedPreferences("SHARED_CHUCK_NORRIS_AND_CATS", Context.MODE_PRIVATE);

        setProgressVisibilityTo(View.GONE);
        calculeteImageParameters();
        addImageViews();
        addButtonHandlers();
        loadImages();
        checkQoute();
    }

    private void checkQoute() {
        String qoute = mPrefs.getString("qoute", null );

        if( qoute != null ){
            joke.setText( qoute );
        }
    }

    private void setGridParameters() {
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();

        if( orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270 ){
            columnCount = 5;
            rowCount = 4;
        }
        else{
            columnCount = 2;
            rowCount = 10;
        }
        imgCount = columnCount * rowCount;
    }

    private void calculeteImageParameters() {
        Point size = new Point();
        int screenWidth;

        getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        imgSize = (screenWidth) / columnCount; //( screenWidth - (columnCount+1) * 5))
    }

    private void addImageViews() {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                img[i][j] = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(i, GridLayout.CENTER),
                        GridLayout.spec(j, GridLayout.CENTER));
                params.width = imgSize;
                params.height = imgSize;
                img[i][j].setLayoutParams(params);
                gridLayout.addView(img[i][j]);
            }
        }
    }

    private void addButtonHandlers() {
        addCatButtonHandler();
        addChuckNorrisHandler();
    }

    private void addCatButtonHandler() {
        catButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImages();
            }
        });
    }

    private void addChuckNorrisHandler() {
        chuckNorrisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadQoute();
            }
        });
    }

    private void downloadQoute() {
        Ion.with(this)
                .load(urlCN)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Response res = Response.parseJSON(result);
                        joke.setText(res.getValue().getJoke());
                    }
                });
    }

    private void downloadImages() {
        downloadAdresses();
    }

    @Nullable
    private Document parseStringToXml(String result) {
        InputStream is = new ByteArrayInputStream(result.getBytes());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            return doc;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    private void loadImages() {
        int index = 0;

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                loadImage(index, i, j);
                index++;
            }
        }
    }

    private void loadImage(int index, int i, int j) {
        File file = new File(getFilesDir() + "/img" + index + ".jpg");

        Picasso.with(this).invalidate(file);
        Picasso.with(this).load(file)
                .resize(imgSize, imgSize)
                .centerCrop()
                .into(img[i][j]);
    }

    private void downloadAdresses() {
        Ion.with(this)
                .load(CAT_API + imgCount)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Document doc = parseStringToXml(result);
                        NodeList nList = doc.getElementsByTagName("url");

                        for (int i = 0; i < nList.getLength(); i++) {
                            imageUrl[i] = nList.item(i).getTextContent();
                        }
                        downloadImagesWithProgress();
                    }
                });
    }


    private void downloadImagesWithProgress() {
        imgDownloaded = 0;
        setProgressVisibilityTo(View.VISIBLE);

        for (int index = 0; index < imgCount; index++) {
            File file = new File(getFilesDir() + "/img" + index + ".jpg");

            Ion.with(this)
                    .load(imageUrl[index])
                    .progressBar(progressBar)
                    .progressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {
                            downloadCount.setText("" + imgDownloaded + " / " + imgCount);
                        }
                    })
                    .write(file)
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {
                            imgDownloaded++;

                            if (imgDownloaded == imgCount) {
                                loadImages();
                                setProgressVisibilityTo(View.GONE);
                            }
                        }
                    });
        }
    }

    public void setProgressVisibilityTo(int progressVisibilityTo) {
        progressBar.setVisibility(progressVisibilityTo);
        downloadCount.setVisibility(progressVisibilityTo);
    }
}


//                        try {
//                            JSONObject json = XML.toJSONObject( result );
//                            Log.d("JOSN: ", json.toString() );
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }