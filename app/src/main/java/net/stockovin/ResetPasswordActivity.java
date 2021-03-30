package net.stockovin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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
import java.util.Random;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText editTextEmail;
    User user;
    int password;
    String username;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editTextEmail = findViewById(R.id.editTextEmail);

        user = SharedPrefManager.getInstance(this).getUser();


        findViewById(R.id.buttonReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Resources Res = getResources();

                final String email = editTextEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError(Res.getString(R.string.EnterEmail));
                    editTextEmail.requestFocus();
                    return;
                }
                ResetPassword();

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


    private void ResetPassword() {
        email = editTextEmail.getText().toString().trim();

        final Resources Res = getResources();
        //first we will do the validations
        //Validation des entrées

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

        Random rand = new Random();
        password = rand.nextInt(89999999) + 10000000;

        Log.d("ResetPassword","password " + password);

        //if it passes all the validations
        // Si toutes les validation sont bonnes

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

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
                JSONArray sous_key = null;

                try {
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
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
                            username = userJson.getString("username");
                        }

                        sendMessage();

                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), Res.getString(R.string.ErrorModifUser), Toast.LENGTH_SHORT).show();
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
                params.put("email", email);
                params.put("password", Integer.toString(password));

                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_RESETPASSWORD, params);
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void sendMessage() {
        final ProgressDialog dialog = new ProgressDialog(ResetPasswordActivity.this);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        //dialog.show();

        final Resources Res = getResources();

        Thread sender = new Thread(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                try {

                    GMailSender sender = new GMailSender("stockovin@alwaysdata.net", "stockwesh");

                    Log.d("ResetPassword","password " + password + " username " +username);

                    sender.sendMail(Res.getString(R.string.objectMail),
                            Res.getString(R.string.bodyMail, username, Integer.toString(password)),
                            "stockovin@alwaysdata.net",
                            email);
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

}
