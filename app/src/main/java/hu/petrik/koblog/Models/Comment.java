package hu.petrik.koblog.Models;

public class Comment {
    private int id;
    private String comment,date;
    private User user;

    //Üres comment class
    public Comment() {
    }

    //get függvények
    public int getId() {
        return id;
    }
    public String getComment() {
        return comment;
    }
    public String getDate() {
        return date;
    }
    public User getUser() {
        return user;
    }

    //set függvények
    public void setId(int id) {
        this.id = id;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
