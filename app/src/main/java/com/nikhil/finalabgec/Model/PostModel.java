package com.nikhil.finalabgec.Model;

public class PostModel {

    String name;
    String title;
    String description;
    String uid;
    String pushkey;
    String date;
    String image_link;
    String link;
    String like;
    String dp_link;

    public PostModel() {
    }

    public PostModel(String name, String title, String description, String uid, String pushkey, String date, String image_link, String link, String like, String dp_link) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.uid = uid;
        this.pushkey = pushkey;
        this.date = date;
        this.image_link = image_link;
        this.link = link;
        this.like = like;
        this.dp_link = dp_link;
    }

    public String getDp_link() {
        return dp_link;
    }

    public String getLink() {
        return link;
    }

    public String getLike() {
        return like;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUid() {
        return uid;
    }

    public String getPushkey() {
        return pushkey;
    }

    public String getDate() {
        return date;
    }

    public String getImage_link() {
        return image_link;
    }
}
