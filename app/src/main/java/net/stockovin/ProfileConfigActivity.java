package net.stockovin;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_PICTURES;


public class ProfileConfigActivity extends AppCompatActivity {

    EditText EditTextUserName,  EditTextUserEmail, EditTextUsercaveName, EditTextUserNbBottleMax, EditTextViewConfirmPassword, EditTextViewPassword;
    TextView textViewUsername,TextViewUserFirstName;
    CheckBox CheckBoxViewPublic;
    private ImageView imgAnim, imgAnim2, imgMiFoto;
    private Handler handlerAnimationCIMG;
    static final int RESULT_LOAD_IMG = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    //getting the current user
    User user = SharedPrefManager.getInstance(this).getUser();
    boolean b_public;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile3);
        this.configureToolbar();

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

        Log.d("ProfileConfigActivity", "b_public " + b_public);
        if(user.getPublic() == 1) b_public =true;
        else b_public = false;

        TextViewUserFirstName = (TextView) findViewById(R.id.TextViewUserFirstName);
        EditTextUserName = (EditText) findViewById(R.id.EditTextUserName);
        EditTextUserEmail = (EditText) findViewById(R.id.EditTextViewEmail);
        EditTextUsercaveName = (EditText) findViewById(R.id.EditTextViewCaveName);
        EditTextUserNbBottleMax = (EditText) findViewById(R.id.EditTextViewNbBottleMax);
        EditTextViewPassword = (EditText) findViewById(R.id.EditTextViewPassword);
        EditTextViewConfirmPassword = (EditText) findViewById(R.id.EditTextViewConfirmPassword);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        CheckBoxViewPublic = findViewById(R.id.CheckBoxViewPublic);


        textViewUsername.setText(user.getUsername());
        TextViewUserFirstName.setText(user.getUsername());
        EditTextUserName.setText(user.getName());
        EditTextUserEmail.setText(user.getEmail());
        EditTextUsercaveName.setText(user.getcaveName());
        EditTextUserNbBottleMax.setText(Integer.toString(user.getNbBottleMax()));
        CheckBoxViewPublic.setChecked(b_public);


        init();

        Bitmap selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() +".jpg");
        imgMiFoto.setImageBitmap(selectedImage);
        File file = new File(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin/Profile" + user.getId() +".jpg");

        if (!file.exists())  Glide.with(this).load(R.drawable.ic_user3).apply(new RequestOptions().circleCrop()).into(imgMiFoto);
        else  Glide.with(this).load(selectedImage).apply(new RequestOptions().circleCrop()).into(imgMiFoto);

        findViewById(R.id.imgMiFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.buttonRegister).startAnimation(alpha);
                userUpdate();
            }
        });

        findViewById(R.id.ButtonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                findViewById(R.id.ButtonLogout).startAnimation(alpha);

                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });

        this.runnableAnim.run();
        this.handlerAnimationCIMG.removeCallbacks(runnableAnim);
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

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Conf);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        return true;
    }

    private void init() {
        this.handlerAnimationCIMG = new Handler();
        this.imgAnim = findViewById(R.id.imgAnim);
        this.imgAnim2 = findViewById(R.id.imgAnim2);
        this.imgMiFoto = findViewById(R.id.imgMiFoto);
    }

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

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgMiFoto.setImageBitmap(selectedImage);
                File Photo_Signa = new File(Environment.getExternalStorageDirectory().toString()+ File.separator +DIRECTORY_PICTURES + "/Stockovin");

                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {

                            if(!Photo_Signa.exists()) {
                                try {
                                    Photo_Signa.mkdirs();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            File file = new File(Photo_Signa.getPath(), "Profile" + user.getId() +".jpg");
                            //if (!file.exists()) {
                            try {
                                FileOutputStream fos = new FileOutputStream(file);
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //}
                        } else {
                            requestPermission(); // Code for permission
                        }
                    } else {

                        File file = new File(Photo_Signa.getPath(), "Profile" + user.getId() +".jpg");
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

                Glide.with(this).load(selectedImage).apply(new RequestOptions().circleCrop()).into(imgMiFoto);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ProfileConfigActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileConfigActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(ProfileConfigActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ProfileConfigActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void userUpdate() {
        boolean majPassword = false;
        //first getting the values
        //On récupère le entrées
        Resources Res = getResources();

        EditTextUserName = (EditText) findViewById(R.id.EditTextUserName);
        EditTextUserEmail = (EditText) findViewById(R.id.EditTextViewEmail);
        EditTextUsercaveName = (EditText) findViewById(R.id.EditTextViewCaveName);
        EditTextUserNbBottleMax = (EditText) findViewById(R.id.EditTextViewNbBottleMax);
        EditTextViewPassword = (EditText) findViewById(R.id.EditTextViewPassword);
        EditTextViewConfirmPassword = (EditText) findViewById(R.id.EditTextViewConfirmPassword);
        CheckBoxViewPublic =  findViewById(R.id.CheckBoxViewPublic);

        final String name = EditTextUserName.getText().toString();
        final String email = EditTextUserEmail.getText().toString();
        final String caveName = EditTextUsercaveName.getText().toString();
        final int nbBottleMax = Integer.valueOf(EditTextUserNbBottleMax.getText().toString());
        final String password  = EditTextViewPassword.getText().toString();
        final String confirmPassword = EditTextViewConfirmPassword.getText().toString();
        final int userPublic;
        if(CheckBoxViewPublic.isChecked() == true) userPublic = 1;
        else userPublic = 0;

        if (TextUtils.isEmpty(name)) {
            EditTextUserName.setError(Res.getString(R.string.EnterNameUser));
            EditTextUserName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            EditTextUserEmail.setError(Res.getString(R.string.EnterEmail));
            EditTextUserEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(caveName)) {
            EditTextUsercaveName.setError(Res.getString(R.string.EnterCaveName));
            EditTextUsercaveName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Integer.toString(nbBottleMax))) {
            EditTextUserNbBottleMax.setError(Res.getString(R.string.EnterNbBottle));
            EditTextUserNbBottleMax.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
            EditTextViewConfirmPassword.setError(Res.getString(R.string.EnterPassword));
            EditTextViewConfirmPassword.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {

            if (password.length()<=7) {
                EditTextViewPassword.setError(Res.getString(R.string.lengthPassword));
                EditTextViewPassword.requestFocus();
                return;
            }

            if (password.length()<=7) {
                EditTextViewConfirmPassword.setError(Res.getString(R.string.lengthPassword));
                EditTextViewConfirmPassword.requestFocus();
                return;
            }

            if(password.equals(confirmPassword))majPassword = true;
            else {
                EditTextViewConfirmPassword.setError(Res.getString(R.string.difPassword));
                EditTextViewConfirmPassword.requestFocus();
                return;
            }
        }

        //if everything is fine
        //Si tout est OK
        boolean finalMajPassword = majPassword;
        class UserUpdate extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;
            Resources Res = getResources();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBarCatInsert);
                //progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //progressBar.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        majUser(caveName, email, name, nbBottleMax, userPublic);

                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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
                RequestHandler requestHandler = new RequestHandler();

                if(finalMajPassword == false ) {
                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", Integer.toString(user.getId()));
                    params.put("name", name);
                    params.put("email", email);
                    params.put("caveName", caveName);
                    params.put("nb_bottle_max", Integer.toString(nbBottleMax));
                    params.put("public", Integer.toString(userPublic));

                    //returing the response
                    return requestHandler.sendPostRequest(URLs.URL_UPDATEUSER, params);
                }
                else{
                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", Integer.toString(user.getId()));
                    params.put("name", name);
                    params.put("email", email);
                    params.put("caveName", caveName);
                    params.put("nb_bottle_max", Integer.toString(nbBottleMax));
                    params.put("password", password);
                    params.put("public", Integer.toString(userPublic));

                    //returing the response
                    return requestHandler.sendPostRequest(URLs.URL_UPDATEUSER_PW, params);
                }
            }
        }

        UserUpdate bi = new UserUpdate();
        bi.execute();
    }

    void majUser(String caveName, String email, String name, int nbBottleMax, int userPublic)
    {
        SharedPrefManager.getInstance(this).setCaveName(caveName);
        SharedPrefManager.getInstance(this).setEmail(email);
        SharedPrefManager.getInstance(this).setName(name);
        SharedPrefManager.getInstance(this).setNbBottleMax(nbBottleMax);
        SharedPrefManager.getInstance(this).setPublic(userPublic);
    }
}