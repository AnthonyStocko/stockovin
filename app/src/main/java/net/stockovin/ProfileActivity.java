package net.stockovin;

import static android.os.Environment.DIRECTORY_PICTURES;
import static java.lang.Thread.sleep;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.features2d.DescriptorExtractor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class ProfileActivity extends Activity {

    final User user = SharedPrefManager.getInstance(this).getUser();

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    private static final int PERMISSION_REQUEST_CODE = 100;
    TextView textViewUsername, textViewEmail, textViewNbBottle, textViewPourcent, textViewNbSuggest;
    private ImageView imgAnim, imgAnim2, imgMiFoto;
    private Handler handlerAnimationCIMG;
    ProgressBar progressBar;

    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;

    private static int descriptor = DescriptorExtractor.BRISK;
    private static int min_dist = 100;
    int g_nbBot = 0;
    int g_nbSuggest = 0;
    int g_pourcentAbs = 0;

    private CustomGauge gauge1, gauge2;

    private final OkHttpClient client = new OkHttpClient();

    Uri imageUri;

    TourGuide mTourGuideHandler;
    TourGuide mTourGuideHandlerDegust;
    LinearLayout linlay5;

    RelativeLayout rellayAll, buttonDegust, imgBotCave,
            imgSuggest, buttonFriend, buttonSuggest;

    Boolean newUser;
    Boolean newUserCatActivity;
    Boolean mesGauge;

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

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest2);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }

        newUser = getIntent().getBooleanExtra("newUser", false);
        newUserCatActivity = newUser;
        mesGauge = false;

        textViewNbBottle = findViewById(R.id.textViewNbBottle);
        textViewNbSuggest = findViewById(R.id.nbSuggest);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewPourcent = findViewById(R.id.textViewPourcent);
        progressBar = findViewById(R.id.progressBarMenu);
        textViewUsername.setText(user.getUsername());

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

        if (!checkPermission()) {
            requestPermission();
        }

        if (checkPermission()) {
            Bitmap selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() + ".jpg");
            imgMiFoto.setImageBitmap(selectedImage);

            File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() + ".jpg");

            if (!file.exists()) {
                Glide.with(this).load(R.drawable.ic_user3).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
                imgMiFoto.setPadding(30, 30, 30, 30);
            } else {
                Glide.with(this).load(selectedImage).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
                imgMiFoto.setPadding(3, 3, 3, 3);
            }
        }

        this.runnableAnim.run();
        this.handlerAnimationCIMG.removeCallbacks(runnableAnim);
        recupNbBouteille(this.runnableGauge);

        sendToken(user.getEmail());

        if (newUser) {
            ToolTip toolTipCave = new ToolTip().setTitle("Bingo!").setDescription("Vous pouvez enregistrer vos bouteilles ici");
            toolTipCave.setGravity(Gravity.TOP);

            // 1. On initialise l'instance de TourGuide principale
            mTourGuideHandler = TourGuide.init(this);

            // 2. On configure la technique, le tooltip et l'overlay séparément
            mTourGuideHandler.with(TourGuide.Technique.CLICK)
                    .setToolTip(toolTipCave)
                    .setOverlay(new Overlay()); // Cette ligne renvoie void mais modifie mTourGuideHandler en interne

            // 3. On lance enfin l'affichage sur la vue voulue
            mTourGuideHandler.playOn(imgBotCave);
        }

        findViewById(R.id.rellayAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mesGauge) {
                    mTourGuideHandlerDegust.cleanUp();
                    mesGauge = false;
                }
            }
        });

        findViewById(R.id.imgBotCave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newUser) {
                    mTourGuideHandler.cleanUp();
                    displayToolTipDegut();
                } else {
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
                if (newUser) {
                    mTourGuideHandlerDegust.cleanUp();
                    displayToolTipSuggest();
                } else {
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(500);
                    findViewById(R.id.linlayFriend).startAnimation(alpha);
                    Intent UserFriendListActivity = new Intent(getApplicationContext(), UserFriendListActivity.class);
                    finish();
                    startActivity(UserFriendListActivity);
                }
            }
        });

        findViewById(R.id.imgSuggest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newUser) {
                    mTourGuideHandlerDegust.cleanUp();
                    newUser = false;
                } else {
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(500);
                    findViewById(R.id.linlaySuggest).startAnimation(alpha);
                    Intent suggestListActivity = new Intent(getApplicationContext(), BottleListSuggest.class);
                    finish();
                    startActivity(suggestListActivity);
                }
            }
        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        });

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
                if (newUser) {
                    mTourGuideHandlerDegust.cleanUp();
                    displayToolTipFriend();
                } else {
                    captureImage();
                }
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

                if (file.exists()) {
                    if (ext.equals("jpg")) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        i.setDataAndType(photoURI, "image/jpg");
                    }
                    try {
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent ProfileConfigActivity = new Intent(getApplicationContext(), ProfileConfigActivity.class);
                    finish();
                    ProfileConfigActivity.putExtra("newUserCatActivity", newUserCatActivity);
                    startActivity(ProfileConfigActivity);
                }
            }
        });
    }

    private Notification getNotification(int nb_bot) {
        long[] swPattern = new long[]{0, 500, 110, 500, 110, 450, 110, 200, 110, 170, 40, 450, 110, 200, 110, 170, 40, 500};
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle("Alerte seuil minimum atteint");
        builder.setContentText("Il vous reste " + nb_bot + " bouteilles");
        builder.setSmallIcon(R.drawable.icon_bottle);
        builder.setLights(Color.RED, 1000, 1000);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setVibrate(swPattern);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Stockovin Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, ProfileActivity.class);
        PendingIntent conPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(conPendingIntent);

        return builder.build();
    }

    public void displayToolTipDegut() {
        ToolTip toolTip = new ToolTip().setTitle("Tchin!").setDescription("Retrouvez votre bouteille plus facilement dans votre cave en prenant en photo l'étiquette");
        toolTip.setGravity(Gravity.TOP);

        // 1. On initialise l'instance de TourGuide principale
        mTourGuideHandlerDegust = TourGuide.init(this);

        // 2. On configure la technique, le tooltip et l'overlay séparément
        mTourGuideHandlerDegust.with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .setOverlay(new Overlay()); // Cette ligne renvoie void mais modifie mTourGuideHandler en interne

        // 3. On lance enfin l'affichage sur la vue voulue
        mTourGuideHandlerDegust.playOn(buttonDegust);
    }

    public void displayToolTipFriend() {
        Resources res = getResources();
        ToolTip toolTip = new ToolTip().setTitle(res.getString(R.string.friendTourGuide)).setDescription(res.getString(R.string.friendFoundTourGuide));
        toolTip.setGravity(Gravity.TOP);

        // 1. On initialise l'instance de TourGuide principale
        mTourGuideHandlerDegust = TourGuide.init(this);

        // 2. On configure la technique, le tooltip et l'overlay séparément
        mTourGuideHandlerDegust.with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .setOverlay(new Overlay()); // Cette ligne renvoie void mais modifie mTourGuideHandler en interne

        // 3. On lance enfin l'affichage sur la vue voulue
        mTourGuideHandlerDegust.playOn(buttonFriend);
    }

    public void displayToolTipSuggest() {
        Resources res = getResources();
        ToolTip toolTip = new ToolTip().setTitle(res.getString(R.string.friendTourGuide)).setDescription(res.getString(R.string.friendFoundTourGuide));
        toolTip.setGravity(Gravity.TOP);

        // 1. On initialise l'instance de TourGuide principale
        mTourGuideHandlerDegust = TourGuide.init(this);

        // 2. On configure la technique, le tooltip et l'overlay séparément
        mTourGuideHandlerDegust.with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .setOverlay(new Overlay()); // Cette ligne renvoie void mais modifie mTourGuideHandler en interne

        // 3. On lance enfin l'affichage sur la vue voulue
        mTourGuideHandlerDegust.playOn(buttonSuggest);
    }

    public void displayToolTipGaugeBot() {
        if (mesGauge) {
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

    public void displayToolTipGaugeInfo() {
        if (mesGauge) {
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public Runnable runnableGauge = new Runnable() {
        @Override
        public void run() {
            gauge1.setEndValue(user.getNbBottleMax());
            gauge1.setStartValue(0);
            gauge1.setValue(1);

            gauge2.setEndValue(100);
            gauge2.setStartValue(0);
            gauge2.setValue(1);

            new Thread() {
                public void run() {
                    for (int i = 0; i <= 100; i++) {
                        try {
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalI <= g_nbBot) gauge1.setValue(finalI);
                                    if (finalI <= g_pourcentAbs) gauge2.setValue(finalI);
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

    private Runnable runnableAnim = new Runnable() {
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

    private void recupNbBouteille(final Runnable runnableGauge) {
        class AfficheNbBouteille extends AsyncTask<Void, Void, String> {
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
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    JSONArray sous_key2 = null;
                    int nbBot = 0;

                    if (!obj.getBoolean("error")) {
                        JSONArray key = obj.names();
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) sous_key = obj.getJSONArray(keys);
                            if (i == 3) sous_key2 = obj.getJSONArray(keys);
                        }

                        if (sous_key != null) {
                            for (int i = 0; i < sous_key.length(); i++) {
                                JSONObject userJson = sous_key.getJSONObject(i);
                                nbBot = userJson.getInt("nbBottle");
                            }
                        }

                        textViewNbBottle.setText(String.valueOf(nbBot));
                        g_nbBot = nbBot;

                        recupNbInfoBot(runnableGauge);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("iduser", Integer.toString(user.getId()));
                return requestHandler.sendPostRequest(URLs.URL_COUNTBOTCAT, params);
            }
        }

        AfficheNbBouteille rnb = new AfficheNbBouteille();
        rnb.execute();
    }

    private void recupNbInfoBot(final Runnable runnableGauge) {
        final int[] tot_info = new int[1];
        final int[] tot_info_abs = new int[1];

        class AfficheNbInfoBot extends AsyncTask<Void, Void, String> {
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
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    JSONArray sous_key2 = null;
                    JSONArray sous_key3 = null;
                    int nbNoteZero = 0;
                    int nbComEmpty = 0;

                    if (!obj.getBoolean("error")) {
                        JSONArray key = obj.names();
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) sous_key = obj.getJSONArray(keys);
                            if (i == 3) sous_key2 = obj.getJSONArray(keys);
                            if (i == 4) sous_key3 = obj.getJSONArray(keys);
                        }

                        if (sous_key != null) {
                            for (int i = 0; i < sous_key.length(); i++) {
                                JSONObject userJson = sous_key.getJSONObject(i);
                                nbNoteZero = userJson.getInt("nbNoteZero");
                            }
                        }

                        if (sous_key3 != null) {
                            for (int i = 0; i < sous_key3.length(); i++) {
                                JSONObject userJson = sous_key3.getJSONObject(i);
                                nbComEmpty = userJson.getInt("nbComEmpty");
                            }
                        }

                        if (g_nbBot > 0) {
                            tot_info[0] = g_nbBot * 2;
                            tot_info_abs[0] = nbNoteZero + nbComEmpty;
                            g_pourcentAbs = (tot_info_abs[0] * 100) / tot_info[0];
                            g_pourcentAbs = 100 - g_pourcentAbs;
                        } else {
                            g_pourcentAbs = 0;
                        }

                        if (g_pourcentAbs < 0) g_pourcentAbs = 0;
                        textViewPourcent.setText(String.valueOf(g_pourcentAbs));

                        runnableGauge.run();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("iduser", Integer.toString(user.getId()));
                return requestHandler.sendPostRequest(URLs.URL_COUNTINFOBOT, params);
            }
        }

        AfficheNbInfoBot rnb = new AfficheNbInfoBot();
        rnb.execute();
    }

    private void captureImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Toast.makeText(this, "Activer le flash et cadrer la photo sur l'étiquette de la bouteille", Toast.LENGTH_LONG).show();
        startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Intent BottleList = new Intent(getApplicationContext(), BottleList.class);
                finish();
                BottleList.putExtra("uri", imageUri);
                startActivity(BottleList);
            } else if (resultCode == RESULT_CANCELED) {
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.d("OpenCV", "OpenCV loaded successfully");
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void sendToken(String p_email) {
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = p_email;

        if (token == null) {
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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