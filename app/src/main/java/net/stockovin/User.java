package net.stockovin;


public class User {

    private int id, nbBottleMax, userPublic;
    private String username, email, gender, name, caveName;

    public User(int id, String username, String email, String gender, String name, String caveName, int nbBottleMax, int userPublic) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.caveName = caveName;
        this.nbBottleMax = nbBottleMax;
        this.userPublic = userPublic;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getcaveName () {
        return caveName;
    }

    public void setCaveName (String cave_Name) {
        this.caveName = cave_Name;
    }

    public int getNbBottleMax () {
        return nbBottleMax;
    }

    public void setNbBottleMax (int nbBottleMax) {
        this.nbBottleMax = nbBottleMax;
    }

    public int getPublic () { return userPublic;  }
}