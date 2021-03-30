package net.stockovin;


import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //MediationTestSuite.launch(MainActivity.this);

        if(!SharedPrefManager.getInstance(this).isLoggedIn())
        {
            // call Login Activity
        }
        else
        {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);


        //if user presses on login
        //calling the method login
        findViewById(R.id.imgLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.imgLogin).startAnimation(alpha);
                userLogin();
            }
        });

        //if user presses on not registered
        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.textViewRegister).startAnimation(alpha);
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        //if user presses on not registered
        findViewById(R.id.textViewForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.textViewForgotPassword).startAnimation(alpha);
                finish();
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si on a appuyé sur le retour arrière

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPrefManager.getInstance(getApplicationContext()).logout_close();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void userLogin() {
        //first getting the values
        //On récupère le entrées
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        final Resources Res = getResources();

        //validating inputs
        //Valide les entrees
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError(Res.getString(R.string.EnterNameLogin));
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(Res.getString(R.string.EnterPassword));
            editTextPassword.requestFocus();
            return;
        }

        //if everything is fine
        //Si tout est OK

        class UserLogin extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBar);
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
                    User user = null;

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
                            user = new User(
                                    userJson.getInt("id"),
                                    userJson.getString("username"),
                                    userJson.getString("email"),
                                    userJson.getString("gender"),
                                    userJson.getString("name"),
                                    userJson.getString("caveName"),
                                    userJson.getInt("nb_bottle_max"),
                                    userJson.getInt("public"));
                        }

                        if (user != null) {
                            //storing the user in shared preferences
                            //On stock les préférences de l'utilisateur
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                            Toast.makeText(getApplicationContext(),  Res.getString(R.string.LoginAccountOK), Toast.LENGTH_SHORT).show();

                            //starting the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }
                        else Toast.makeText(getApplicationContext(),  Res.getString(R.string.LoginAccountNOK), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),  Res.getString(R.string.LoginAccountNOK), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }
}