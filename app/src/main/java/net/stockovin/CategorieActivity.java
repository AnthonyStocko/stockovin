package net.stockovin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static android.os.Environment.DIRECTORY_PICTURES;


public class CategorieActivity extends AppCompatActivity implements BottleAdapter.Listener, CategorieAdapter.Listener, FilterAdapter.Listener{

    public final static String POSITION = "com.example.intent.example.POSITION";
    public final static String CATEGORIE = "com.example.intent.example.CATEGORIE";
    public final static String CATEGORIENAME = "com.example.intent.example.CATEGORIENAME";
    public final static String CATEGORIEARRAY = "com.example.intent.example.CATEGORIEARRAY";
    public final static String MODIFY = "com.example.intent.example.MODIFY";
    public final static String CATEGORIE_BOTTLE = "com.example.intent.example.CATEGORIE_BOTTLE";
    public final static String NEWUSERRETURN = "com.example.intent.example.NEWUSERRETURN";

    private List<Categorie> catList = new ArrayList<>();
    private CategorieAdapter catAdapter;

    private List<Bottle> botList = new ArrayList<>();
    private BottleAdapter botAdapter;

    private List<String> filterList = new ArrayList<>();
    private FilterAdapter filterAdapter;

    private List<String> g_nameCatrgorie = new ArrayList<>();
    private List<Integer> g_nbBotCatList = new ArrayList<>();

    RecyclerView recyclerBotView;
    RecyclerView recyclerCatView;
    RecyclerView recyclerFilterView;

    FrameLayout buttonCreationBot;
    LinearLayout recyclerViewLayCat;

    CoordinatorLayout coordinatorLayout;
    private ImageView imgAnim, imgAnim2, imgMiFoto;
    private Handler handlerAnimationCIMG;
    SearchView editsearch;
    private int Glo_idcategorie;
    private String Glo_categoriename;
    PopupMenu popup;
    ProgressBar progressBar ;
    Boolean newUserCatActivity, newUserCatActivityBot, newUserCatActivityBotReturn;

    private int g_position_cat = -1;

    boolean rougeType, blancType, roseType, champagneType;
    boolean demiBouteilleContainer, bouteilleContainer;
    int type = -1;
    int container = -1;
    int garde = -1;
    int year = -1;
    int note = -1;
    final String[] constraint = {null,null,null,null,null};

    TourGuide mTourGuideHandler;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie);

        this.configureToolbar();

        newUserCatActivity = getIntent().getBooleanExtra("newUserCatActivity", false);
        newUserCatActivityBotReturn = getIntent().getBooleanExtra("newUserReturn", false);
        g_position_cat = getIntent().getIntExtra("POSITION", -1);
        newUserCatActivityBot = newUserCatActivity;

        progressBar = this.findViewById(R.id.progressBarMenu);
        recyclerCatView = findViewById(R.id.recyclerView);
        recyclerBotView  = findViewById(R.id.recyclerbotView);
        recyclerFilterView  = findViewById(R.id.recyclerViewFilter);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        LinearLayoutManager catLayoutManager = new LinearLayoutManager(getApplicationContext());
        catLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerCatView.setLayoutManager(catLayoutManager);
        recyclerCatView.setItemAnimator(new DefaultItemAnimator());
        recyclerCatView.setAdapter(catAdapter);

        LinearLayoutManager botLayoutManager = new LinearLayoutManager(getApplicationContext());
        botLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerBotView.setLayoutManager(botLayoutManager);
        recyclerBotView.setItemAnimator(new DefaultItemAnimator());
        recyclerBotView.setAdapter(botAdapter);

        LinearLayoutManager filterLayoutManager = new LinearLayoutManager(getApplicationContext());
        filterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerFilterView.setLayoutManager(filterLayoutManager);
        recyclerFilterView.setItemAnimator(new DefaultItemAnimator());
        recyclerFilterView.setAdapter(filterAdapter);

        editsearch = findViewById(R.id.search);

        buttonCreationBot = findViewById(R.id.buttonCreationBot);
        recyclerViewLayCat = findViewById(R.id.recyclerViewLayCat);

        findViewById(R.id.buttonCreationBot).setVisibility(View.INVISIBLE);

        rougeType = false;

        afficheCategorie(true);

        filterList.add("Type");
        filterList.add("Contenant");
        filterList.add("Garde");
        filterList.add("Année");
        filterList.add("Note");

        chargeFilter();

        enableSwipeToDeleteAndUndo();

        findViewById(R.id.buttonCreationBot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newUserCatActivityBotReturn == true){
                    mTourGuideHandler.cleanUp();
                    newUserCatActivityBotReturn = false;
                }

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.buttonCreationBot).startAnimation(alpha);

                Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
                finish();
                bottleCreationActivity.putParcelableArrayListExtra("CategorieListe", (ArrayList<? extends Parcelable>) catList);
                bottleCreationActivity.putExtra(POSITION, g_position_cat);
                bottleCreationActivity.putExtra(CATEGORIE, Glo_idcategorie);
                bottleCreationActivity.putExtra(CATEGORIENAME, Glo_categoriename);
                startActivity(bottleCreationActivity);

            }
        });

        if (newUserCatActivityBotReturn == true) {

            ToolTip toolTipCave = new ToolTip().setTitle("Les bouteilles").setDescription("Vous pouvez, maintenant, enregistrer votre première bouteille");
            toolTipCave.setGravity(Gravity.TOP);

            mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                    .setToolTip(toolTipCave)
                    .setOverlay(new Overlay())
                    .playOn(buttonCreationBot);

        }

    }

    private Runnable runnableAnim = new Runnable(){
        @Override
        public void run() {
            imgAnim.animate().scaleX(1f).scaleY(1f).alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    imgAnim.setScaleX(1f);
                    imgAnim.setScaleY(1f);
                    imgAnim.setAlpha(1f);
                }
            });

            imgAnim2.animate().scaleX(1f).scaleY(1f).alpha(0f).setDuration(700).withEndAction(new Runnable() {
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
        titleToolbar.setText(user.getcaveName());

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

        switch(item.getItemId()) {
            case R.id.deleteItem:
                // On récupère la position de l'item concerné
                DeleteCategorie(catList.get(info.position).getId(), catList.get(info.position).getName());
                catList.remove(catList.get(info.position));
                break;

            case R.id.modifyItem:
                Intent categorieCreationActivity = new Intent(getApplicationContext(), CategorieCreationActivity.class);
                finish();
                categorieCreationActivity.putExtra(CATEGORIE, catList.get(info.position).getId());
                categorieCreationActivity.putExtra(CATEGORIENAME, catList.get(info.position).getName());
                categorieCreationActivity.putExtra(MODIFY, true);
                startActivity(categorieCreationActivity);
                break;
        }

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

    private void afficheCategorie(final boolean afficheBottle) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();

        class AfficheListeCategorie extends AsyncTask<Void, Void, String> {

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

                        //chargeCategorie();
                        if(g_position_cat != -1){
                            findViewById(R.id.buttonCreationBot).setVisibility(View.VISIBLE);
                        }
                        else {
                            findViewById(R.id.buttonCreationBot).setVisibility(View.INVISIBLE);
                        }

                        chargeCategorie();

                        if(afficheBottle == true && g_position_cat != -2)  AfficheBottle(catList.get(0).getId());

                        if(g_position_cat == -2){
                            g_position_cat=-1;
                        }

                        if (g_nbBotCatList.size() == 2) displayTourGuide();

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

    public void openOptionMenuCat(View v,final int position){

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.context_dir, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.deleteItem:
                        // On récupère la position de l'item concerné
                        DeleteCategorie(catList.get(position).getId(), catList.get(position).getName());
                        catList.remove(catList.get(position));
                        afficheCategorie(true);
                        break;

                    case R.id.modifyItem:
                        Intent categorieCreationActivity = new Intent(getApplicationContext(), CategorieCreationActivity.class);
                        finish();
                        categorieCreationActivity.putExtra(CATEGORIE, catList.get(position).getId());
                        categorieCreationActivity.putExtra(CATEGORIENAME, catList.get(position).getName());
                        categorieCreationActivity.putExtra(MODIFY, true);
                        startActivity(categorieCreationActivity);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void DeleteCategorie(final int p_id, final String Name) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();

        class DeleteListeCategorie extends AsyncTask<Void, Void, String> {

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

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), Res.getString(R.string.DeleteCategorieOK, Name), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorListeCategorie), Toast.LENGTH_SHORT).show();
                    }

                    chargeCategorie();

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
                params.put("id", Integer.toString(p_id));

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_DELETECAT, params);
            }
        }

        DeleteListeCategorie alc = new DeleteListeCategorie();
        alc.execute();
    }

    private void AfficheBottle(final int idcategorie) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final Resources Res = getResources();

        class AfficheListeBottle extends AsyncTask<Void, Void, String>  {

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
                    Bottle bot;

                    botList.clear();
                    g_nameCatrgorie.clear();

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names ();
                        //JSONObject userJson = obj.getJSONObject("user");
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

                if(idcategorie == -1)
                {
                    //creating request handler object
                    //création de l'objet request handler
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    //création des paramètres du request
                    HashMap<String, String> params = new HashMap<>();
                    params.put("iduser", Integer.toString(user.getId()));

                    //returing the response
                    //retour de la réponse
                    return requestHandler.sendPostRequest(URLs.URL_SELECTBOT_ALLCAT, params);
                }
                else
                {

                    //creating request handler object
                    //création de l'objet request handler
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    //création des paramètres du request
                    HashMap<String, String> params = new HashMap<>();
                    params.put("iduser", Integer.toString(user.getId()));
                    params.put("idcategorie", Integer.toString(idcategorie));

                    //returing the response
                    //retour de la réponse
                    return requestHandler.sendPostRequest(URLs.URL_SELECTBOT, params);
                }
            }
        }

        AfficheListeBottle alc = new AfficheListeBottle();
        alc.execute();

    }

    private void chargeBottle() {
        recyclerBotView = findViewById(R.id.recyclerbotView);
        botAdapter = new BottleAdapter(botList, g_nameCatrgorie,  Glide.with(getApplicationContext()), (BottleAdapter.Listener) this);
        recyclerBotView.setAdapter(botAdapter);
        registerForContextMenu(recyclerBotView);
        recyclerBotView.scheduleLayoutAnimation();
    }

    private void chargeCategorie() {
        catAdapter = new CategorieAdapter(catList, g_nbBotCatList, g_position_cat,  Glide.with(getApplicationContext()), (CategorieAdapter.Listener) this);
        recyclerCatView.setAdapter(catAdapter);
        registerForContextMenu(recyclerCatView);
        if(g_position_cat != -1 && g_position_cat != -2)
        {
            recyclerCatView.scrollToPosition(g_position_cat);
            findViewById(R.id.buttonCreationBot).setVisibility(View.VISIBLE);
        }

        if(g_position_cat == -2){
            recyclerCatView.scrollToPosition(catList.size()-1);
            findViewById(R.id.buttonCreationBot).setVisibility(View.VISIBLE);
            Glo_categoriename = catList.get(catList.size()-2).getName();
            Glo_idcategorie = catList.get(catList.size()-2).getId();
        }
        chargeFilter();

    }

    private void chargeFilter() {
        filterAdapter = new FilterAdapter(filterList, Glide.with(getApplicationContext()), (FilterAdapter.Listener) this);
        recyclerFilterView.setAdapter(filterAdapter);
        registerForContextMenu(recyclerFilterView);
        //recyclerFilterView.scheduleLayoutAnimation();

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
                        AfficheBottle(botList.get(position).getIdcategorie());
                        botList.remove(botList.get(position));
                        break;

                    case R.id.modifyItem:
                        Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
                        finish();
                        bottleCreationActivity.putParcelableArrayListExtra("CategorieListe", (ArrayList<? extends Parcelable>) catList);
                        bottleCreationActivity.putExtra(CATEGORIE, botList.get(position).getIdcategorie() );
                        bottleCreationActivity.putExtra(CATEGORIE_BOTTLE, botList.get(position).getId());
                        bottleCreationActivity.putExtra(CATEGORIENAME, g_nameCatrgorie.get(position));
                        bottleCreationActivity.putExtra(MODIFY, true);

                        startActivity(bottleCreationActivity);
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    public void DeleteBottle(int p_idBottle, String NameBot) {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final int idBottle = p_idBottle;
        final String nameBot = NameBot;
        final Resources Res = getResources();

        class DeleteListeBottle extends AsyncTask<Void, Void, String> {

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
        final Resources Res = getResources();
        final int l_id = id;

        class ClickIncreaseBottle extends AsyncTask<Void, Void, String> {
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
        afficheCategorie(false);
    }

    @Override
    public void onClickDesincreaseBottle(int id, final int p_qte) {

        final Resources Res = getResources();
        final int l_id = id;

        class ClickDesincreaseBottle extends AsyncTask<Void, Void, String> {
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
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

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
        afficheCategorie(false);
    }

    @Override
    public void onClickDisplayBottle(int position) {

        if(botList.isEmpty() == false) {
            int idbottle = (botList.get(position).getId());
            int idbottleCat = (botList.get(position).getIdcategorie());

            Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
            finish();
            bottleCreationActivity.putParcelableArrayListExtra("CategorieListe", (ArrayList<? extends Parcelable>) catList);
            bottleCreationActivity.putExtra(CATEGORIE, idbottleCat);
            bottleCreationActivity.putExtra(CATEGORIE_BOTTLE, idbottle);
            bottleCreationActivity.putExtra(CATEGORIENAME, g_nameCatrgorie.get(position));
            bottleCreationActivity.putExtra(MODIFY, true);

            startActivity(bottleCreationActivity);
        }
    }

    @Override
    public boolean onLongClickDisplayBottle(View view, int position) {
        openOptionMenuBot(view, position);
        return true;
    }

    @Override
    public void onClickDisplayFilter(View view, int position) {
        openOptionMenuFilterType(view, position);
    }

    @SuppressLint("ResourceAsColor")
    public void openOptionMenuFilterType(View v, final int position){

        PopupMenu popup;
        popup = new PopupMenu(v.getContext(), v);

        if(position == 0) {
            popup.getMenuInflater().inflate(R.menu.filtre_type, popup.getMenu());
            if(type != -1) {
                MenuItem item = popup.getMenu().getItem(type);
                item.setChecked(true);
            }
        }

        if(position == 1) {
            popup.getMenuInflater().inflate(R.menu.filter_container, popup.getMenu());
            if (container != -1) {
                MenuItem item = popup.getMenu().getItem(container);
                item.setChecked(true);
            }
        }

        if(position == 2) {
            popup.getMenuInflater().inflate(R.menu.filter_garde, popup.getMenu());
            if (garde != -1) {
                MenuItem item = popup.getMenu().getItem(garde);
                item.setChecked(true);
            }
        }

        if(position == 3) {
            popup.getMenuInflater().inflate(R.menu.filter_year, popup.getMenu());
            if (year != -1) {
                MenuItem item = popup.getMenu().getItem(year);
                item.setChecked(true);
            }
        }

        if(position == 4) {
            popup.getMenuInflater().inflate(R.menu.filter_note, popup.getMenu());
            if (note != -1) {
                MenuItem item = popup.getMenu().getItem(note);
                item.setChecked(true);
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.Rouge:
                        if(type != 0) {
                            type = 0;
                            constraint[0]="Rouge";
                        }
                        else type = -1;
                        break;

                    case R.id.Blanc:
                        if(type != 1) {
                            type = 1;
                            constraint[0]="Blanc";
                        }
                        else type = -1;
                        break;

                    case R.id.Rose:
                        if(type != 2) {
                            type = 2;
                            constraint[0]="Rosé";
                        }
                        else type = -1;
                        break;

                    case R.id.Champagne:
                        if(type != 3) {
                            type = 3;
                            constraint[0]="Champagne";
                        }
                        else type = -1;
                        break;

                    case R.id.demiBouteille:
                        if(container != 0) {
                            container = 0;
                            constraint[1]="37,5cl - Demi-Bouteille";
                        }
                        else container = -1;
                        break;

                    case R.id.medium:
                        if(container != 1) {
                            container = 1;
                            constraint[1]="50cl - Médium";
                        }
                        else container = -1;
                        break;

                    case R.id.bouteille:
                        if(container != 2) {
                            container = 2;
                            constraint[1]="75cl - Bouteille";
                        }
                        else container = -1;
                        break;
                    case R.id.magnum:
                        if(container != 3) {
                            container = 3;
                            constraint[1]="1,5L - Magnum";
                        }
                        else container = -1;
                        break;
                    case R.id.jeroboam:
                        if(container != 4) {
                            container = 4;
                            constraint[1]="3L - Jéroboam";
                        }
                        else container = -1;
                        break;
                    case R.id.rehoboam:
                        if(container != 5) {
                            container = 5;
                            constraint[1]="4,5cl - Réhoboam";
                        }
                        else container = -1;
                        break;
                    case R.id.mathusalem:
                        if(container != 6) {
                            container = 6;
                            constraint[1]="6L - Mathusalem";
                        }
                        else container = -1;
                        break;
                    case R.id.salmanazar:
                        if(container != 7) {
                            container = 7;
                            constraint[1]="9L - Salmanazar";
                        }
                        else container = -1;
                        break;
                    case R.id.balthazar:
                        if(container != 8) {
                            container = 8;
                            constraint[1]="12L - Balthazar";
                        }
                        else container = -1;
                        break;
                    case R.id.Agarder:
                        if(garde != 0) {
                            garde = 0;
                            constraint[2]="A garder";
                        }
                        else garde = -1;
                        break;
                    case R.id.Aconsommer:
                        if(garde != 1) {
                            garde = 1;
                            constraint[2]="A consommer";
                        }
                        else garde = -1;
                        break;
                    case R.id.Aoffrir:
                        if(garde != 2) {
                            garde = 2;
                            constraint[2]="A offrir";
                        }
                        else garde = -1;
                        break;
                    case R.id.year1990:
                        if(year != 0) {
                            year = 0;
                            constraint[3]="1990";
                        }
                        else year = -1;
                        break;
                    case R.id.year1991:
                        if(year != 1) {
                            year = 1;
                            constraint[3]="1991";
                        }
                        else year = -1;
                        break;
                    case R.id.year1992:
                        if(year != 2) {
                            year = 2;
                            constraint[3]="1992";
                        }
                        else year = -1;
                        break;
                    case R.id.year1993:
                        if(year != 3) {
                            year = 3;
                            constraint[3]="1993";
                        }
                        else year = -1;
                        break;
                    case R.id.year1994:
                        if(year != 4) {
                            year = 4;
                            constraint[3]="1994";
                        }
                        else year = -1;
                        break;
                    case R.id.year1995:
                        if(year != 5) {
                            year = 5;
                            constraint[3]="1995";
                        }
                        else year = -1;
                        break;
                    case R.id.year1996:
                        if(year != 6) {
                            year = 6;
                            constraint[3]="1996";
                        }
                        else year = -1;
                        break;
                    case R.id.year1997:
                        if(year != 7) {
                            year = 7;
                            constraint[3]="1997";
                        }
                        else year = -1;
                        break;
                    case R.id.year1998:
                        if(year != 8) {
                            year = 8;
                            constraint[3]="1998";
                        }
                        else year = -1;
                        break;
                    case R.id.year1999:
                        if(year != 9) {
                            year = 9;
                            constraint[3]="1999";
                        }
                        else year = -1;
                        break;
                    case R.id.year2000:
                        if(year != 10) {
                            year = 10;
                            constraint[3]="2000";
                        }
                        else year = -1;
                        break;
                    case R.id.year2001:
                        if(year != 11) {
                            year = 11;
                            constraint[3]="2001";
                        }
                        else year = -1;
                        break;
                    case R.id.year2002:
                        if(year != 12) {
                            year = 12;
                            constraint[3]="2002";
                        }
                        else year = -1;
                        break;
                    case R.id.year2003:
                        if(year != 13) {
                            year = 13;
                            constraint[3]="2003";
                        }
                        else year = -1;
                        break;
                    case R.id.year2004:
                        if(year != 14) {
                            year = 14;
                            constraint[3]="2004";
                        }
                        else year = -1;
                        break;
                    case R.id.year2005:
                        if(year != 15) {
                            year = 15;
                            constraint[3]="2005";
                        }
                        else year = -1;
                        break;
                    case R.id.year2006:
                        if(year != 16) {
                            year = 16;
                            constraint[3]="2006";
                        }
                        else year = -1;
                        break;
                    case R.id.year2007:
                        if(year != 17) {
                            year = 17;
                            constraint[3]="2007";
                        }
                        else year = -1;
                        break;
                    case R.id.year2008:
                        if(year != 18) {
                            year = 18;
                            constraint[3]="2008";
                        }
                        else year = -1;
                        break;
                    case R.id.year2009:
                        if(year != 19) {
                            year = 19;
                            constraint[3]="2009";
                        }
                        else year = -1;
                        break;
                    case R.id.year2010:
                        if(year != 20) {
                            year = 20;
                            constraint[3]="2010";
                        }
                        else year = -1;
                        break;
                    case R.id.year2011:
                        if(year != 21) {
                            year = 21;
                            constraint[3]="2011";
                        }
                        else year = -1;
                        break;
                    case R.id.year2012:
                        if(year != 22) {
                            year = 22;
                            constraint[3]="2012";
                        }
                        else year = -1;
                        break;
                    case R.id.year2013:
                        if(year != 23) {
                            year = 23;
                            constraint[3]="2013";
                        }
                        else year = -1;
                        break;
                    case R.id.year2014:
                        if(year != 24) {
                            year = 24;
                            constraint[3]="2014";
                        }
                        else year = -1;
                        break;
                    case R.id.year2015:
                        if(year != 25) {
                            year = 25;
                            constraint[3]="2015";
                        }
                        else year = -1;
                        break;
                    case R.id.year2016:
                        if(year != 26) {
                            year = 26;
                            constraint[3]="2016";
                        }
                        else year = -1;
                        break;
                    case R.id.year2017:
                        if(year != 27) {
                            year = 27;
                            constraint[3]="2017";
                        }
                        else year = -1;
                        break;
                    case R.id.year2018:
                        if(year != 28) {
                            year = 28;
                            constraint[3]="2018";
                        }
                        else year = -1;
                        break;
                    case R.id.year2019:
                        if(year != 29) {
                            year = 29;
                            constraint[3]="2019";
                        }
                        else year = -1;
                        break;
                    case R.id.year2020:
                        if(year != 30) {
                            year = 30;
                            constraint[3]="2020";
                        }
                        else year = -1;
                        break;
                    case R.id.year2021:
                        if(year != 31) {
                            year = 31;
                            constraint[3]="2021";
                        }
                        else year = -1;
                        break;
                    case R.id.note1:
                        if(note != 0) {
                            note = 0;
                            constraint[4]="1";
                        }
                        else note = -1;
                        break;
                    case R.id.note2:
                        if(note != 1) {
                            note = 1;
                            constraint[4]="2";
                        }
                        else note = -1;
                        break;
                    case R.id.note3:
                        if(note != 2) {
                            note = 2;
                            constraint[4]="3";
                        }
                        else note = -1;
                        break;
                    case R.id.note4:
                        if(note != 3) {
                            note = 3;
                            constraint[4]="4";
                        }
                        else note = -1;
                        break;
                    case R.id.note5:
                        if(note != 4) {
                            note = 4;
                            constraint[4]="5";
                        }
                        else note = -1;
                        break;
                    case R.id.note6:
                        if(note != 5) {
                            note = 5;
                            constraint[4]="6";
                        }
                        else note = -1;
                        break;
                    case R.id.note7:
                        if(note != 6) {
                            note = 6;
                            constraint[4]="7";
                        }
                        else note = -1;
                        break;
                    case R.id.note8:
                        if(note != 7) {
                            note = 7;
                            constraint[4]="8";
                        }
                        else note = -1;
                        break;
                    case R.id.note9:
                        if(note != 8) {
                            note = 8;
                            constraint[4]="9";
                        }
                        else note = -1;
                        break;
                    case R.id.note10:
                        if(note != 9) {
                            note = 9;
                            constraint[4]="10";
                        }
                        else note = -1;
                        break;
                }

                filterList.clear();

                if(type == -1 )  constraint[0] = null;
                if(container == -1 )   constraint[1] = null;
                if(garde == -1 )   constraint[2] = null;
                if(year == -1 )   constraint[3] = null;
                if(note == -1 )   constraint[4] = null;

                botAdapter.getFilterType().filter("|1|"+constraint[0]+"|2|"+constraint[1]+"|3|"+constraint[2]+"|4|"+constraint[3]+"|@5|"+constraint[4]+"|@6|");
                Log.d("Categorie","getFilterType "+ "|1|"+constraint[0]+"|2|"+constraint[1]+"|3|"+constraint[2]+"|4|"+constraint[3]+"|@5|"+constraint[4]+"|@6|");

                if(type == -1 )  constraint[0] = "Type";
                if(container == -1 )   constraint[1] = "Contenant";
                if(garde == -1 )   constraint[2] = "Garde";
                if(year == -1 )   constraint[3] = "Année";
                if(note == -1 )   constraint[4] = "Note";

                filterList.add(constraint[0]);
                filterList.add(constraint[1]);
                filterList.add(constraint[2]);
                filterList.add(constraint[3]);
                filterList.add(constraint[4]);
                chargeFilter();

                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onClickDisplayCategorie(int position) {

        if (newUserCatActivity == true) {
            mTourGuideHandler.cleanUp();
            newUserCatActivity=false;
        }

        if(catList.get(position).getId() != -2) {
            if (catList.get(position).getId() == -1)
                findViewById(R.id.buttonCreationBot).setVisibility(View.INVISIBLE);
            else findViewById(R.id.buttonCreationBot).setVisibility(View.VISIBLE);

            Glo_categoriename = catList.get(position).getName();
            Glo_idcategorie = catList.get(position).getId();

            g_position_cat = position;
            AfficheBottle(catList.get(position).getId());
        }
        else
        {
            Intent categorieCreationActivity = new Intent(getApplicationContext(), CategorieCreationActivity.class);
            finish();
            categorieCreationActivity.putExtra(MODIFY, false);
            categorieCreationActivity.putExtra("newUserCatActivityBot",newUserCatActivityBot);
            categorieCreationActivity.putExtra("POSITION",catList.get(position).getId());
            startActivity(categorieCreationActivity);
        }

        filterList.clear();
        filterList.add("Type");
        filterList.add("Contenant");
        filterList.add("Garde");
        filterList.add("Année");
        filterList.add("Note");

        type = -1;
        container = -1;
        garde = -1;
        chargeFilter();
    }

    @Override
    public boolean onLongClickCategorie(View view, int position) {
        openOptionMenuCat(view, position);
        return true;
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Bottle bot = botAdapter.getData().get(position);

                botAdapter.removeItem(position);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Suppression de la bouteille", Snackbar.LENGTH_SHORT);
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
                            DeleteBottle(bot.getId(), bot.getName());
                            afficheCategorie(false);
                            AfficheBottle(bot.getIdcategorie());
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

    void displayTourGuide(){

        newUserCatActivityBot = true;

        ToolTip toolTipCave = new ToolTip().setTitle("Les Régions").setDescription("Créez votre première région");
                                toolTipCave.setGravity(Gravity.BOTTOM);

        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setToolTip(toolTipCave)
                .setOverlay(new Overlay())
                .playOn(recyclerViewLayCat);

    }

}