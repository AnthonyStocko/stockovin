package net.stockovin;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_PICTURES;

public class BottleCreationActivity extends AppCompatActivity {

    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    EditText editTextBottlename;
    EditText editTextBottledomain;
    EditText editTextBottlecomment;
    EditText editTextBottleProvider;
    EditText editTextBottlePrice;
    DatePicker datePickerPurchaseDate;
    Spinner SpinnerNote, SpinnerContainer, SpinnerType, SpinnerQte, SpinnerYear, SpinnerCat, SpinnerGarde;

    TextView textBottleInsertUpdate;
    ProgressBar progressBar;

    int idcategorie = 0;
    String categoriename = null;
    int gIdCategorie = 0;

    int idbottle = 0;
    boolean modify, suggest;
    Bottle selectbottle = null;

    ArrayAdapter<Integer> QteAdapter = null;
    int GQte= 0;
    ArrayAdapter<String> TypeAdapter = null;
    String GType = null;
    ArrayAdapter<Integer> noteAdapter = null;
    int gNote= 0;
    ArrayAdapter<String> containerAdapter = null;
    String gContainer= null;
    ArrayAdapter<Integer> yearAdapter = null;
    int gYear= 0;
    ArrayAdapter<String> catAdapter = null;
    String gcategorie= null;
    ArrayAdapter<String> gardeAdapter = null;
    String gGarde= null;

    private Handler handlerAnimationCIMG;

    static final int RESULT_LOAD_IMG = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ImageView imgMiFoto, imgPhotoBot, imageViewCreaBotAnim2, imageViewCreaBotAnim;
    private RelativeLayout relativeLayoutRellay1, relativeLayoutRellay2, relativeLayoutImgUser, imageViewimgUser;
    private View idcardview;
    private LinearLayout linlay31, linlay310, linlay4;

    private String  clauseWhere = null;
    private String  clauseWhereId = null;


    Uri imageUri;
    private List<Categorie> catList = new ArrayList<>();
    int indice;
    Dialog dialog;

    private List<User> userListFriend = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_bottle);
        String chaine;
        final Resources Res = getResources();

        User user = SharedPrefManager.getInstance(this).getUser();

        Resources res = getResources();
        progressBar = findViewById(R.id.progressBarBotInsert);

        // On récupère les info des autres activity
        Intent i = getIntent();
        idcategorie = i.getIntExtra(CategorieActivity.CATEGORIE, 0);
        idbottle = i.getIntExtra(CategorieActivity.CATEGORIE_BOTTLE, -1);
        categoriename = i.getStringExtra(CategorieActivity.CATEGORIENAME);
        modify = i.getBooleanExtra(CategorieActivity.MODIFY, false);
        clauseWhere = i.getStringExtra("clauseWhere");
        clauseWhereId = i.getStringExtra("clauseWhereId");
        catList = i.getParcelableArrayListExtra("CategorieListe");
        suggest = i.getBooleanExtra(BottleListSuggest.SUGGEST, false);

        //Spinner Categorie
        SpinnerCat = findViewById(R.id.SpinnerCat);
        List<String> categorieName = new ArrayList<String>();
        List<Integer> categorieId = new ArrayList<Integer>();
        for(indice =1; indice < catList.size()-1; indice ++){
            categorieName.add(catList.get(indice).getName());
            categorieId.add(catList.get(indice).getId());
        }

        //SpinnerQte
        SpinnerQte = findViewById(R.id.SpinnerQte);
        List<Integer> exemple = new ArrayList<Integer>();
        exemple.add(0);
        exemple.add(1);
        exemple.add(2);
        exemple.add(3);
        exemple.add(4);
        exemple.add(5);
        exemple.add(6);
        exemple.add(7);
        exemple.add(8);
        exemple.add(9);
        exemple.add(10);
        exemple.add(11);
        exemple.add(12);
        exemple.add(13);
        exemple.add(14);
        exemple.add(15);
        exemple.add(16);
        exemple.add(17);
        exemple.add(18);
        exemple.add(19);
        exemple.add(20);
        exemple.add(21);
        exemple.add(22);
        exemple.add(23);
        exemple.add(24);
        exemple.add(25);
        exemple.add(26);
        exemple.add(27);
        exemple.add(28);
        exemple.add(29);
        exemple.add(30);

        //Spinner type
        SpinnerType= findViewById(R.id.SpinnerType);
        List<String> Type = new ArrayList<String>();
        Type.add("Rouge");
        Type.add("Blanc");
        Type.add("Rosé");
        Type.add("Champagne");

        //Spinner Container
        SpinnerContainer = findViewById(R.id.SpinnerContainer);
        List<String> Container = new ArrayList<String>();
        Container.add("37,5cl - Demi-Bouteille");
        Container.add("50cl - Médium");
        Container.add("75cl - Bouteille");
        Container.add("1,5L - Magnum");
        Container.add("3L - Jéroboam");
        Container.add("4,5cl - Réhoboam");
        Container.add("6L - Mathusalem");
        Container.add("9L - Salmanazar");
        Container.add("12L - Balthazar");

        //Spinner Note
        SpinnerNote = findViewById(R.id.SpinnerNote);
        List<Integer> Note = new ArrayList<>();
        Note.add(1);
        Note.add(2);
        Note.add(3);
        Note.add(4);
        Note.add(5);
        Note.add(6);
        Note.add(7);
        Note.add(8);
        Note.add(9);
        Note.add(10);

        //Spinner Année
        SpinnerYear = findViewById(R.id.SpinnerYear);
        List<Integer> year = new ArrayList<Integer>();
        year.add(2030);
        year.add(2029);
        year.add(2028);
        year.add(2027);
        year.add(2026);
        year.add(2025);
        year.add(2024);
        year.add(2023);
        year.add(2022);
        year.add(2021);
        year.add(2020);
        year.add(2019);
        year.add(2018);
        year.add(2017);
        year.add(2016);
        year.add(2015);
        year.add(2014);
        year.add(2013);
        year.add(2012);
        year.add(2011);
        year.add(2010);
        year.add(2009);
        year.add(2008);
        year.add(2007);
        year.add(2006);
        year.add(2005);
        year.add(2004);
        year.add(2003);
        year.add(2002);
        year.add(2001);
        year.add(2000);
        year.add(1999);
        year.add(1998);
        year.add(1997);
        year.add(1996);
        year.add(1995);
        year.add(1994);
        year.add(1993);
        year.add(1992);
        year.add(1991);
        year.add(1990);

        //Spinner Garde
        SpinnerGarde= findViewById(R.id.SpinnerGarde);
        List<String> garde = new ArrayList<String>();
        garde.add("A garder");
        garde.add("A consommer");
        garde.add("A offrir");

        //Configuration des Spinner avec adapter
        TypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Type);
        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerType.setAdapter(TypeAdapter);

        noteAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Note);
        noteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerNote.setAdapter(noteAdapter);

        containerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Container);
        containerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerContainer.setAdapter(containerAdapter);

        QteAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, exemple);
        QteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerQte.setAdapter(QteAdapter);

        yearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, year);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerYear.setAdapter(yearAdapter);

        catAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categorieName);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCat.setAdapter(catAdapter);

        gardeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, garde);
        gardeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerGarde.setAdapter(gardeAdapter);

        editTextBottlename = findViewById(R.id.editTextbottlename);
        editTextBottledomain = findViewById(R.id.editTextbottledomain);
        editTextBottlecomment = findViewById(R.id.editTextbottlecomment);
        editTextBottleProvider = findViewById(R.id.editTextbottleProvider);
        editTextBottlePrice = findViewById(R.id.editTextbottlePrice);
        imageViewCreaBotAnim2 = findViewById(R.id.imgCreaBotAnim2);
        imageViewCreaBotAnim = findViewById(R.id.imgCreaBotAnim);
        relativeLayoutRellay1 = findViewById(R.id.rellay1);
        relativeLayoutRellay2 = findViewById(R.id.rellay2);
        imageViewimgUser = findViewById(R.id.imgUser);
        relativeLayoutImgUser = findViewById(R.id.imgUser);
        imgMiFoto = findViewById(R.id.imgMiFoto);
        idcardview = findViewById(R.id.idcardview10);
        linlay31 = findViewById(R.id.linlay31);
        linlay310 = findViewById(R.id.linlay310);
        linlay4 = findViewById(R.id.linlay4);

        datePickerPurchaseDate = findViewById(R.id.datePickerPurchaseDate);

        editTextBottlename.requestFocus();
        this.imgPhotoBot = findViewById(R.id.imgPhotoBot);

        friendSelect(user.getId());

        // On est dans le cas d'une modification de bouteille
        if(modify == true){
            // On ne peut pas modifier la région
            //editTextBottlecathegorie.setEnabled(false);
            // On affiche le libelle "Modification
            chaine = res.getString(R.string.Modify);
            // On set le text
            textBottleInsertUpdate = findViewById(R.id.textInserUpdatetBot);
            textBottleInsertUpdate.setText(chaine);

            //appel fonction de select + affichage des information de la bouteille
            bottleSelect(idbottle);

            //appel fonction d'affichage de la photo de la bouteille
            displayImageBottle();

            findViewById(R.id.linlay31).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(500);
                    findViewById(R.id.buttonDeletetBot).startAnimation(alpha);
                    selectbottle.DeleteBottle(selectbottle.getId(), selectbottle.getName(), user.getId(), progressBar, getApplicationContext(),  Res.getString(R.string.ErrorListeBottle));
                    //DeleteBottle(selectbottle.getId(), selectbottle.getName());
                    Intent CategorieActivity = new Intent(getApplicationContext(), CategorieActivity.class);
                    finish();
                    startActivity(CategorieActivity);
                }
            });

            findViewById(R.id.linlay310).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(500);
                    findViewById(R.id.buttonBotSuggest).startAnimation(alpha);

                    final ArrayList itemsSelected = new ArrayList();

                    String[] arr = new String[userListFriend.size()];
                    Integer[] arr_id = new Integer[userListFriend.size()];
                    for(int i=0 ; i< userListFriend.size();i++){
                        arr[i] = userListFriend.get(i).getUsername();
                        arr_id[i] = userListFriend.get(i).getId();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(BottleCreationActivity.this);

                    builder.setTitle(R.string.SelectFriend);

                    builder.setMultiChoiceItems(arr, null,
                            new DialogInterface.OnMultiChoiceClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int selectedItemId,
                                                    boolean isSelected) {
                                    if (isSelected) {
                                        itemsSelected.add(selectedItemId);
                                    } else if (itemsSelected.contains(selectedItemId)) {

                                        itemsSelected.remove(Integer.valueOf(selectedItemId));
                                    }
                                }
                            })
                            .setPositiveButton(R.string.Send, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    //Your logic when OK button is clicked
                                    Log.d("BottleCreationActivity","itemsSelected " + itemsSelected);
                                    for(int i=0 ; i< itemsSelected.size();i++){
                                        Log.d("BottleCreationActivity","itemsSelected " + arr[(int) itemsSelected.get(i)]);
                                        Log.d("BottleCreationActivity","itemsSelected " + arr_id[(int) itemsSelected.get(i)]);
                                        bottleSuggestInsert(arr_id[(int) itemsSelected.get(i)]);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {


                                }
                            });

                    dialog = builder.create();
                    dialog.show();

                }
            });

        }else{

            //configuration de la toolbar
            this.configureToolbar(false, "", "");

            // On est dans le cas d'une création de bouteille
            Glide.with(this).load(R.drawable.icon_bottle).apply(new RequestOptions().circleCrop()).into(imgMiFoto);

            // On affiche pas les view qui permettent d'afficher la photo de la boutelle
            idcardview.setVisibility(View.INVISIBLE);
            imageViewCreaBotAnim2.setVisibility(View.VISIBLE);
            imageViewCreaBotAnim.setVisibility(View.VISIBLE);
            imageViewimgUser.setVisibility(View.VISIBLE);

            linlay4.removeView(linlay31);
            linlay4.removeView(linlay310);

            //Init handler pour l'annimation
            init();
            this.runnableAnim.run();
            this.handlerAnimationCIMG.removeCallbacks(runnableAnim);


            findViewById(R.id.imgMiFoto).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                }
            });


            // On set les valeur par defaut
            //editTextBottlecathegorie.setText(categoriename);
            //editTextBottlecathegorie.setEnabled(false);
            // Spinner Categorie
            for(indice =1; indice < catList.size()-1; indice ++){
                if(categoriename.equals(catList.get(indice).getName())){
                    gIdCategorie = catList.get(indice).getId();
                    SpinnerCat.setSelection(indice-1);
                }
            }

            if(suggest == true){
                bottleSuggestSelect(idbottle);
            }
            else {

                int spinnerPosition = TypeAdapter.getPosition("Rouge");
                SpinnerType.setSelection(spinnerPosition);

                spinnerPosition = containerAdapter.getPosition("75cl - Bouteille");
                SpinnerContainer.setSelection(spinnerPosition);

                spinnerPosition = noteAdapter.getPosition(1);
                SpinnerNote.setSelection(spinnerPosition);

                spinnerPosition = yearAdapter.getPosition(2020);
                SpinnerYear.setSelection(spinnerPosition);

                spinnerPosition = gardeAdapter.getPosition("A garder");
                SpinnerGarde.setSelection(spinnerPosition);
            }

            chaine = res.getString(R.string.creation);
            textBottleInsertUpdate = findViewById(R.id.textInserUpdatetBot);
            textBottleInsertUpdate.setText(chaine);

        }


        findViewById(R.id.ButtonFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                idcardview.setVisibility(View.INVISIBLE);
                imageViewimgUser.setVisibility(View.INVISIBLE);

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        findViewById(R.id.ButtonTrash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File Photo_Signa = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId());
                File file = new File(Photo_Signa.getPath(), "bottle_" + idbottle + ".jpg");
                if(file.exists())
                {
                    imageViewCreaBotAnim2.setVisibility(View.VISIBLE);
                    imageViewCreaBotAnim.setVisibility(View.VISIBLE);
                    imageViewimgUser.setVisibility(View.VISIBLE);
                    idcardview.setVisibility(View.INVISIBLE);
                    file.delete();
                    Glide.with(getApplicationContext()).load(R.drawable.icon_bottle).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
                }
            }
        });

        findViewById(R.id.ButtonCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //relativeLayoutRellay1.removeView(idcardview);
                idcardview.setVisibility(View.INVISIBLE);
                captureImage();
            }

        });

        //calling the method login
        //Appel de la méthode login
        findViewById(R.id.linlay3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.buttonInserUpdatetBot).startAnimation(alpha);

                if(modify == true){
                    bottleUpdate(selectbottle);
                }else{
                    bottleInsert(idcategorie);
                }
            }
        });

        findViewById(R.id.idcardview10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File Photo_Signa = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId());
                File file = new File(Photo_Signa.getPath(), "bottle_" + idbottle + ".jpg");
                Intent i = new Intent(Intent.ACTION_VIEW);
                String ext = file.getName().substring(file.getName().indexOf(".") + 1).toLowerCase();

                if(ext.equals("jpg")) {
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
        });

        SpinnerType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                GType = TypeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpinnerContainer.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gContainer = containerAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpinnerQte.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                GQte = QteAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpinnerNote.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gNote = noteAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpinnerYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gYear = yearAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpinnerCat.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gcategorie = catAdapter.getItem(position);
                // Spinner Categorie
                for(indice =1; indice < catList.size()-1; indice ++){
                    if(gcategorie.equals(catList.get(indice).getName())){
                        gIdCategorie = catList.get(indice).getId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpinnerGarde.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gGarde = gardeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si on a appuyé sur le retour arrière
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(clauseWhere != null) {
                Intent BottleList = new Intent(getApplicationContext(), BottleList.class);
                finish();
                BottleList.putExtra("clauseWhere", clauseWhere);
                BottleList.putExtra("clauseWhereId", clauseWhereId);
                startActivity(BottleList);
            }
            if(suggest == true){
                Intent suggestListActivity = new Intent(getApplicationContext(), BottleListSuggest.class);

                finish();
                startActivity(suggestListActivity);
            }
            else {
                Intent CategorieActivity = new Intent(getApplicationContext(), CategorieActivity.class);
                finish();
                startActivity(CategorieActivity);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        this.handlerAnimationCIMG = new Handler();
    }

    private void configureToolbar(boolean modify, String nameBottle, String type) {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        /*Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar actionBar = getSupportActionBar();*/
        actionBar.setCustomView(R.layout.toolbar_profile_config);

        TextView titleToolbar =  findViewById(R.id.titleToolbarProfileConfig);

        ImageView buttonCreationBotBack = findViewById(R.id.buttonCreationBotBack);
        ImageView buttonCreationBotBackWhite = findViewById(R.id.buttonCreationBotBackWhite);
        ImageView buttonCreationBotBackChamp = findViewById(R.id.buttonCreationBotBackChamp);
        ImageView buttonCreationBotBackRose = findViewById(R.id.buttonCreationBotBackRose);

        if(modify == true) titleToolbar.setText(nameBottle);
        else  titleToolbar.setText(" ");


        if(type.equals("Champagne"))
        {
            buttonCreationBotBackChamp.setVisibility(View.VISIBLE);
            buttonCreationBotBack.setVisibility(View.INVISIBLE);
            buttonCreationBotBackWhite.setVisibility(View.INVISIBLE);
            buttonCreationBotBackRose.setVisibility(View.INVISIBLE);
        }
        if(type.equals("Blanc"))
        {
            buttonCreationBotBackWhite.setVisibility(View.VISIBLE);
            buttonCreationBotBack.setVisibility(View.INVISIBLE);
            buttonCreationBotBackChamp.setVisibility(View.INVISIBLE);
            buttonCreationBotBackRose.setVisibility(View.INVISIBLE);
        }
        if(type.equals("Rosé"))
        {
            buttonCreationBotBackRose.setVisibility(View.VISIBLE);
            buttonCreationBotBack.setVisibility(View.INVISIBLE);
            buttonCreationBotBackChamp.setVisibility(View.INVISIBLE);
            buttonCreationBotBackWhite.setVisibility(View.INVISIBLE);
        }
        if(type.equals("Rouge"))
        {
            buttonCreationBotBack.setVisibility(View.VISIBLE);
            buttonCreationBotBackChamp.setVisibility(View.INVISIBLE);
            buttonCreationBotBackWhite.setVisibility(View.INVISIBLE);
            buttonCreationBotBackRose.setVisibility(View.INVISIBLE);
        }


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){

        if(clauseWhere != null) {
            Intent BottleList = new Intent(getApplicationContext(), BottleList.class);
            finish();
            BottleList.putExtra("clauseWhere", clauseWhere);
            BottleList.putExtra("clauseWhereId", clauseWhereId);
            startActivity(BottleList);
        }
        if(suggest == true){
            Intent suggestListActivity = new Intent(getApplicationContext(), BottleListSuggest.class);

            finish();
            startActivity(suggestListActivity);
        }
        else {
            finish();
            startActivity(new Intent(getApplicationContext(), CategorieActivity.class));
        }

        return true;
    }

    private Runnable runnableAnim = new Runnable(){
        @Override
        public void run() {
            imageViewCreaBotAnim.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    imageViewCreaBotAnim.setScaleX(1f);
                    imageViewCreaBotAnim.setScaleY(1f);
                    imageViewCreaBotAnim.setAlpha(1f);
                }
            });

            imageViewCreaBotAnim2.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(700).withEndAction(new Runnable() {
                @Override
                public void run() {
                    imageViewCreaBotAnim2.setScaleX(1f);
                    imageViewCreaBotAnim2.setScaleY(1f);
                    imageViewCreaBotAnim2.setAlpha(1f);
                }
            });

            handlerAnimationCIMG.postDelayed(runnableAnim, 1500);
        }
    };

    private void bottleInsert(final int idcategorie) {
        /*
        first getting the values
        On récupère le entrées
        */
        final User user = SharedPrefManager.getInstance(this).getUser();
        final String name = editTextBottlename.getText().toString();
        final String domain = editTextBottledomain.getText().toString();
        final String comment = editTextBottlecomment.getText().toString();
        final String provider = editTextBottleProvider.getText().toString();
        final String price = editTextBottlePrice.getText().toString();
        final String purchaseDate;
        purchaseDate = (datePickerPurchaseDate.getYear() +"-"+ (datePickerPurchaseDate.getMonth()) + "-" +datePickerPurchaseDate.getDayOfMonth());


        final int year = gYear;
        final String type = GType;
        final String container = gContainer;
        final int note = gNote;
        final String garde = gGarde;

        final int[] l_id_bottle = new int[1];

        final Resources Res = getResources();

        final boolean[] retour_sql = new boolean[1];
        retour_sql[0] = true;

        //validating inputs
        //Valide les entrees
        if (TextUtils.isEmpty(name)) {
            editTextBottlename.setError(Res.getString(R.string.EnterBottleName));
            editTextBottlename.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(domain)) {
            editTextBottledomain.setText(" ");
            /*editTextBottledomain.setError(Res.getString(R.string.EnterDomainName));
            editTextBottledomain.requestFocus();*/
            return;
        }

        if (TextUtils.isEmpty(comment)) {
            editTextBottlecomment.setText(" ");
        }

        //if everything is fine
        //Si tout est OK
        class BottleInsert extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @SuppressLint("WrongThread")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        //JSONObject userJson = obj.getJSONObject("user");
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                        }

                        JSONObject userJson = sous_key.getJSONObject(0);
                        l_id_bottle[0] = userJson.getInt("max(id)");
                        retour_sql[0] =false;
                    }


                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (retour_sql[0] == false) {

                        String message_affiche = Res.getString(R.string.BottleCreate,name);
                        Toast.makeText(getApplicationContext(), message_affiche, Toast.LENGTH_SHORT).show();

                        finish();
                        startActivity(new Intent(getApplicationContext(), CategorieActivity.class));

                    }
                    if (retour_sql[0] != false) {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.AllreadyExiste,name), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                imgPhotoBot = findViewById(R.id.imgPhotoBot);

                Drawable drawable = imgPhotoBot.getDrawable();

                if (drawable!= null) {
                    Bitmap Bipdrawable = ((BitmapDrawable) imgPhotoBot.getDrawable()).getBitmap();
                    File Photo_Signa = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId());

                    File file = new File(Photo_Signa.getPath(), "bottle_" + l_id_bottle[0] + ".jpg");

                    if (!file.exists()) {
                        //("path", file.toString());
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            Bipdrawable.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //Création de l'objet request handler

                String replaceName;


                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //Creation des parametres request
                HashMap<String, String> params = new HashMap<>();
                replaceName = name.replaceAll("'","''");
                params.put("name", replaceName);
                params.put("domain", domain);
                params.put("qte", Integer.toString(GQte));
                params.put("idcategorie", Integer.toString(gIdCategorie));
                params.put("iduser", Integer.toString(user.getId()));
                params.put("type", type);
                params.put("year", Integer.toString(year));
                params.put("comment", comment);
                params.put("note", Integer.toString(note));
                params.put("container", container);
                params.put("garde", garde);
                params.put("provider", provider);
                params.put("price", price);
                params.put("purchasedate", purchaseDate);

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_INSERTBOT, params);
            }
        }

        BottleInsert bi = new BottleInsert();
        bi.execute();
    }

    private void bottleSelect(final int idbotlle) {

        final Resources Res = getResources();

        class BottleSelect extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                Bottle bot = null;

                try {

                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        //JSONObject userJson = obj.getJSONObject("user");
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                        }

                        JSONObject userJson = sous_key.getJSONObject(0);

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
                        userJson.getString("catname");


                        editTextBottlename.setText(bot.getName());
                        editTextBottledomain.setText(bot.getDomain());
                        SpinnerQte.setSelection(bot.getQte());

                        // Spinner Categorie
                        //editTextBottlecathegorie.setText(categoriename);
                        for(indice =1; indice < catList.size()-1; indice ++){
                            if(bot.getIdcategorie() == catList.get(indice).getId()){
                                gIdCategorie = catList.get(indice).getId();
                                SpinnerCat.setSelection(indice-1);
                            }
                        }

                        if(bot.getType().compareTo("Rouge") == 0) SpinnerType.setSelection(0);
                        if(bot.getType().compareTo("Blanc") == 0) SpinnerType.setSelection(1);
                        if(bot.getType().compareTo("Rosé") == 0) SpinnerType.setSelection(2);
                        if(bot.getType().compareTo("Champagne") == 0) SpinnerType.setSelection(3);

                        if(bot.getGarde().compareTo("A garder") == 0) SpinnerGarde.setSelection(0);
                        if(bot.getGarde().compareTo("A consommer") == 0) SpinnerGarde.setSelection(1);
                        if(bot.getGarde().compareTo("A offrir") == 0) SpinnerGarde.setSelection(2);

                        if(bot.getContainer().compareTo("37,5cl - Demi-Bouteille") == 0) SpinnerContainer.setSelection(0);
                        if(bot.getContainer().compareTo("50cl - Médium") == 0) SpinnerContainer.setSelection(1);
                        if(bot.getContainer().compareTo("75cl - Bouteille") == 0) SpinnerContainer.setSelection(2);
                        if(bot.getContainer().compareTo("1,5L - Magnum") == 0) SpinnerContainer.setSelection(3);
                        if(bot.getContainer().compareTo("3L - Jéroboam") == 0) SpinnerContainer.setSelection(4);
                        if(bot.getContainer().compareTo("4,5cl - Réhoboam") == 0) SpinnerContainer.setSelection(5);
                        if(bot.getContainer().compareTo("6L - Mathusalem") == 0) SpinnerContainer.setSelection(6);
                        if(bot.getContainer().compareTo("9L - Salmanazar") == 0) SpinnerContainer.setSelection(7);
                        if(bot.getContainer().compareTo("12L - Balthazar") == 0) SpinnerContainer.setSelection(8);

                        if(bot.getYear() == Integer.parseInt("2030")) SpinnerYear.setSelection(0);
                        if(bot.getYear() == Integer.parseInt("2029")) SpinnerYear.setSelection(1);
                        if(bot.getYear() == Integer.parseInt("2028")) SpinnerYear.setSelection(2);
                        if(bot.getYear() == Integer.parseInt("2027")) SpinnerYear.setSelection(3);
                        if(bot.getYear() == Integer.parseInt("2026")) SpinnerYear.setSelection(4);
                        if(bot.getYear() == Integer.parseInt("2025")) SpinnerYear.setSelection(5);
                        if(bot.getYear() == Integer.parseInt("2024")) SpinnerYear.setSelection(6);
                        if(bot.getYear() == Integer.parseInt("2023")) SpinnerYear.setSelection(7);
                        if(bot.getYear() == Integer.parseInt("2022")) SpinnerYear.setSelection(8);
                        if(bot.getYear() == Integer.parseInt("2021")) SpinnerYear.setSelection(9);
                        if(bot.getYear() == Integer.parseInt("2020")) SpinnerYear.setSelection(10);
                        if(bot.getYear() == Integer.parseInt("2019")) SpinnerYear.setSelection(11);
                        if(bot.getYear() == Integer.parseInt("2018")) SpinnerYear.setSelection(12);
                        if(bot.getYear() == Integer.parseInt("2017")) SpinnerYear.setSelection(13);
                        if(bot.getYear() == Integer.parseInt("2016")) SpinnerYear.setSelection(14);
                        if(bot.getYear() == Integer.parseInt("2015")) SpinnerYear.setSelection(15);
                        if(bot.getYear() == Integer.parseInt("2014")) SpinnerYear.setSelection(16);
                        if(bot.getYear() == Integer.parseInt("2013")) SpinnerYear.setSelection(17);
                        if(bot.getYear() == Integer.parseInt("2012")) SpinnerYear.setSelection(18);
                        if(bot.getYear() == Integer.parseInt("2011")) SpinnerYear.setSelection(19);
                        if(bot.getYear() == Integer.parseInt("2010")) SpinnerYear.setSelection(20);
                        if(bot.getYear() == Integer.parseInt("2009")) SpinnerYear.setSelection(21);
                        if(bot.getYear() == Integer.parseInt("2008")) SpinnerYear.setSelection(22);
                        if(bot.getYear() == Integer.parseInt("2007")) SpinnerYear.setSelection(23);
                        if(bot.getYear() == Integer.parseInt("2006")) SpinnerYear.setSelection(24);
                        if(bot.getYear() == Integer.parseInt("2005")) SpinnerYear.setSelection(25);
                        if(bot.getYear() == Integer.parseInt("2004")) SpinnerYear.setSelection(26);
                        if(bot.getYear() == Integer.parseInt("2003")) SpinnerYear.setSelection(27);
                        if(bot.getYear() == Integer.parseInt("2002")) SpinnerYear.setSelection(28);
                        if(bot.getYear() == Integer.parseInt("2001")) SpinnerYear.setSelection(29);
                        if(bot.getYear() == Integer.parseInt("2000")) SpinnerYear.setSelection(30);
                        if(bot.getYear() == Integer.parseInt("1999")) SpinnerYear.setSelection(31);
                        if(bot.getYear() == Integer.parseInt("1998")) SpinnerYear.setSelection(32);
                        if(bot.getYear() == Integer.parseInt("1997")) SpinnerYear.setSelection(33);
                        if(bot.getYear() == Integer.parseInt("1996")) SpinnerYear.setSelection(34);
                        if(bot.getYear() == Integer.parseInt("1995")) SpinnerYear.setSelection(35);
                        if(bot.getYear() == Integer.parseInt("1994")) SpinnerYear.setSelection(36);
                        if(bot.getYear() == Integer.parseInt("1993")) SpinnerYear.setSelection(37);
                        if(bot.getYear() == Integer.parseInt("1992")) SpinnerYear.setSelection(38);
                        if(bot.getYear() == Integer.parseInt("1991")) SpinnerYear.setSelection(39);
                        if(bot.getYear() == Integer.parseInt("1990")) SpinnerYear.setSelection(40);

                        SpinnerNote.setSelection(bot.getNote()-1);
                        editTextBottlecomment.setText(bot.getComment());
                        editTextBottleProvider.setText(bot.getProvider());
                        editTextBottlePrice.setText(Integer.toString(bot.getPrice()));

                        int year = Integer.parseInt(bot.getPurchaseDate().substring(0,4));
                        int month = Integer.parseInt(bot.getPurchaseDate().substring(5,7));
                        int day = Integer.parseInt(bot.getPurchaseDate().substring(8,10));

                        datePickerPurchaseDate.init(year, month, day, null);

                        //configuration de la toolbar
                        configureToolbar(true, bot.getName(), bot.getType());

                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorCreationCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                selectbottle = bot;
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //Création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //Creation des parametres request
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(idbotlle));

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_SELECTBOT_BOT, params);
            }
        }

        BottleSelect bi = new BottleSelect();
        bi.execute();
    }

    private void bottleSuggestSelect(final int idbotlle) {

        final Resources Res = getResources();
        Bottle BotTemp = null;

        class BottleSelect extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                BottleSuggest bot = null;

                try {

                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        //JSONObject userJson = obj.getJSONObject("user");
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                        }

                        Log.d("BottleCreationActivity","key " + key);

                        JSONObject userJson = sous_key.getJSONObject(0);

                        Log.d("BottleCreationActivity","userJson " + userJson);

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
                                0);
                        userJson.getString("catname");

                        editTextBottlename.setText(bot.getName());
                        editTextBottledomain.setText(bot.getDomain());
                        SpinnerQte.setSelection(bot.getQte());

                        // Spinner Categorie
                        //editTextBottlecathegorie.setText(categoriename);
                        for(indice =1; indice < catList.size()-1; indice ++){
                            if(bot.getIdcategorie() == catList.get(indice).getId()){
                                gIdCategorie = catList.get(indice).getId();
                                SpinnerCat.setSelection(indice-1);
                            }
                        }

                        if(bot.getType().compareTo("Rouge") == 0) SpinnerType.setSelection(0);
                        if(bot.getType().compareTo("Blanc") == 0) SpinnerType.setSelection(1);
                        if(bot.getType().compareTo("Rosé") == 0) SpinnerType.setSelection(2);
                        if(bot.getType().compareTo("Champagne") == 0) SpinnerType.setSelection(3);

                        if(bot.getGarde().compareTo("A garder") == 0) SpinnerGarde.setSelection(0);
                        if(bot.getGarde().compareTo("A consommer") == 0) SpinnerGarde.setSelection(1);
                        if(bot.getGarde().compareTo("A offrir") == 0) SpinnerGarde.setSelection(2);

                        if(bot.getContainer().compareTo("37,5cl - Demi-Bouteille") == 0) SpinnerContainer.setSelection(0);
                        if(bot.getContainer().compareTo("50cl - Médium") == 0) SpinnerContainer.setSelection(1);
                        if(bot.getContainer().compareTo("75cl - Bouteille") == 0) SpinnerContainer.setSelection(2);
                        if(bot.getContainer().compareTo("1,5L - Magnum") == 0) SpinnerContainer.setSelection(3);
                        if(bot.getContainer().compareTo("3L - Jéroboam") == 0) SpinnerContainer.setSelection(4);
                        if(bot.getContainer().compareTo("4,5cl - Réhoboam") == 0) SpinnerContainer.setSelection(5);
                        if(bot.getContainer().compareTo("6L - Mathusalem") == 0) SpinnerContainer.setSelection(6);
                        if(bot.getContainer().compareTo("9L - Salmanazar") == 0) SpinnerContainer.setSelection(7);
                        if(bot.getContainer().compareTo("12L - Balthazar") == 0) SpinnerContainer.setSelection(8);

                        if(bot.getYear() == Integer.parseInt("2030")) SpinnerYear.setSelection(0);
                        if(bot.getYear() == Integer.parseInt("2029")) SpinnerYear.setSelection(1);
                        if(bot.getYear() == Integer.parseInt("2028")) SpinnerYear.setSelection(2);
                        if(bot.getYear() == Integer.parseInt("2027")) SpinnerYear.setSelection(3);
                        if(bot.getYear() == Integer.parseInt("2026")) SpinnerYear.setSelection(4);
                        if(bot.getYear() == Integer.parseInt("2025")) SpinnerYear.setSelection(5);
                        if(bot.getYear() == Integer.parseInt("2024")) SpinnerYear.setSelection(6);
                        if(bot.getYear() == Integer.parseInt("2023")) SpinnerYear.setSelection(7);
                        if(bot.getYear() == Integer.parseInt("2022")) SpinnerYear.setSelection(8);
                        if(bot.getYear() == Integer.parseInt("2021")) SpinnerYear.setSelection(9);
                        if(bot.getYear() == Integer.parseInt("2020")) SpinnerYear.setSelection(10);
                        if(bot.getYear() == Integer.parseInt("2019")) SpinnerYear.setSelection(11);
                        if(bot.getYear() == Integer.parseInt("2018")) SpinnerYear.setSelection(12);
                        if(bot.getYear() == Integer.parseInt("2017")) SpinnerYear.setSelection(13);
                        if(bot.getYear() == Integer.parseInt("2016")) SpinnerYear.setSelection(14);
                        if(bot.getYear() == Integer.parseInt("2015")) SpinnerYear.setSelection(15);
                        if(bot.getYear() == Integer.parseInt("2014")) SpinnerYear.setSelection(16);
                        if(bot.getYear() == Integer.parseInt("2013")) SpinnerYear.setSelection(17);
                        if(bot.getYear() == Integer.parseInt("2012")) SpinnerYear.setSelection(18);
                        if(bot.getYear() == Integer.parseInt("2011")) SpinnerYear.setSelection(19);
                        if(bot.getYear() == Integer.parseInt("2010")) SpinnerYear.setSelection(20);
                        if(bot.getYear() == Integer.parseInt("2009")) SpinnerYear.setSelection(21);
                        if(bot.getYear() == Integer.parseInt("2008")) SpinnerYear.setSelection(22);
                        if(bot.getYear() == Integer.parseInt("2007")) SpinnerYear.setSelection(23);
                        if(bot.getYear() == Integer.parseInt("2006")) SpinnerYear.setSelection(24);
                        if(bot.getYear() == Integer.parseInt("2005")) SpinnerYear.setSelection(25);
                        if(bot.getYear() == Integer.parseInt("2004")) SpinnerYear.setSelection(26);
                        if(bot.getYear() == Integer.parseInt("2003")) SpinnerYear.setSelection(27);
                        if(bot.getYear() == Integer.parseInt("2002")) SpinnerYear.setSelection(28);
                        if(bot.getYear() == Integer.parseInt("2001")) SpinnerYear.setSelection(29);
                        if(bot.getYear() == Integer.parseInt("2000")) SpinnerYear.setSelection(30);
                        if(bot.getYear() == Integer.parseInt("1999")) SpinnerYear.setSelection(31);
                        if(bot.getYear() == Integer.parseInt("1998")) SpinnerYear.setSelection(32);
                        if(bot.getYear() == Integer.parseInt("1997")) SpinnerYear.setSelection(33);
                        if(bot.getYear() == Integer.parseInt("1996")) SpinnerYear.setSelection(34);
                        if(bot.getYear() == Integer.parseInt("1995")) SpinnerYear.setSelection(35);
                        if(bot.getYear() == Integer.parseInt("1994")) SpinnerYear.setSelection(36);
                        if(bot.getYear() == Integer.parseInt("1993")) SpinnerYear.setSelection(37);
                        if(bot.getYear() == Integer.parseInt("1992")) SpinnerYear.setSelection(38);
                        if(bot.getYear() == Integer.parseInt("1991")) SpinnerYear.setSelection(39);
                        if(bot.getYear() == Integer.parseInt("1990")) SpinnerYear.setSelection(40);

                        SpinnerNote.setSelection(bot.getNote()-1);
                        editTextBottlecomment.setText(bot.getComment());
                        editTextBottleProvider.setText(bot.getProvider());
                        editTextBottlePrice.setText(Integer.toString(bot.getPrice()));

                        int year = Integer.parseInt(bot.getPurchaseDate().substring(0,4));
                        int month = Integer.parseInt(bot.getPurchaseDate().substring(5,7));
                        int day = Integer.parseInt(bot.getPurchaseDate().substring(8,10));

                        datePickerPurchaseDate.init(year, month, day, null);

                        //configuration de la toolbar
                        configureToolbar(true, bot.getName(), bot.getType());

                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorCreationCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("BottleCreationActivity","bot.getName() " + bot.getName());


                selectbottle = new Bottle(bot.getId(),
                bot.getName(),
                bot.getDomain(),
                0,
                bot.getIdcategorie(),
                bot.getIduser(),
                bot.getType(),
                bot.getYear(),
                bot.getComment(),
                bot.getNote(),
                bot.getContainer(),
                bot.getGarde(),
                bot.getProvider(),
                bot.getPrice(),
                bot.getPurchaseDate());

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //Création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                Log.d("BottleCreationActivity","idbotlle " + idbotlle);

                //creating request parameters
                //Creation des parametres request
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(idbotlle));

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_SELECTBOT_BOT_SUGGEST, params);
            }
        }

        BottleSelect bi = new BottleSelect();
        bi.execute();
    }

    @SuppressLint("ResourceType")
    private void bottleUpdate(final Bottle bottle) {
        //first getting the values
        //On récupère le entrées
        final User loc_user = SharedPrefManager.getInstance(this).getUser();
        final int loc_id = bottle.getId();
        final String loc_name = editTextBottlename.getText().toString();
        final String loc_domain = editTextBottledomain.getText().toString();
        final int loc_qte = (int) SpinnerQte.getSelectedItemId();
        final int loc_note = (int) SpinnerNote.getSelectedItemId() + 1;
        String loc_year = null;
        String loc_type = null;
        String loc_container = null;
        String loc_garde = null;

        final String purchaseDate;
        purchaseDate = (datePickerPurchaseDate.getYear() +"-"+ (datePickerPurchaseDate.getMonth()) + "-" +datePickerPurchaseDate.getDayOfMonth());

        final Resources Res = getResources();

        final String loc_comment = editTextBottlecomment.getText().toString();
        final String loc_provider = editTextBottleProvider.getText().toString();
        final String loc_price = editTextBottlePrice.getText().toString();

        // Spinner Categorie
        //editTextBottlecathegorie.setText(categoriename);
        for(indice =1; indice < catList.size()-1; indice ++){
            if(SpinnerCat.getSelectedItemId() == indice){
                gIdCategorie = catList.get(indice+1).getId();
            }
        }

        if(SpinnerType.getSelectedItemId() == 0) loc_type = "Rouge" ;
        if(SpinnerType.getSelectedItemId() == 1) loc_type = "Blanc";
        if(SpinnerType.getSelectedItemId() == 2) loc_type = "Rosé";
        if(SpinnerType.getSelectedItemId() == 3) loc_type = "Champagne";

        if(SpinnerGarde.getSelectedItemId() == 0) loc_garde = "A garder" ;
        if(SpinnerGarde.getSelectedItemId() == 1) loc_garde = "A consommer";
        if(SpinnerGarde.getSelectedItemId() == 2) loc_garde = "A offrir";

        if(SpinnerContainer.getSelectedItemId() == 0) loc_container ="37,5cl - Demi-Bouteille";
        if(SpinnerContainer.getSelectedItemId() == 1) loc_container ="50cl - Médium";
        if(SpinnerContainer.getSelectedItemId() == 2) loc_container ="75cl - Bouteille";
        if(SpinnerContainer.getSelectedItemId() == 3) loc_container ="1,5L - Magnum" ;
        if(SpinnerContainer.getSelectedItemId() == 4) loc_container ="3L - Jéroboam" ;
        if(SpinnerContainer.getSelectedItemId() == 5) loc_container ="4,5cl - Réhoboam";
        if(SpinnerContainer.getSelectedItemId() == 6) loc_container ="6L - Mathusalem";
        if(SpinnerContainer.getSelectedItemId() == 7) loc_container ="9L - Salmanazar" ;
        if(SpinnerContainer.getSelectedItemId() == 8) loc_container ="12L - Balthazar";

        if(SpinnerYear.getSelectedItemId() == 0) loc_year ="2030";
        if(SpinnerYear.getSelectedItemId() == 1) loc_year ="2029";
        if(SpinnerYear.getSelectedItemId() == 2) loc_year ="2028";
        if(SpinnerYear.getSelectedItemId() == 3) loc_year ="2027";
        if(SpinnerYear.getSelectedItemId() == 4) loc_year ="2026";
        if(SpinnerYear.getSelectedItemId() == 5) loc_year ="2025";
        if(SpinnerYear.getSelectedItemId() == 6) loc_year ="2024";
        if(SpinnerYear.getSelectedItemId() == 7) loc_year ="2023";
        if(SpinnerYear.getSelectedItemId() == 8) loc_year ="2022";
        if(SpinnerYear.getSelectedItemId() == 9) loc_year ="2021";
        if(SpinnerYear.getSelectedItemId() == 10) loc_year ="2020";
        if(SpinnerYear.getSelectedItemId() == 11) loc_year ="2019";
        if(SpinnerYear.getSelectedItemId() == 12) loc_year ="2018";
        if(SpinnerYear.getSelectedItemId() == 13) loc_year ="2017";
        if(SpinnerYear.getSelectedItemId() == 14) loc_year ="2016";
        if(SpinnerYear.getSelectedItemId() == 15) loc_year ="2015";
        if(SpinnerYear.getSelectedItemId() == 16) loc_year ="2014";
        if(SpinnerYear.getSelectedItemId() == 17) loc_year ="2013";
        if(SpinnerYear.getSelectedItemId() == 18) loc_year ="2012";
        if(SpinnerYear.getSelectedItemId() == 19) loc_year ="2011";
        if(SpinnerYear.getSelectedItemId() == 20) loc_year ="2010";
        if(SpinnerYear.getSelectedItemId() == 21) loc_year ="2009";
        if(SpinnerYear.getSelectedItemId() == 22) loc_year ="2008";
        if(SpinnerYear.getSelectedItemId() == 23) loc_year ="2007";
        if(SpinnerYear.getSelectedItemId() == 24) loc_year ="2006";
        if(SpinnerYear.getSelectedItemId() == 25) loc_year ="2005";
        if(SpinnerYear.getSelectedItemId() == 26) loc_year ="2004";
        if(SpinnerYear.getSelectedItemId() == 27) loc_year ="2003";
        if(SpinnerYear.getSelectedItemId() == 28) loc_year ="2002";
        if(SpinnerYear.getSelectedItemId() == 29) loc_year ="2001";
        if(SpinnerYear.getSelectedItemId() == 30) loc_year ="2000";
        if(SpinnerYear.getSelectedItemId() == 31) loc_year ="1999";
        if(SpinnerYear.getSelectedItemId() == 32) loc_year ="1998";
        if(SpinnerYear.getSelectedItemId() == 33) loc_year ="1997";
        if(SpinnerYear.getSelectedItemId() == 34) loc_year ="1996";
        if(SpinnerYear.getSelectedItemId() == 35) loc_year ="1995";
        if(SpinnerYear.getSelectedItemId() == 36) loc_year ="1994";
        if(SpinnerYear.getSelectedItemId() == 37) loc_year ="1993";
        if(SpinnerYear.getSelectedItemId() == 38) loc_year ="1992";
        if(SpinnerYear.getSelectedItemId() == 39) loc_year ="1991";
        if(SpinnerYear.getSelectedItemId() == 40) loc_year ="1990";


        //validating inputs
        //Valide les entrees
        if (TextUtils.isEmpty(loc_name)) {
            editTextBottlename.setError(Res.getString(R.string.EnterBottleName));
            editTextBottlename.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(loc_domain)) {
            editTextBottlename.setError(Res.getString(R.string.EnterDomainName));
            editTextBottledomain.requestFocus();
            return;
        }

        //if everything is fine
        //Si tout est OK
        final String finalLoc_type = loc_type;
        final String finalLoc_container = loc_container;
        final String finalLoc_year = loc_year;
        final String finalLoc_garde = loc_garde;

        class BottleUpdate extends AsyncTask<Void, Void, String> {

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

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(),Res.getString(R.string.BottleModifiee, loc_name), Toast.LENGTH_SHORT).show();

                        if(clauseWhere != null) {
                            Intent BottleList = new Intent(getApplicationContext(), BottleList.class);
                            finish();
                            BottleList.putExtra("clauseWhere", clauseWhere);
                            BottleList.putExtra("clauseWhereId", clauseWhereId);
                            startActivity(BottleList);
                        }
                        else {
                            Intent CategorieActivity = new Intent(getApplicationContext(), CategorieActivity.class);
                            finish();
                            startActivity(CategorieActivity);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorCreationCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //Création de l'objet request handler

                String replaceName;

                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //Creation des parametres request
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(loc_id));

                replaceName = loc_name.replaceAll("'","''");
                params.put("name", replaceName);
                params.put("domain", loc_domain);
                params.put("qte", Integer.toString(loc_qte));
                params.put("idcategorie", Integer.toString(gIdCategorie));
                params.put("iduser", Integer.toString(loc_user.getId()));
                params.put("type", finalLoc_type);
                params.put("year", finalLoc_year);
                params.put("comment", loc_comment);
                params.put("note", Integer.toString(loc_note));
                params.put("container", finalLoc_container);
                params.put("garde", finalLoc_garde);
                params.put("provider", loc_provider);
                params.put("price", loc_price);
                params.put("purchaseDate", purchaseDate);


                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_UPDATEBOT, params);
            }
        }

        BottleUpdate bi = new BottleUpdate();
        bi.execute();
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        Bitmap selectedImage = null;
        User user = SharedPrefManager.getInstance(this).getUser();

        try {
            if (reqCode == REQUEST_ID_IMAGE_CAPTURE) {
                if (resultCode == RESULT_OK) {
                    //selectedImage = (Bitmap) data.getExtras().get("data");
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    getContentResolver().delete(imageUri, null,null);
                    this.imgPhotoBot.setImageBitmap(selectedImage);
                } else if (resultCode == RESULT_CANCELED)
                {
                    displayImageBottle();
                }
                else {
                    displayImageBottle();
                }
            }
            else {
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    //getContentResolver().delete(imageUri, null,null);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                }
                else if (resultCode == RESULT_CANCELED) {
                    displayImageBottle();
                } else {
                    displayImageBottle();
                }
            }

            if(selectedImage != null) {
                File Photo_Signa = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId());

                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            if (!Photo_Signa.exists()) {
                                try {
                                    Photo_Signa.mkdirs();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            File file = new File(Photo_Signa.getPath(), "bottle_" + idbottle + ".jpg");
                            try {
                                ByteArrayOutputStream bos  = new ByteArrayOutputStream ();
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos );
                                byte[] bitmapdata  = bos.toByteArray();
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            requestPermission(); // Code for permission
                        }
                    } else {

                        File file = new File(Photo_Signa.getPath(), "bottle_" + idbottle + ".jpg");
                        if (!file.exists()) {
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                Glide.with(this).load(selectedImage).apply(new RequestOptions().autoClone()).into(imgPhotoBot);
                idcardview.setVisibility(View.VISIBLE);
            } else {
                displayImageBottle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(BottleCreationActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(BottleCreationActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(BottleCreationActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(BottleCreationActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
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

    private void displayImageBottle() {

        final User user = SharedPrefManager.getInstance(this).getUser();

        Bitmap selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId()+"/bottle_" + idbottle +".jpg");
        File file = new File(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId()+"/bottle_" + idbottle +".jpg");

        if (!file.exists() || idbottle == -1)
        {
            Glide.with(this).load(R.drawable.icon_bottle).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
            imageViewCreaBotAnim2.setVisibility(View.VISIBLE);
            imageViewCreaBotAnim.setVisibility(View.VISIBLE);
            imageViewimgUser.setVisibility(View.VISIBLE);
            idcardview.setVisibility(View.INVISIBLE);
        }
        else {
            Glide.with(this).load(selectedImage).apply(new RequestOptions().centerInside()).into(imgPhotoBot);
            imageViewCreaBotAnim2.setVisibility(View.INVISIBLE);
            imageViewCreaBotAnim.setVisibility(View.INVISIBLE);
            imageViewimgUser.setVisibility(View.INVISIBLE);
            idcardview.setVisibility(View.VISIBLE);
        }
    }

    private void friendSelect(final int id) {

        final Resources Res = getResources();


        class BottleSelect extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                Bottle bot = null;
                User user;

                try {

                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        //JSONObject userJson = obj.getJSONObject("user");
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                        }

                        for (int i = 0; i < sous_key.length(); i++) {
                            JSONObject userJson = sous_key.getJSONObject(i);
                            user = new User(
                                    userJson.getInt("id"),
                                    userJson.getString("username"),
                                    userJson.getString("email"),
                                    userJson.getString("gender"),
                                    userJson.getString("name"),
                                    userJson.getString("caveName"),
                                    userJson.getInt("nb_bottle_max"),
                                    userJson.getInt("public")
                            );
                            userListFriend.add(user);
                            Log.d("BottleCreationActivity", "getUsername " + user.getUsername());
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorCreationCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //Création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //Creation des parametres request
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_SELECTUSER_FRIEND, params);
            }
        }

        BottleSelect bi = new BottleSelect();
        bi.execute();
    }

    private void bottleSuggestInsert(final int id_user_friend) {
        /*
        first getting the values
        On récupère le entrées
        */
        final User user = SharedPrefManager.getInstance(this).getUser();
        final String name = editTextBottlename.getText().toString();
        final String domain = editTextBottledomain.getText().toString();
        final String comment = editTextBottlecomment.getText().toString();
        final String provider = editTextBottleProvider.getText().toString();
        final String price = editTextBottlePrice.getText().toString();
        final String purchaseDate;
        purchaseDate = (datePickerPurchaseDate.getYear() +"-"+ (datePickerPurchaseDate.getMonth()) + "-" +datePickerPurchaseDate.getDayOfMonth());


        final int year = gYear;
        final String type = GType;
        final String container = gContainer;
        final int note = gNote;
        final String garde = gGarde;

        final int[] l_id_bottle = new int[1];

        final Resources Res = getResources();

        final boolean[] retour_sql = new boolean[1];
        retour_sql[0] = true;

        //validating inputs
        //Valide les entrees
        if (TextUtils.isEmpty(name)) {
            editTextBottlename.setError(Res.getString(R.string.EnterBottleName));
            editTextBottlename.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(domain)) {
            editTextBottledomain.setText(" ");
            /*editTextBottledomain.setError(Res.getString(R.string.EnterDomainName));
            editTextBottledomain.requestFocus();*/
            return;
        }

        if (TextUtils.isEmpty(comment)) {
            editTextBottlecomment.setText(" ");
        }

        //if everything is fine
        //Si tout est OK
        class BottleInsert extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @SuppressLint("WrongThread")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {

                        JSONArray key = obj.names();
                        //JSONObject userJson = obj.getJSONObject("user");
                        for (int i = 0; i < key.length(); ++i) {
                            String keys = key.getString(i);
                            if (i == 2) {
                                sous_key = obj.getJSONArray(keys);
                            }
                        }

                        Log.d("BottleCreationActivity","key " + key);

                        JSONObject userJson = sous_key.getJSONObject(0);
                        l_id_bottle[0] = userJson.getInt("max(id)");
                        retour_sql[0] =false;
                    }


                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (retour_sql[0] == false) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.BottleSend,name), Toast.LENGTH_SHORT).show();
                    }
                    if (retour_sql[0] != false) {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.AllreadyExiste,name), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                imgPhotoBot = findViewById(R.id.imgPhotoBot);

                Drawable drawable = imgPhotoBot.getDrawable();

                if (drawable!= null) {
                    Bitmap Bipdrawable = ((BitmapDrawable) imgPhotoBot.getDrawable()).getBitmap();
                    File Photo_Signa = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DIRECTORY_PICTURES + "/Stockovin/Bottle"+user.getId());

                    File file = new File(Photo_Signa.getPath(), "bottle_" + l_id_bottle[0] + ".jpg");

                    if (!file.exists()) {
                        //("path", file.toString());
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            Bipdrawable.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //Création de l'objet request handler

                String replaceName;


                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //Creation des parametres request
                HashMap<String, String> params = new HashMap<>();
                replaceName = name.replaceAll("'","''");
                params.put("name", replaceName);
                params.put("domain", domain);
                params.put("qte", Integer.toString(GQte));
                params.put("idcategorie", Integer.toString(gIdCategorie));
                params.put("iduser", Integer.toString(id_user_friend));
                params.put("type", type);
                params.put("year", Integer.toString(year));
                params.put("comment", comment);
                params.put("note", Integer.toString(note));
                params.put("container", container);
                params.put("garde", garde);
                params.put("provider", provider);
                params.put("price", price);
                params.put("purchasedate", purchaseDate);
                params.put("id_user_friend", Integer.toString(user.getId()));

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_INSERTBOT_SUGGEST, params);
            }
        }

        BottleInsert bi = new BottleInsert();
        bi.execute();
    }
}
