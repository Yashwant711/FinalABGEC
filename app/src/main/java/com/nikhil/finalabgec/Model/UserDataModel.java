package com.nikhil.finalabgec.Model;

public class UserDataModel {

    private String uid;
    private String email;
    private String name;
    private String batch;
    private String branch;
    private boolean isAdmin;

    private String bio;
    private String city;
    private String state;
    private String country;
    private String designation;
    private String organization;
    private String phone;

    private String dob;
    private String gender;
    private String dp_link;
    private String fb;
    private String insta;
    private String linkedin;
    private String twitter;

    private String token;
    private boolean contact_visibility = false;

    public UserDataModel() {
    }

    public UserDataModel(String uid, String email, String name, String batch, String branch, boolean isAdmin, String bio, String city, String state, String country, String designation, String organization, String phone, String dob, String gender, String dp_link, String fb, String insta, String linkedin, String twitter, String token, boolean contact_visibility) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.batch = batch;
        this.branch = branch;
        this.isAdmin = isAdmin;
        this.bio = bio;
        this.city = city;
        this.state = state;
        this.country = country;
        this.designation = designation;
        this.organization = organization;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.dp_link = dp_link;
        this.fb = fb;
        this.insta = insta;
        this.linkedin = linkedin;
        this.twitter = twitter;
        this.token = token;
        this.contact_visibility = contact_visibility;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setisAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDp_link() {
        return dp_link;
    }

    public void setDp_link(String dp_link) {
        this.dp_link = dp_link;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isContact_visibility() {
        return contact_visibility;
    }

    public void setContact_visibility(boolean contact_visibility) {
        this.contact_visibility = contact_visibility;
    }

}
