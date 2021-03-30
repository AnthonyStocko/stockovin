package net.stockovin;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BottleSuggest {

    private int id, qte, idcategorie, iduser, year, note, price, id_user_friend;
    private String name, domain, type, comment, container, garde, provider, purchaseDate;

    public BottleSuggest(int id, String name, String domain, int qte, int idcategorie, int iduser, String type, int year,
                  String comment, int note, String container, String garde, String provider, int price, String purchaseDate, int id_user_friend) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.qte = qte;
        this.idcategorie = idcategorie;
        this.iduser = iduser;
        this.type = type;
        this.year = year;
        this.comment = comment;
        this.note = note;
        this.container = container;
        this.garde = garde;
        this.provider = provider;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.id_user_friend = id_user_friend;
    }

    public int getId() {
        return id;
    }

    public int getIdUser() {
        return iduser;
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    public int getQte() {
        return qte;
    }

    public int getIdcategorie() {
        return idcategorie;
    }

    public int getIduser() {
        return iduser;
    }

    public int getIdUserFriend() {
        return id_user_friend;
    }

    public String getType() {
        return type;
    }

    public int getYear() {
        return year;
    }

    public String getComment() {
        return comment;
    }

    public String getContainer() {
        return container;
    }

    public int getNote() {
        return note;
    }

    public String getGarde() {
        return garde;
    }

    public String getProvider() {
        return provider;
    }

    public int getPrice() {
        return price;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void DeleteBottleSuggest(int p_idBottle, String NameBot, int iduser, ProgressBar progressBar, Context context, String msg) {

        final int idBottle = p_idBottle;
        final String nameBot = NameBot;

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
                        Toast.makeText(context,  msg, Toast.LENGTH_SHORT).show();
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
                params.put("iduser", Integer.toString(iduser));


                //returing the response
                //retour de la réponse
                return requestHandler.sendPostRequest(URLs.URL_DELETEBOT_SUGGEST, params);
            }
        }

        DeleteListeBottle alc = new DeleteListeBottle();
        alc.execute();

    }

}
