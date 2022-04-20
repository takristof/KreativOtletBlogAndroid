package hu.petrik.koblog.Models;

public class User {
    private int id;
    private String userName,photo;

    //get függvények
    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
    public String getPhoto() {
        return photo;
    }

    //set függvények
    public void setId(int id) {
        this.id = id;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
