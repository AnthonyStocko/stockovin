package net.stockovin;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.features2d.DescriptorExtractor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static android.content.ContentValues.TAG;
import static android.os.Environment.DIRECTORY_PICTURES;
import static java.lang.Thread.sleep;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends Activity {

    final User user = SharedPrefManager.getInstance(this).getUser();

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    private static final int PERMISSION_REQUEST_CODE = 100;
    TextView textViewUsername, textViewEmail, textViewNbBottle, textViewPourcent, textViewNbSuggest;
    private ImageView imgAnim, imgAnim2, imgMiFoto;
    private Handler handlerAnimationCIMG;
    ProgressBar progressBar ;
    private int requestCode = 0;

    private static Bitmap  bmpimg1, bmpimg2;

    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;

    private static int descriptor = DescriptorExtractor.BRISK;
    private static int min_dist = 100;
    int g_nbBot = 0;
    int g_nbSuggest = 0;
    int g_pourcentAbs= 0;

    private CustomGauge gauge1, gauge2;

    private final OkHttpClient client = new OkHttpClient();


    Uri imageUri;

    TourGuide mTourGuideHandler;
    TourGuide mTourGuideHandlerDegust;
    LinearLayout linlay5;

    RelativeLayout rellayAll, buttonDegust, imgBotCave,
            imgSuggest, buttonFriend, buttonSuggest ;

    Boolean newUser;
    Boolean newUserCatActivity;
    Boolean mesGauge;

    private InterstitialAd mInterstitialAd;
    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_ID = "ca-app-pub-8989015951151317/6357990210";

    private AdView mAdView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);
        Log.d("TAG", "The interstitial 1 " + AdRequest.DEVICE_ID_EMULATOR);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //mInterstitialAd.loadAd(adRequest);
        // Begin loading your interstitial.
        //mInterstitialAd.show();
        Log.d("TAG", "The interstitial 1.2");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest2);

        //Si le utilisateur n'est pas loggué
        //On démarre l'activité de login
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        final User user = SharedPrefManager.getInstance(this).getUser();

        newUser = getIntent().getBooleanExtra("newUser", false);
        newUserCatActivity = newUser;
        mesGauge = false;

        textViewNbBottle = findViewById(R.id.textViewNbBottle);
        textViewNbSuggest = findViewById(R.id.nbSuggest);
        textViewUsername = findViewById(R.id.textViewUsername);
        //textViewEmail = findViewById(R.id.tv_address);
        textViewPourcent = findViewById(R.id.textViewPourcent);
        progressBar = findViewById(R.id.progressBarMenu);
        textViewUsername.setText(user.getUsername());
        //textViewEmail.setText(user.getEmail());
        gauge1 = findViewById(R.id.gauge1);
        gauge2 = findViewById(R.id.gauge2);
        linlay5 = findViewById(R.id.linlay5);
        buttonDegust = findViewById(R.id.buttonDegust);
        buttonFriend = findViewById(R.id.imgFriend);
        buttonSuggest = findViewById(R.id.imgSuggest);
        imgBotCave = findViewById(R.id.imgBotCave);
        rellayAll = findViewById(R.id.rellayAll);
        handlerAnimationCIMG = new Handler();
        imgAnim = findViewById(R.id.imgAnim);
        imgAnim2 = findViewById(R.id.imgAnim2);
        imgMiFoto = findViewById(R.id.imgMiFoto);

        //textViewEmail.setVisibility(View.INVISIBLE);

        if (checkPermission()) {
            //
        }else{
            requestPermission();
        }

        /*String token = SharedPrefManager.getInstance(this).getDeviceToken();

        /*if (token == null) {
            MyFirebaseInstanceIDService MyFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
            MyFirebaseInstanceIDService.onTokenRefresh();
        }*/

        if (checkPermission()) {
            Bitmap selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() + ".jpg");
            imgMiFoto.setImageBitmap(selectedImage);

            File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() + ".jpg");

            if (!file.exists()) {
                Glide.with(this).load(R.drawable.ic_user3).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
                imgMiFoto.setPadding(30, 30, 30, 30);
            }
            else {
                Glide.with(this).load(selectedImage).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
                imgMiFoto.setPadding(3, 3, 3, 3);
            }
        }
        else{
            requestPermission();
        }

        this.runnableAnim.run();
        this.handlerAnimationCIMG.removeCallbacks(runnableAnim);
        recupNbBouteille(this.runnableGauge);

        sendToken(user.getEmail());

        if (newUser == true) {

            ToolTip toolTipCave = new ToolTip().setTitle("Bingo!").setDescription("Vous pouvez enregistrer vos bouteilles ici");
            toolTipCave.setGravity(Gravity.TOP);

            mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                    .setToolTip(toolTipCave)
                    .setOverlay(new Overlay())
                    .playOn(imgBotCave);

        }

        findViewById(R.id.rellayAll).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (mesGauge == true){
                     mTourGuideHandlerDegust.cleanUp();
                     mesGauge = false;
                 }
             }
         });

        findViewById(R.id.imgBotCave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newUser == true){
                    mTourGuideHandler.cleanUp();
                    displayToolTipDegut();

                }
                else {
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(500);
                    findViewById(R.id.linlayCave).startAnimation(alpha);
                    Intent CategorieActivity = new Intent(getApplicationContext(), CategorieActivity.class);

                    finish();
                    CategorieActivity.putExtra("newUserCatActivity", newUserCatActivity);
                    startActivity(CategorieActivity);
                }
            }
        });

        findViewById(R.id.imgFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newUser == true)
                {
                    mTourGuideHandlerDegust.cleanUp();
                    displayToolTipSuggest();
                }
                else {
                    // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(500);
                    findViewById(R.id.linlayFriend).startAnimation(alpha);
                    Intent UserFriendListActivity = new Intent(getApplicationContext(), UserFriendListActivity.class);

                    finish();
                    //UserFriendListActivity.putExtra("newUserCatActivity", newUserCatActivity);
                    startActivity(UserFriendListActivity);
                }

            }
        });

        findViewById(R.id.imgSuggest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newUser == true)
                {
                    mTourGuideHandlerDegust.cleanUp();
                    newUser = false;
                }
                else {

                    // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(500);
                    findViewById(R.id.linlaySuggest).startAnimation(alpha);
                    Intent suggestListActivity = new Intent(getApplicationContext(), BottleListSuggest.class);

                    finish();
                    //UserFriendListActivity.putExtra("newUserCatActivity", newUserCatActivity);
                    startActivity(suggestListActivity);
                }

            }
        });

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails
                //afficheMessage(errorCode);

            }
        });

       /*mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                Log.d("TAG", "AlphaAnimation");
                mInterstitialAd.show();
            }
        });
*/
        findViewById(R.id.ButtonConf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.ButtonConf).startAnimation(alpha);

                Intent ProfileConfigActivity = new Intent(getApplicationContext(), ProfileConfigActivity.class);
                finish();
                ProfileConfigActivity.putExtra("newUserCatActivity", newUserCatActivity);
                startActivity(ProfileConfigActivity);
            }
        });

        findViewById(R.id.buttonDegust).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newUser == true)
                {
                    mTourGuideHandlerDegust.cleanUp();
                    displayToolTipFriend();
                }
                else captureImage();
            }
        });

        findViewById(R.id.gauge1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayToolTipGaugeBot();
            }
        });

        findViewById(R.id.gauge2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayToolTipGaugeInfo();
            }
        });

        findViewById(R.id.imgMiFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File Photo_Signa = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/");
                File file = new File(Photo_Signa.getPath(), "profile" + user.getId() + ".jpg");
                Intent i = new Intent(Intent.ACTION_VIEW);
                String ext = file.getName().substring(file.getName().indexOf(".") + 1).toLowerCase();

                if(file.exists()) {
                    if (ext.equals("jpg")) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        i.setDataAndType(photoURI, "image/jpg");
                    }

                    try {
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(i);
                        // Et s'il n'y a pas d'activité qui puisse gérer ce type de fichier
                    } catch (ActivityNotFoundException e) {
                        //Toast.makeText(this, "Oups, vous n'avez pas d'application qui puisse lancer ce fichier", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else {
                    Intent ProfileConfigActivity = new Intent(getApplicationContext(), ProfileConfigActivity.class);
                    finish();
                    ProfileConfigActivity.putExtra("newUserCatActivity", newUserCatActivity);
                    startActivity(ProfileConfigActivity);
                }
            }
        });

    }

    //private void scheduleNotification(Notification notification) {
    private void scheduleNotification(int nb_bot) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        //notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationPublisher.NB_BOT, nb_bot);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime();

        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, 5);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 1);
        }

        //Log.d("Profile activity", "calendar " + calendar.getTime());
        //Toast.makeText(this, "calendar:"+ calendar.getTime() , Toast.LENGTH_LONG).show();

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  AlarmManager.INTERVAL_DAY, pendingIntent);

        // Cancel alarms
        /*try {
            alarmManager.cancel(pendingIntent);
            Log.d("Profile activity", "AlarmManager was canceled. " );
        } catch (Exception e) {
            Log.d("Profile activity", "AlarmManager update was not canceled. " );
        }*/

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private Notification getNotification(int nb_bot) {

        long [] swPattern = new long[] { 0, 500, 110, 500, 110, 450, 110, 200, 110,
                170, 40, 450, 110, 200, 110, 170, 40, 500 };

        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Alerte seuil minimum atteint");
        builder.setContentText("Il vous reste "+ nb_bot + " bouteilles");
        builder.setSmallIcon(R.drawable.icon_bottle);
        builder.setLights(Color.RED, 1000, 1000);
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        builder.setVibrate(swPattern);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("YOUR_PACKAGE_NAME");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "YOUR_PACKAGE_NAME",
                    "YOUR_APP_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this,ProfileActivity.class);
        PendingIntent conPendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(conPendingIntent);

        return builder.build();

    }

    void afficheMessage(int errorCode){
        Toast.makeText(this, "error load " +errorCode, Toast.LENGTH_LONG).show();

    }

    public void displayToolTipDegut(){

        ToolTip toolTip = new ToolTip().setTitle("Tchin!").setDescription("Retrouvez votre bouteille plus facilement dans votre cave en prenant en photo l'étiquette");
        toolTip.setGravity(Gravity.TOP);

        mTourGuideHandlerDegust = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .setOverlay(new Overlay())
                .playOn(buttonDegust);
    }

    public void displayToolTipFriend(){

        Resources res = getResources();

        ToolTip toolTip = new ToolTip().setTitle(res.getString(R.string.friendTourGuide)).setDescription(res.getString(R.string.friendFoundTourGuide));
        toolTip.setGravity(Gravity.TOP);

        mTourGuideHandlerDegust = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .setOverlay(new Overlay())
                .playOn(buttonFriend);
    }

    public void displayToolTipSuggest(){

        Resources res = getResources();

        ToolTip toolTip = new ToolTip().setTitle(res.getString(R.string.friendTourGuide)).setDescription(res.getString(R.string.friendFoundTourGuide));
        toolTip.setGravity(Gravity.TOP);

        mTourGuideHandlerDegust = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .setOverlay(new Overlay())
                .playOn(buttonSuggest);
    }

    public void displayToolTipGaugeBot(){
        if (mesGauge == true){
            mTourGuideHandlerDegust.cleanUp();
            mesGauge = false;
        }

        ToolTip toolTip = new ToolTip().setDescription("Nombre de bouteilles dans votre cave");
        toolTip.setGravity(Gravity.TOP);

        mTourGuideHandlerDegust = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .playOn(gauge1);

        mesGauge = true;
    }

    public void displayToolTipGaugeInfo(){
        if (mesGauge == true){
            mTourGuideHandlerDegust.cleanUp();
            mesGauge = false;
        }

        ToolTip toolTip = new ToolTip().setDescription("Informations renseignées sur les bouteilles");
        toolTip.setGravity(Gravity.TOP);

        mTourGuideHandlerDegust = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .playOn(gauge2);

        mesGauge = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si on a appuyé sur le retour arrière
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public Runnable runnableGauge = new Runnable(){
        @Override
        public void run() {
            gauge1.setEndValue(user.getNbBottleMax());
            gauge1.setStartValue(0);
            gauge1.setValue(1);

            gauge2.setEndValue(100);
            gauge1.setStartValue(0);
            gauge2.setValue(1);

            new Thread() {
                public void run() {
                    for (int i=0;i<=100;i++) {
                        try {
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(finalI <= g_nbBot) gauge1.setValue(finalI);
                                    if(finalI <= g_pourcentAbs) gauge2.setValue(finalI);
                                }
                            });
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

        }
    };

    private Runnable runnableAnim = new Runnable(){
        @Override
        public void run() {
            imgAnim.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    imgAnim.setScaleX(1f);
                    imgAnim.setScaleY(1f);
                    imgAnim.setAlpha(1f);
                }
            });

            imgAnim2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700).withEndAction(new Runnable() {
                @Override
                public void run() {
                    imgAnim2.setScaleX(1f);
                    imgAnim2.setScaleY(1f);
                    imgAnim2.setAlpha(1f);
                }
            });

            handlerAnimationCIMG.postDelayed(runnableAnim, 1500);
        }
    };
/*
    private void recupNbSuggest(final Runnable runnableGauge) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();

        class recupNbBouteille extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    // On convertie la réponse en json object
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    int nbSuggest = 0;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                        }

                        for (int i = 0; i < sous_key.length(); i++) {
                            JSONObject userJson = sous_key.getJSONObject(i);
                            nbSuggest = userJson.getInt("nbSuggest");
                        }

                        textViewNbSuggest.setText(String.valueOf(nbSuggest));
                        g_nbSuggest= nbSuggest;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //création des paramètres du request
                HashMap<String, String> params = new HashMap<>();
                params.put("iduser", Integer.toString(user.getId()));

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_COUNTSUGGEST, params);
            }
        }

        recupNbBouteille rnb = new recupNbBouteille();
        rnb.execute();
    }
*/
    private void recupNbBouteille(final Runnable runnableGauge) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();

        class recupNbBouteille extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    // On convertie la réponse en json object
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    JSONArray sous_key2 = null;
                    int nbBot = 0;
                    long nbCat = 0;
                    ListView mList;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                            if (i == 3) {
                                sous_key2 = obj.getJSONArray(keys);
                            }
                        }

                        for (int i = 0; i < sous_key.length(); i++) {
                            JSONObject userJson = sous_key.getJSONObject(i);
                            nbBot = userJson.getInt("nbBottle");
                        }

                        textViewNbBottle.setText(String.valueOf(nbBot));
                        g_nbBot= nbBot;

                        for (int i = 0; i < sous_key2.length(); i++) {
                            JSONObject userJson = sous_key2.getJSONObject(i);
                            nbCat = userJson.getInt("nbCat");
                        }

                        recupNbInfoBot(runnableGauge);
                        //scheduleNotification(getNotification(g_nbBot));
                        int pourcentage_bot = (g_nbBot * 100)/ user.getNbBottleMax();
                       // if(pourcentage_bot < 10) scheduleNotification(g_nbBot);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //création des paramètres du request
                HashMap<String, String> params = new HashMap<>();
                params.put("iduser", Integer.toString(user.getId()));

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_COUNTBOTCAT, params);
            }
        }

        recupNbBouteille rnb = new recupNbBouteille();
        rnb.execute();
    }

    private void recupNbInfoBot(final Runnable runnableGauge) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();
        final int[] tot_info = new int[1];
        final int[] tot_info_abs = new int[1];
        final int[] pourcentAbs = {0};

        class recupNbInfoBot extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    // On convertie la réponse en json object
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    JSONArray sous_key2 = null;
                    JSONArray sous_key3 = null;
                    int nbNoteZero = 0;
                    int nbContainerEmpty = 0;
                    int nbComEmpty = 0;
                    ListView mList;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                            if (i == 3) {
                                sous_key2 = obj.getJSONArray(keys);
                            }
                            if (i == 4) {
                                sous_key3 = obj.getJSONArray(keys);
                            }
                        }

                        for (int i = 0; i < sous_key.length(); i++) {
                            JSONObject userJson = sous_key.getJSONObject(i);
                            nbNoteZero = userJson.getInt("nbNoteZero");
                        }

                        for (int i = 0; i < sous_key2.length(); i++) {
                            JSONObject userJson = sous_key2.getJSONObject(i);
                            nbContainerEmpty = userJson.getInt("nbContainerEmpty");
                        }

                        for (int i = 0; i < sous_key3.length(); i++) {
                            JSONObject userJson = sous_key3.getJSONObject(i);
                            nbComEmpty = userJson.getInt("nbComEmpty");
                        }

                        if(g_nbBot > 0) {
                            tot_info[0] = g_nbBot * 2;
                            tot_info_abs[0] = nbNoteZero + nbComEmpty;
                            g_pourcentAbs = (tot_info_abs[0] * 100) / tot_info[0];
                            g_pourcentAbs = 100 - g_pourcentAbs;
                        }
                        else g_pourcentAbs=0;

                        if(g_pourcentAbs < 0) g_pourcentAbs = 0;
                        textViewPourcent.setText(String.valueOf(g_pourcentAbs));

                        runnableGauge.run();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //création des paramètres du request
                HashMap<String, String> params = new HashMap<>();
                params.put("iduser", Integer.toString(user.getId()));

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_COUNTINFOBOT, params);
            }
        }

        recupNbInfoBot rnb = new recupNbInfoBot();
        rnb.execute();
    }

    private void captureImage() {

        // Create an implicit intent, for image capture.
        Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Start camera and wait for the results.
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        Toast.makeText(this, "Activer le flash et cadrer la photo sur l'étiquette de la bouteille", Toast.LENGTH_LONG).show();

        startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);


    }

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        progressBar.setVisibility(View.VISIBLE);

        Bitmap selectedImage = null;
        Bitmap imageCapture = null;
        int id;

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                //imageCapture = (Bitmap) data.getExtras().get("data");
                try {
                    imageCapture = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return;
            } else {
                //Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }

        //bmpimg2 = imageCapture;
        Intent BottleList = new Intent(getApplicationContext(), BottleList.class);
        finish();
        BottleList.putExtra("uri", imageUri);
        startActivity(BottleList);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(ProfileConfigActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void sendToken(String p_email) {
        //ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Registering Device...");
        //progressDialog.show();

        MyFirebaseInstanceIDService MyFirebaseInstanceIDService = new MyFirebaseInstanceIDService();

        MyFirebaseInstanceIDService.onTokenRefresh();

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = p_email;

        if (token == null) {
            //progressDialog.dismiss();
            //Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            //Toast.makeText(ProfileActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        //Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        FcmVolley.getInstance(this).addToRequestQueue(stringRequest);
    }


}