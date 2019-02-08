package com.kalakaar.admin.danceapp;

public class ApplicationModel {

    String id,name,heading,address,mobile,email,gender,state,city;

    public ApplicationModel(String id, String name, String heading) {
        this.id = id;
        this.name = name;
        this.heading = heading;
    }

    public ApplicationModel(String id, String name, String heading, String address, String mobile, String email, String gender, String state, String city) {
        this.id = id;
        this.name = name;
        this.heading = heading;
        this.address = address;
        this.mobile = mobile;
        this.email = email;
        this.gender = gender;
        this.state = state;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
