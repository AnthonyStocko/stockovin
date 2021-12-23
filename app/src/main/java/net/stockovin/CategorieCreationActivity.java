package net.stockovin;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class CategorieCreationActivity extends AppCompatActivity {

    Button buttonCatInsertUpdate;
    TextView TextCatInsertUpdate;

    EditText editTextCategoriename;
    int IdCategorie = 0;
    String CategorieName = null;
    boolean Modify;
    Categorie selectcat = null;

    Categorie Categorie = null;

    ProgressBar progressBar;

    Boolean newUserCatActivityBot;
    Integer g_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_categorie);

        progressBar = findViewById(R.id.progressBarCatInsert);

        Resources res = getResources();
        String chaine;

        editTextCategoriename = findViewById(R.id.editTextCategoriename);

        Intent i = getIntent();
        IdCategorie = i.getIntExtra(CategorieActivity.CATEGORIE, 0);
        CategorieName = i.getStringExtra(CategorieActivity.CATEGORIENAME);
        Modify = i.getBooleanExtra(CategorieActivity.MODIFY, false);
        newUserCatActivityBot = i.getBooleanExtra("newUserCatActivityBot", false);
        g_position = i.getIntExtra("POSITION", -1);

        if(Modify == true){
            chaine = res.getString(R.string.Modify);
            TextCatInsertUpdate = findViewById(R.id.buttonLogin);
            TextCatInsertUpdate.setText(chaine);
            categorieSelect(IdCategorie);
        }else{
            configureToolbar(false, "");
            chaine = res.getString(R.string.creation);
            TextCatInsertUpdate = findViewById(R.id.buttonLogin);
            TextCatInsertUpdate.setText(chaine);
        }

        //if user presses on login
        //Si l'utilisateur click sur login
        //calling the method login
        //Appel de la méthode login
        findViewById(R.id.linlay4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Modify == true) categorieUpdate();
                else categorieInsert();

            }
        });
    }

    private void configureToolbar(boolean modify, String nameCat) {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /*Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.toolbar_profile_config);*/

        TextView titleToolbar =  findViewById(R.id.titleToolbarProfileConfig);
        if(modify == true) titleToolbar.setText(nameCat);
        else titleToolbar.setText("");

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si on a appuyé sur le retour arrière
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(getApplicationContext(), CategorieActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        startActivity(new Intent(getApplicationContext(), CategorieActivity.class));

        return true;
    }

    private void categorieInsert() {
        //first getting the values
        //On récupère le entrées
        final User user = SharedPrefManager.getInstance(this).getUser();
        final String name = editTextCategoriename.getText().toString();
        final Resources Res = getResources();

        //validating inputs
        //Valide les entrees
        if (TextUtils.isEmpty(name)) {
            editTextCategoriename.setError(Res.getString(R.string.EnterCategorieName));
            editTextCategoriename.requestFocus();
            return;
        }

        //if everything is fine
        //Si tout est OK
        class CategorieInsert extends AsyncTask<Void, Void, String> {

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
                    Categorie categorie = null;

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
                            categorie = new Categorie(
                                    userJson.getInt("id"),
                                    userJson.getInt("iduser"),
                                    userJson.getString("name")
                            );
                        }

                        String message_affiche = Res.getString(R.string.CategorieCreee,categorie.getName());
                        //Toast.makeText(getApplicationContext(), message_affiche, Toast.LENGTH_SHORT).show();

                        Intent CategorieActivity = new Intent(getApplicationContext(), CategorieActivity.class);
                        finish();
                        CategorieActivity.putExtra("idCategorie", categorie.getId());
                        CategorieActivity.putExtra("newUserReturn", newUserCatActivityBot);
                        CategorieActivity.putExtra("POSITION",g_position);
                        startActivity(CategorieActivity);
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
                params.put("iduser", Integer.toString(user.getId()));
                params.put("name", name);

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_INSERTCAT, params);
            }
        }

        CategorieInsert ci = new CategorieInsert();
        ci.execute();
    }

    private void categorieSelect(final int idcategorie) {

        final Resources Res = getResources();

        class CategorieSelect extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                Categorie cat = null;

                try {

                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;

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

                        JSONObject userJson = sous_key.getJSONObject(0);
                        cat = new Categorie(
                                userJson.getInt("id"),
                                userJson.getInt("iduser"),
                                userJson.getString("name")
                        );

                        editTextCategoriename.setText(cat.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorCreationCategorie), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                selectcat = cat;
                configureToolbar(true, cat.getName());
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //Création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //Creation des parametres request
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(idcategorie));

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_SELECTCAT_CAT, params);
            }
        }

        CategorieSelect bi = new CategorieSelect();
        bi.execute();
    }

    private void categorieUpdate() {
        //first getting the values
        //On récupère le entrées
        Resources Res = getResources();

        final String name = editTextCategoriename.getText().toString();

        //validating inputs
        //Valide les entrees
        if (TextUtils.isEmpty(CategorieName)) {
            editTextCategoriename.setError(Res.getString(R.string.EnterCategorieName));
            editTextCategoriename.requestFocus();
            return;
        }

        //if everything is fine
        //Si tout est OK
        class CategorieUpdate extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;
            Resources Res = getResources();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBarCatInsert);
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
                        //Toast.makeText(getApplicationContext(),Res.getString(R.string.CategorieModifiee, CategorieName), Toast.LENGTH_SHORT).show();

                        finish();
                        startActivity(new Intent(getApplicationContext(), CategorieActivity.class));
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
                params.put("id", Integer.toString(IdCategorie));
                params.put("name", name);

                //returing the response
                //Retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_UPDATECAT, params);
            }
        }

        CategorieUpdate bi = new CategorieUpdate();
        bi.execute();
    }
}