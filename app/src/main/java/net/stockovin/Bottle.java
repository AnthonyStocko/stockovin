package net.stockovin;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Bottle {

    private int id, qte, idcategorie, iduser, year, note, price;
    private String name, domain, type, comment, container, garde, provider, purchaseDate;

    public Bottle(int id, String name, String domain, int qte, int idcategorie, int iduser, String type, int year,
                  String comment, int note, String container, String garde, String provider, int price, String purchaseDate) {
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
    }

    public int getId() {
        return id;
    }
    public void setId(int p_id) {
        id = p_id;
    }

    public String getName() {
        return name;
    }
    public void setName(String p_name) {
        Log.d("Bottle","setName " + p_name);
        name = p_name;
    }

    public String getDomain() {
        return domain;
    }
    public void setDomain(String p_domain) {
        domain = p_domain;
    }

    public int getQte() {
        return qte;
    }
    public void setQte(int p_qte) {
        qte = p_qte;
    }

    public int getIdcategorie() {
        return idcategorie;
    }
    public void setIdcategorie(int p_idcategorie) {
        idcategorie = p_idcategorie;
    }

    public int getIdUser() {
        return iduser;
    }
    public void setIdUser(int p_idUser) {
        iduser = p_idUser;
    }

    public String getType() {
        return type;
    }
    public void setType(String p_type) {
        type = p_type;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int p_year) {
        year = p_year;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String p_Comment) {
        comment = p_Comment;
    }

    public String getContainer() {
        return container;
    }
    public void setContainer(String p_container) {
        container = p_container;
    }

    public int getNote() {
        return note;
    }
    public void setNote(int p_note) {
        note = p_note;
    }

    public String getGarde() {
        return garde;
    }
    public void setGarde(String p_garde) {
        garde = p_garde;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String p_provider) {
        provider = p_provider;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int p_price) {
        price = p_price;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(String p_purchaseDate) {
        purchaseDate = p_purchaseDate;
    }

    public void DeleteBottle(int p_idBottle, String NameBot, int iduser, ProgressBar progressBar, Context context, String msg) {

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
                return requestHandler.sendPostRequest(URLs.URL_DELETEBOT, params);
            }
        }

        DeleteListeBottle alc = new DeleteListeBottle();
        alc.execute();

    }

}
