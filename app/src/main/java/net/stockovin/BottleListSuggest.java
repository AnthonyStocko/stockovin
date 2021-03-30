package net.stockovin;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_PICTURES;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class BottleListSuggest extends AppCompatActivity implements BottleSuggestAdapter.Listener{


    public final static String CATEGORIE = "com.example.intent.example.CATEGORIE";
    public final static String CATEGORIENAME = "com.example.intent.example.CATEGORIENAME";
    public final static String MODIFY = "com.example.intent.example.MODIFY";
    public final static String CATEGORIE_BOTTLE = "com.example.intent.example.CATEGORIE_BOTTLE";
    public final static String SUGGEST = "com.example.intent.example.SUGGEST";

    private final List<BottleSuggest> botList = new ArrayList<>();
    private BottleSuggestAdapter botSuggestAdapter;
    private final List<String> g_nameCatrgorie = new ArrayList<>();
    private final List<String> g_nameUser = new ArrayList<>();

    private List<Categorie> catList = new ArrayList<>();
    private List<Integer> g_nbBotCatList = new ArrayList<>();

    RecyclerView recyclerBotSuggestView;

    CoordinatorLayout coordinatorLayout;
    private ImageView imgAnim, imgAnim2, imgMiFoto;
    private Handler handlerAnimationCIMG;
    SearchView editsearch;
    PopupMenu popup;
    //ProgressBar progressBar;
    TextView messageNotBottle;

    private final ArrayList<Integer> idBottleList = new ArrayList<>();
    String clauseWhere = null;
    String clauseWhereId = null;

    User user;
    private static Bitmap  bmpimg1, bmpimg2;

    Handler hdlr = new Handler();

    int gNbFile = 0;
    int gIndFile = 0;
    ProgressBar  pgsBar;
    ProgressBar  progressBarList;

    Bitmap bitmap;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottle_list_suggest);

        pgsBar = findViewById(R.id.pBar);
        progressBarList = findViewById(R.id.progressBarBottleSuggestList);

        user = SharedPrefManager.getInstance(this).getUser();



        this.configureToolbar();

        editsearch = findViewById(R.id.search);
        recyclerBotSuggestView  = findViewById(R.id.recyclerBotSuggestView);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        messageNotBottle = findViewById(R.id.messageNotBottle);
        messageNotBottle.setVisibility(View.INVISIBLE);

        LinearLayoutManager botLayoutManager = new LinearLayoutManager(getApplicationContext());
        botLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerBotSuggestView.setLayoutManager(botLayoutManager);
        recyclerBotSuggestView.setItemAnimator(new DefaultItemAnimator());
        recyclerBotSuggestView.setAdapter(botSuggestAdapter);


        botList.clear();
        g_nameCatrgorie.clear();
        g_nameUser.clear();

        AfficheBottle();
        afficheCategorie (false);

        enableSwipeToDeleteAndUndo();
    }

    private final Runnable runnableAnim = new Runnable(){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //2 - Inflate the menu and add it to the Toolbar
        /*getMenuInflater().inflate(R.menu.menu_categorie_activity, menu);*/
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        return true;
    }

    private void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        User user = SharedPrefManager.getInstance(this).getUser();

        TextView titleToolbar = findViewById(R.id.titleToolbar);
        titleToolbar.setText(R.string.ButtonSuggest);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        this.handlerAnimationCIMG = new Handler();
        this.imgAnim = findViewById(R.id.imgAnimtoobar);
        this.imgAnim2 = findViewById(R.id.imgAnimtoobar2);
        this.imgMiFoto = findViewById(R.id.imgProfileToolbar);

        Bitmap selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() +".jpg");
        imgMiFoto.setImageBitmap(selectedImage);

        File file = new File(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() +".jpg");

        if (!file.exists())  Glide.with(this).load(R.drawable.ic_user3).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
        else  Glide.with(this).load(selectedImage).apply(new RequestOptions().circleCrop()).into(imgMiFoto);

        this.runnableAnim.run();
        this.handlerAnimationCIMG.removeCallbacks(runnableAnim);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //3 - Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.menu_activity_add_cat:
                Intent categorieCreationActivity = new Intent(getApplicationContext(), CategorieCreationActivity.class);
                finish();
                categorieCreationActivity.putExtra(MODIFY, false);
                startActivity(categorieCreationActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View vue, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, vue, menuInfo);

        MenuInflater inflater = getMenuInflater();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // On récupère le fichier concerné par le menu contextuel
        // Categorie cat = mAdapter.getItem(info.position);
        // On a deux menus, s'il s'agit d'un répertoire ou d'un fichier
        inflater.inflate(R.menu.context_dir, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // On récupère la position de l'item concerné
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si on a appuyé sur le retour arrière

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    private void AfficheBottle() {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();

        class AfficheListeBottle extends AsyncTask<Void, Void, String>  {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    //converting response to json object
                    // On convertie la réponse en json object
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    BottleSuggest bot;

                    Log.d("BottleListSuggest","AfficheListeBottle " );

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names ();
                        for (int i = 0; i < key.length (); ++i) {
                            String keys = key.getString(i);
                            if( i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                        }

                        Log.d("BottleListuggest","key " + key);

                        for (int i = 0; i < sous_key.length(); i++) {
                            JSONObject userJson = sous_key.getJSONObject(i);

                            Log.d("BottleListuggest","userJson " + userJson);

                            bot = new BottleSuggest(
                                    userJson.getInt("id"),
                                    userJson.getString("name"),
                                    userJson.getString("domain"),
                                    userJson.getInt("qte"),
                                    userJson.getInt("idcategorie"),
                                    userJson.getInt("iduser"),
                                    userJson.getString("type"),
                                    userJson.getInt("year"),
                                    userJson.getString("comment"),
                                    userJson.getInt("note"),
                                    userJson.getString("container"),
                                    userJson.getString("garde"),
                                    userJson.getString("provider"),
                                    userJson.getInt("price"),
                                    userJson.getString("purchasedate"),
                                    userJson.getInt("id_user_friend"));
                            g_nameUser.add (userJson.getString("username"));
                            g_nameCatrgorie.add (userJson.getString("catname"));
                            botList.add(bot);
                        }

                        chargeBottle();

                        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }
                            @Override
                            public boolean onQueryTextChange(String newText) {
                                botSuggestAdapter.getFilter().filter(newText);
                                return false;
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorListeBottle), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //création des paramètres du request
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(user.getId()));

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_SELECTBOT_BOT_IN_SUGGEST, params);
            }
        }

        AfficheListeBottle alc = new AfficheListeBottle();
        alc.execute();

    }

    private void afficheCategorie(final boolean afficheBottle) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();

        class AfficheListeCategorie extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    // On convertie la réponse en json object
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    JSONArray sous_key2 = null;
                    Categorie cat;
                    Integer nbBottleFull = 0;
                    catList.clear();
                    g_nbBotCatList.clear();

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names ();
                        for (int i = 0; i < key.length (); ++i) {
                            String keys = key.getString(i);
                            if( i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                            if( i == 3) {
                                sous_key2 = obj.getJSONArray(keys);
                            }
                        }

                        for (int i = 0; i < sous_key2.length(); i++) {
                            JSONObject userJson = sous_key2.getJSONObject(i);
                            nbBottleFull = userJson.getInt("nbBottleFull");
                        }


                        cat = new Categorie(-1, user.getId(), "Tout");
                        catList.add(cat);
                        g_nbBotCatList.add (nbBottleFull);

                        for (int i = 0; i < sous_key.length(); i++) {
                            JSONObject userJson = sous_key.getJSONObject(i);
                            cat = new Categorie(
                                    userJson.getInt("id"),
                                    userJson.getInt("iduser"),
                                    userJson.getString("name")
                            );
                            g_nbBotCatList.add (userJson.getInt("nbBottle" ));
                            catList.add(cat);
                        }

                        cat = new Categorie(-2, user.getId(), "+");
                        catList.add(cat);
                        g_nbBotCatList.add (0);

                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorListeCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    findViewById(R.id.buttonCreationBot).setVisibility(View.INVISIBLE);
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
                return requestHandler.sendPostRequest(URLs.URL_SELECTCAT, params);
            }
        }

        AfficheListeCategorie alc = new AfficheListeCategorie();
        alc.execute();

    }

    private void chargeBottle() {
        recyclerBotSuggestView = findViewById(R.id.recyclerBotSuggestView);
        Log.d("BottleListuggest","botList " + botList);
        botSuggestAdapter = new BottleSuggestAdapter(botList, g_nameCatrgorie, g_nameUser,  Glide.with(getApplicationContext()), (BottleSuggestAdapter.Listener) this);
        recyclerBotSuggestView.setAdapter(botSuggestAdapter);
        registerForContextMenu(recyclerBotSuggestView);
        recyclerBotSuggestView.scheduleLayoutAnimation();

        if(pgsBar != null)        pgsBar.setVisibility(View.INVISIBLE);
    }

    public void openOptionMenuBot(View v,final int position){

        popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.context_dir, popup.getMenu());
        final Resources Res = getResources();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.deleteItem:
                        // On récupère la position de l'item concerné
                        botList.get(position).DeleteBottleSuggest(botList.get(position).getId(), botList.get(position).getName(), user.getId(), pgsBar, getApplicationContext(),  Res.getString(R.string.ErrorListeBottle));
                        botList.remove(botList.get(position));
                        botList.clear();
                        AfficheBottle();

                        break;

                    case R.id.modifyItem:

                        if(botList.isEmpty() == false) {
                            Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
                            int idbottle = (botList.get(position).getId());
                            int idbottleCat = (botList.get(position).getIdcategorie());

                            finish();
                            bottleCreationActivity.putExtra(CATEGORIE, idbottleCat);
                            bottleCreationActivity.putExtra(CATEGORIE_BOTTLE, idbottle);
                            bottleCreationActivity.putExtra(CATEGORIENAME, g_nameCatrgorie.get(position));
                            bottleCreationActivity.putExtra(MODIFY, false);
                            bottleCreationActivity.putParcelableArrayListExtra("CategorieListe", (ArrayList<? extends Parcelable>) catList);
                            bottleCreationActivity.putExtra(SUGGEST, true);

                            startActivity(bottleCreationActivity);
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(popup != null) popup.dismiss();
    }
/*
    @Override
    public void onClickIncreaseBottle(int id, final int p_qte) {
        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();
        final int l_id = id;

        class ClickIncreaseBottle extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    // On convertie la réponse en json object
                    JSONObject obj = new JSONObject(s);

                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error"))
                    {
                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorListeCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                int l_qte;
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //l_qte = botList.get(l_position).getQte() + 1;

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(l_id));
                params.put("qte",  Integer.toString(p_qte));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_UPDATEQTEBOTDE, params);
            }
        }

        ClickIncreaseBottle alc = new ClickIncreaseBottle();
        alc.execute();

    }

    @Override
    public void onClickDesincreaseBottle(int id, final int p_qte) {

        final Resources Res = getResources();
        final int l_id = id;

        class ClickDesincreaseBottle extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    // On convertie la réponse en json object
                    JSONObject obj = new JSONObject(s);

                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error"))
                    {
                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorListeCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                int l_qte;
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //l_qte = botList.get(l_position).getQte() -1;

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(l_id));
                params.put("qte",  Integer.toString(p_qte));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_UPDATEQTEBOTDE, params);
            }
        }

        ClickDesincreaseBottle alc = new ClickDesincreaseBottle();
        alc.execute();
    }
*/
    @Override
    public void onClickDisplayBottle(int position) {

        if(botList.isEmpty() == false) {
            Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
            int idbottle = (botList.get(position).getId());
            int idbottleCat = (botList.get(position).getIdcategorie());

            finish();
            bottleCreationActivity.putExtra(CATEGORIE, idbottleCat);
            bottleCreationActivity.putExtra(CATEGORIE_BOTTLE, idbottle);
            bottleCreationActivity.putExtra(CATEGORIENAME, g_nameCatrgorie.get(position));
            bottleCreationActivity.putExtra(MODIFY, false);
            bottleCreationActivity.putParcelableArrayListExtra("CategorieListe", (ArrayList<? extends Parcelable>) catList);
            bottleCreationActivity.putExtra(SUGGEST, true);

            startActivity(bottleCreationActivity);
        }
    }

    @Override
    public boolean onLongClickDisplayBottle(View view, int position) {
        openOptionMenuBot(view, position);
        return true;
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final BottleSuggest bot = botSuggestAdapter.getData().get(position);
                final Resources Res = getResources();

                botSuggestAdapter.removeItem(position);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Suppression de la bouteille", Snackbar.LENGTH_LONG);
                snackbar.setAction("Retour", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        botSuggestAdapter.restoreItem(bot, position);
                        recyclerBotSuggestView.scrollToPosition(position);
                    }
                });

                snackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            bot.DeleteBottleSuggest(bot.getId(), bot.getName(), user.getId(), progressBarList, getApplicationContext(),  Res.getString(R.string.ErrorListeBottle));
                            botList.clear();
                            AfficheBottle();
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        //Do something in shown
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerBotSuggestView);
    }

}
