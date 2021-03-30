package net.stockovin;


import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextPassword;
    RadioGroup radioGroupGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGender);


        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.buttonRegister).startAnimation(alpha);
                registerUser();
            }
        });

        findViewById(R.id.textViewLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.textViewLogin).startAnimation(alpha);
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si on a appuyé sur le retour arrière

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            //SharedPrefManager.getInstance(getApplicationContext()).logout();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }
        return super.onKeyDown(keyCode, event);
    }


    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //final String gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
        final String gender = "1";

        final Resources Res = getResources();

        //first we will do the validations
        //Validation des entrées

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError(Res.getString(R.string.EnterName));
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(Res.getString(R.string.EnterEmail));
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(Res.getString(R.string.EnterEmailValide));
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(Res.getString(R.string.EnterPassword));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length()<=7) {
            editTextPassword.setError(Res.getString(R.string.lengthPassword));
            editTextPassword.requestFocus();
            return;
        }

        //if it passes all the validations
        // Si toutes les validation sont bonnes

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //création de l'objet request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                //création des paramètres du request
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("gender", gender);

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                //Affichage de la barre de progression pendant l'enregistrement de l'utilisateur sur le serveur
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray sous_key = null;
                    User user = null;

                    //if no error in response
                    //Si pas d'erreur dans la réponse
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), Res.getString(R.string.CreateAccountOK), Toast.LENGTH_SHORT).show();

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
                                    userJson.getInt("public")
                            );
                        }

                        if (user != null) {
                            //storing the user in shared preferences
                            //On stock les préférences de l'utilisateur
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                            //starting the profile activity
                            //démarre le ProfileActivity
                            Intent ProfileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
                            finish();
                            ProfileActivity.putExtra("newUser", true);
                            startActivity(ProfileActivity);
                        }
                    } else {
                        String message = null;
                        JSONArray key = obj.names ();
                        for (int i = 0; i < key.length (); ++i) {
                            String keys = key.getString(i);
                            if( i == 1) {
                                message = obj.getString(keys);
                            }
                        }

                        Log.d("LoginActivity", "creation user message " + message);

                        if(message.equals("user exist"))
                        {
                            Toast.makeText(getApplicationContext(), "L'utilisateur existe déja", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (message.equals("email exist")) {
                                Toast.makeText(getApplicationContext(), "L'email est déjà utilisé", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorCreateAccount), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }
}
