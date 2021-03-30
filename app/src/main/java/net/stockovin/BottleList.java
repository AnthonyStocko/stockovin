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

public class BottleList extends AppCompatActivity implements BottleAdapter.Listener{


    public final static String CATEGORIE = "com.example.intent.example.CATEGORIE";
    public final static String CATEGORIENAME = "com.example.intent.example.CATEGORIENAME";
    public final static String MODIFY = "com.example.intent.example.MODIFY";
    public final static String CATEGORIE_BOTTLE = "com.example.intent.example.CATEGORIE_BOTTLE";

    private final List<Bottle> botList = new ArrayList<>();
    private BottleAdapter botAdapter;
    private final List<String> g_nameCatrgorie = new ArrayList<>();

    private List<Categorie> catList = new ArrayList<>();
    private List<Integer> g_nbBotCatList = new ArrayList<>();

    RecyclerView recyclerBotView;

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

    private final ArrayList<tabBottleSize> tabCompare =  new ArrayList<>();
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

        pgsBar = findViewById(R.id.pBar);
        progressBarList = findViewById(R.id.progressBarBottleList);

        user = SharedPrefManager.getInstance(this).getUser();

        setContentView(R.layout.activity_bottle_list);

        this.configureToolbar();

        editsearch = findViewById(R.id.search);

        recyclerBotView  = findViewById(R.id.recyclerbotView);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        messageNotBottle = findViewById(R.id.messageNotBottle);
        messageNotBottle.setVisibility(View.INVISIBLE);

        LinearLayoutManager botLayoutManager = new LinearLayoutManager(getApplicationContext());
        botLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerBotView.setLayoutManager(botLayoutManager);
        recyclerBotView.setItemAnimator(new DefaultItemAnimator());
        recyclerBotView.setAdapter(botAdapter);


        botList.clear();
        g_nameCatrgorie.clear();

        Uri imageUri = getIntent().getParcelableExtra("uri");
        clauseWhere = getIntent().getStringExtra("clauseWhere");
        clauseWhereId = getIntent().getStringExtra("clauseWhereId");

        if(clauseWhere == null)
        {
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            getContentResolver().delete(imageUri, null,null);
            gestionTableImage();
        }
        else AfficheBottle(clauseWhere, clauseWhereId);

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

        /*
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.toolbar);*/

        User user = SharedPrefManager.getInstance(this).getUser();

        TextView titleToolbar = findViewById(R.id.titleToolbar);
        titleToolbar.setText(R.string.tittleDegust);

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

    private void AfficheBottle(final String clauseWhere, final String clauseWhereId) {

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
                    Bottle bot;

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

                        for (int i = 0; i < sous_key.length(); i++) {
                            JSONObject userJson = sous_key.getJSONObject(i);

                            bot = new Bottle(
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
                                    userJson.getString("purchasedate"));
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
                                botAdapter.getFilter().filter(newText);
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
                params.put("id", clauseWhere);
                params.put("id_order", clauseWhereId);

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_SELECTBOT_BOT_IN, params);
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
        recyclerBotView = findViewById(R.id.recyclerbotView);
        botAdapter = new BottleAdapter(botList, g_nameCatrgorie,  Glide.with(getApplicationContext()), (BottleAdapter.Listener) this);
        recyclerBotView.setAdapter(botAdapter);
        registerForContextMenu(recyclerBotView);
        recyclerBotView.scheduleLayoutAnimation();

        if(pgsBar != null)        pgsBar.setVisibility(View.INVISIBLE);
    }

    public void openOptionMenuBot(View v,final int position){

        popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.context_dir, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.deleteItem:
                        // On récupère la position de l'item concerné
                        DeleteBottle(botList.get(position).getId(), botList.get(position).getName());
                        botList.remove(botList.get(position));
                        botList.clear();
                        AfficheBottle(clauseWhere, clauseWhereId);

                        break;

                    case R.id.modifyItem:
                        Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
                        finish();
                        bottleCreationActivity.putExtra(CATEGORIE, botList.get(position).getIdcategorie() );
                        bottleCreationActivity.putExtra(CATEGORIE_BOTTLE, botList.get(position).getId());
                        bottleCreationActivity.putExtra(CATEGORIENAME, g_nameCatrgorie.get(position));
                        bottleCreationActivity.putExtra(MODIFY, true);
                        bottleCreationActivity.putExtra("clauseWhere", clauseWhere );
                        bottleCreationActivity.putExtra("clauseWhereId", clauseWhereId );

                        startActivity(bottleCreationActivity);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void DeleteBottle(int p_idBottle, String NameBot) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final int idBottle = p_idBottle;
        final String nameBot = NameBot;
        final Resources Res = getResources();

        class DeleteListeBottle extends AsyncTask<Void, Void, String> {

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

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), Res.getString(R.string.DeleteBottleOK, nameBot), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),  Res.getString(R.string.ErrorListeBottle), Toast.LENGTH_SHORT).show();
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
                params.put("id", Integer.toString(idBottle));
                params.put("iduser", Integer.toString(user.getId()));


                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_DELETEBOT, params);
            }
        }

        DeleteListeBottle alc = new DeleteListeBottle();
        alc.execute();

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
        //AfficheBottle (clauseWhere, clauseWhereId);

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
            bottleCreationActivity.putExtra(MODIFY, true);
            bottleCreationActivity.putParcelableArrayListExtra("CategorieListe", (ArrayList<? extends Parcelable>) catList);
            bottleCreationActivity.putExtra("clauseWhere", clauseWhere);
            bottleCreationActivity.putExtra("clauseWhereId", clauseWhereId);

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
                final Bottle bot = botAdapter.getData().get(position);
                final Resources Res = getResources();

                botAdapter.removeItem(position);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Suppression de la bouteille", Snackbar.LENGTH_LONG);
                snackbar.setAction("Retour", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        botAdapter.restoreItem(bot, position);
                        recyclerBotView.scrollToPosition(position);
                    }
                });

                snackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            bot.DeleteBottle(bot.getId(), bot.getName(), user.getId(), progressBarList, getApplicationContext(),  Res.getString(R.string.ErrorListeBottle));
                            //DeleteBottle(bot.getId(), bot.getName());
                            botList.clear();
                            AfficheBottle (clauseWhere, clauseWhereId);
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
        itemTouchhelper.attachToRecyclerView(recyclerBotView);

    }

    public void gestionTableImage(){
        class gestionTableImage extends AsyncTask<Void, Void, String> {

            Handler hdlr = new Handler();
            final int[] ind = {0};
            //ProgressBar  pgsBar = (ProgressBar) findViewById(R.id.pBar);

            TextView txtView = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //progressBar.setVisibility(View.GONE);
                if(clauseWhere.equals("()") || clauseWhere.equals("(-1)"))
                {
                    messageNotBottle = findViewById(R.id.messageNotBottle);
                    messageNotBottle.setVisibility(View.VISIBLE);
                    if(pgsBar != null)        pgsBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            protected String doInBackground(Void... voids) {

                int id;
                Bitmap selectedImage = null;

                idBottleList.clear();
                tabBottleSize tempTab = new tabBottleSize(0, 0);

                File mCurrentFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle" + user.getId());

                // On récupère la liste des fichiers dans le répertoire actuel
                File[] fichiers = mCurrentFile.listFiles();
                // On transforme le tableau en une structure de données de taille variable
                ArrayList<File> liste = new ArrayList<File>();

                if( fichiers !=null) {
                    for (File f : fichiers) liste.add(f);
                }

                gNbFile = liste.size();
                runnableProgressBar.run();

                //bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin"  + "/bottle_34.jpg");
                bmpimg2 = bitmap;
                Mat mat2 = new Mat(bmpimg2.getWidth(), bmpimg2.getHeight(), CvType.CV_8UC3);
                Utils.bitmapToMat(bmpimg2, mat2);

                FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

                Mat descriptors2 = new Mat();
                MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
                detector.detect(mat2, keypoints2);
                extractor.compute(mat2, keypoints2, descriptors2);

                //Toast.makeText(this, "Avant boucle", Toast.LENGTH_LONG).show();
                for(int i = 0; i<liste.size();i++)
                {
                    gIndFile = i;
                    //Toast.makeText(this,  , Toast.LENGTH_LONG).show();
                    //File file = new File(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId()+"/bottle_" + i +".jpg");
                    //bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle" + user.getId() + "/bottle_34.jpg");
                    id = Integer.valueOf(liste.get(i).getName().substring(liste.get(i).getName().indexOf('_') + 1, liste.get(i).getName().indexOf('.')));
                    File file = liste.get(i);

                    if (file.exists()) {
                        //selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId()+"/bottle_" + i + ".jpg");
                        //imageCapture= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId()+"/bottle_34.jpg");
                        selectedImage = BitmapFactory.decodeFile(file.getPath());

                        bmpimg1 = selectedImage;
                        if (bmpimg1 != null && bmpimg2 != null) {
                            //bmpimg1 = Bitmap.createScaledBitmap(bmpimg1, 100, 100, true);
                            //bmpimg2 = Bitmap.createScaledBitmap(bmpimg2, 100, 100, true);
                            //Log.d("BottleList","id " + id);

                            MatOfDMatch matches_final_mat = compareImage3(descriptors2);

                            if (matches_final_mat != null) {
                                List<DMatch> finalMatchesList = matches_final_mat.toList();
                                //Toast.makeText(getApplicationContext(), "finalMatchesList.size() " + finalMatchesList.size() + " bottle_" + i, Toast.LENGTH_SHORT).show();
                                if (finalMatchesList.size() > 0) {
                                    tabBottleSize tempTab2 = new tabBottleSize(id, finalMatchesList.size());
                                    tabCompare.add(tempTab2);
                                }
                            }
                        }
                    }
                }

                Collections.sort(tabCompare,tempTab.ComparatorSize);

                for(int i = 0; i<tabCompare.size();i++)
                {
                    //Log.d("BottleList","id tri " + tabCompare.get(i).getIdBottle()+" / Match tri " + tabCompare.get(i).getSize());
                    idBottleList.add(tabCompare.get(i).getIdBottle());
                }

                int indice = 0;
                clauseWhere ="(";
                clauseWhereId ="(b.id,";

                for(indice=idBottleList.size()-1;indice>=0;indice--)
                {
                    if (indice != idBottleList.size() - 1) {
                        clauseWhere = clauseWhere + ",";
                        clauseWhereId = clauseWhereId + ",";
                    }
                    clauseWhere = clauseWhere + idBottleList.get(indice);
                    clauseWhereId = clauseWhereId + idBottleList.get(indice);
                }

                clauseWhere =clauseWhere +")";
                clauseWhereId =clauseWhereId +")";

                //Log.d("BottleList","clauseWhere " +clauseWhere + " clauseWhereId " + clauseWhereId);

                AfficheBottle(clauseWhere, clauseWhereId);

                return null;
            }
        }

        gestionTableImage gti = new gestionTableImage();
        gti.execute();
    }

    MatOfDMatch compareImage() {
        final int descriptor = DescriptorExtractor.BRISK;
        Mat img1, img2, descriptors, dupDescriptors;
        FeatureDetector detector;
        DescriptorExtractor DescExtractor;
        DescriptorMatcher matcher;
        MatOfKeyPoint keypoints, dupKeypoints;
        MatOfDMatch matches, matches_final_mat = null;

        try {
            bmpimg1 = bmpimg1.copy(Bitmap.Config.ARGB_8888, true);
            bmpimg2 = bmpimg2.copy(Bitmap.Config.ARGB_8888, true);
            img1 = new Mat();
            img2 = new Mat();
            Utils.bitmapToMat(bmpimg1, img1);
            Utils.bitmapToMat(bmpimg2, img2);
            Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2RGB);
            detector = FeatureDetector.create(FeatureDetector.BRISK);
            DescExtractor = DescriptorExtractor.create(descriptor);
            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            keypoints = new MatOfKeyPoint();
            dupKeypoints = new MatOfKeyPoint();
            descriptors = new Mat();
            dupDescriptors = new Mat();
            matches = new MatOfDMatch();
            detector.detect(img1, keypoints);
            detector.detect(img2, dupKeypoints);
            DescExtractor.compute(img1, keypoints, descriptors);
            DescExtractor.compute(img2, dupKeypoints, dupDescriptors);
            matcher.match(descriptors, dupDescriptors, matches);
            List<DMatch> matchesList = matches.toList();
            List<DMatch> matches_final = new ArrayList<DMatch>();

            for (int i = 0; i < matchesList.size(); i++) {
                if (matchesList.get(i).distance <= 100) {
                    matches_final.add(matches.toList().get(i));
                }
            }
            matches_final_mat = new MatOfDMatch();
            matches_final_mat.fromList(matches_final);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return(matches_final_mat);
    }

    static MatOfDMatch compareImage3(Mat mat2) {

        Mat mat1 = new Mat(bmpimg1.getWidth(), bmpimg1.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(bmpimg1, mat1);

        return (compareMats(mat1, mat2));
    }

    static MatOfDMatch compareMats(Mat img1, Mat descriptors2) {
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        detector.detect(img1, keypoints1);
        extractor.compute(img1, keypoints1, descriptors1);

        //second image
        // Mat img2 = Imgcodecs.imread(path2);
        /*Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        detector.detect(img2, keypoints2);
        extractor.compute(img2, keypoints2, descriptors2);*/

        //matcher image descriptors
        MatOfDMatch matches = new MatOfDMatch();
        if(descriptors2.isContinuous() == true) {

            matcher.match(descriptors1,descriptors2,matches);
        }
        // Filter matches by distance
        MatOfDMatch filtered = filterMatchesByDistance(matches);

        int total = (int) matches.size().height;
        int Match = (int) filtered.size().height;
        //Log.d("LOG", "total:" + total + " Match:" + Match);

        return filtered;
    }

    static MatOfDMatch filterMatchesByDistance(MatOfDMatch matches) {
        List<DMatch> matches_original = matches.toList();
        List<DMatch> matches_filtered = new ArrayList<DMatch>();

        int DIST_LIMIT = 50;
        // Check all the matches distance and if it passes add to list of filtered matches
        //Log.d("DISTFILTER", "ORG SIZE:" + matches_original.size() + "");
        for (int i = 0; i < matches_original.size(); i++) {
            DMatch d = matches_original.get(i);
            //Log.d("LOG", "distance:" + d.distance);
            if (Math.abs(d.distance) <= DIST_LIMIT) {
                matches_filtered.add(d);
            }
        }
        //Log.d("DISTFILTER", "FIL SIZE:" + matches_filtered.size() + "");

        MatOfDMatch mat = new MatOfDMatch();
        mat.fromList(matches_filtered);
        return mat;

    }

    public class tabBottleSize {

        private int idBottle;
        private double size;

        public int getIdBottle() {
            return idBottle;
        }

        public double getSize() {
            return size;
        }

        public void setIdBottle(int idBottle) {
            this.idBottle = idBottle;
        }

        public void setSize(double size) {
            this.size = size;
        }

        public tabBottleSize(int idBottle, double size) {
            this.idBottle = idBottle;
            this.size = size;
        }

        public Comparator<tabBottleSize> ComparatorSize = new Comparator<tabBottleSize>() {

            @Override
            public int compare(tabBottleSize o1, tabBottleSize o2) {
                return (int) (  o1.getSize() - o2.getSize());
            }
        };
    }

    public Runnable runnableProgressBar = new Runnable(){
        int indProgressBar = 0;

        @Override
        public void run() {

            new Thread(new Runnable() {
                public void run() {
                    pgsBar = findViewById(R.id.pBar);

                    while (gIndFile < gNbFile-1) {
                        if(gNbFile!=0) indProgressBar = (gIndFile * 100)/gNbFile;
                        else indProgressBar=0;
                        //ind[0] += 1;
                        // Update the progress bar and display the current value in text view
                        hdlr.post(new Runnable() {
                            public void run() {
                                if(indProgressBar != 0)  pgsBar.setProgress(indProgressBar);
                                //txtView.setText(ind[0]+"/"+pgsBar.getMax());
                            }
                        });
                        try {
                            // Sleep for 100 milliseconds to show the progress slowly.
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    };
}
