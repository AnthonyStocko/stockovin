package net.stockovin;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.UUID;

//here for this class we are using a singleton pattern
//Ici pour cette classe nous utilisons une singleton pattern

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_GENDER = "keygender";
    private static final String KEY_ID = "keyid";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_CAVENAME = "keycavename";
    private static final String KEY_NBBOTTLEMAX = "keynbbottlemax";
    private static final String KEY_PUBLIC = "keypublic";

    private static final String SHARED_PREF_NAME_FCM = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static String uniqueID = null;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //Méthode pour laisser l'utilisateur loggué
    //this method will store the user data in shared preferences
    //Cette méthode stockera les données de l'utilisateur dans les préférence paratagées
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_GENDER, user.getGender());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_CAVENAME, user.getcaveName());
        editor.putInt(KEY_NBBOTTLEMAX, user.getNbBottleMax());
        editor.putInt(KEY_PUBLIC, user.getPublic());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    //Cette méthode vérifie que l'utilisateur est déjà connecté
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //this method will give the logged in user
    //Cette méthode donne les info dans l'objet user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_GENDER, null),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_CAVENAME, null),
                sharedPreferences.getInt(KEY_NBBOTTLEMAX, 100),
                sharedPreferences.getInt(KEY_PUBLIC, 0)
        );
    }

    public void setPublic(int userPublic) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PUBLIC, userPublic);
        editor.apply();
    }

    public void setCaveName(String caveName) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CAVENAME, caveName);
        editor.apply();
    }


    public void setNbBottleMax(int nbBottleMax) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NBBOTTLEMAX, nbBottleMax);
        editor.apply();
    }

    public void setEmail(String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void setName(String name) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    //this method will logout the user
    //Cette méthode déconnecte le user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, MainActivity.class));
    }

    public void logout_close() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        System.exit(0);
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME_FCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME_FCM, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, null);
       // uniqueID = mCtx.getSharedPreferences("_", Context.MODE_PRIVATE).getString("fcm_token", "empty");
        //return  sharedPreferences.getString(TAG_TOKEN, null);

        /*if (uniqueID == null) {
            SharedPreferences sharedPrefs = mCtx.getSharedPreferences(
                    SHARED_PREF_NAME, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(SHARED_PREF_NAME, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(SHARED_PREF_NAME, uniqueID);
                editor.commit();
            }
        }*/
        //return uniqueID;
    }
}
