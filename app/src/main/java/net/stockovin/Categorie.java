package net.stockovin;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class Categorie implements Parcelable {

    private int id, iduser;
    private String name;
    private int iconImage;

    public Categorie(int id, int iduser, String name) {
        this.id = id;
        this.iduser = iduser;
        this.name = name;;
    }

    protected Categorie(Parcel in) {
        id = in.readInt();
        iduser = in.readInt();
        name = in.readString();
        iconImage = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(iduser);
        dest.writeString(name);
        dest.writeInt(iconImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Categorie> CREATOR = new Creator<Categorie>() {
        @Override
        public Categorie createFromParcel(Parcel in) {
            return new Categorie(in);
        }

        @Override
        public Categorie[] newArray(int size) {
            return new Categorie[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getIdUser() {
        return iduser;
    }

    public String getName() {
        return name;
    }

}