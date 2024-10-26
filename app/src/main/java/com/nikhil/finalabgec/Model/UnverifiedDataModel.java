package com.nikhil.finalabgec.Model;

public class UnverifiedDataModel {
    private String uid;
    private String name;
    private String branch;
    private String batch;
    private String email;
    private boolean isAdmin = false;

    public UnverifiedDataModel(){

    }

    public UnverifiedDataModel(String uid, String name, String branch, String batch, String email, boolean isAdmin){
        this.name = name;
        this.branch = branch;
        this.batch = batch;
        this.email = email;
        this.uid = uid;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public String getBatch() {
        return batch;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public boolean getisAdmin(){
        return isAdmin;
    }

}
