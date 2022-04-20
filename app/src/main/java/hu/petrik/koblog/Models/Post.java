package hu.petrik.koblog.Models;

public class Post {
    private int id,likes=0,comments;
    private String date,desc,photo;
    private User user;
    private boolean selfLike;

    //get függvények
    public int getId() {
        return id;
    }
    public int getLikes() {
        return likes;
    }
    public int getComments() {
        return comments;
    }
    public String getDate() {
        return date;
    }
    public String getDesc() {
        return desc;
    }
    public String getPhoto() {
        return photo;
    }
    public User getUser() {
        return user;
    }

    //set függvények
    public void setId(int id) {
        this.id = id;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }
    public void setComments(int comments) {
        this.comments = comments;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setUser(User user) {
        this.user = user;
    }


    public boolean isSelfLike() {
        return selfLike;
    }
    public void setSelfLike(boolean selfLike) {
        this.selfLike = selfLike;
    }
}
