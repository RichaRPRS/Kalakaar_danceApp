package com.kalakaar.admin.danceapp;

import android.graphics.Bitmap;

import java.io.Serializable;

public class DataModel implements Serializable {

    /*String name;
    String type;
    String version_number;
    String feature;*/

    String heading,description,venue,contact,category,subcateg,city,state,country,img_path,category_id,provider_name,event_Id,prov_Id,enddate,approve,reason;

    public DataModel(String heading, String description,String contact, String category, String subcateg, String city, String state,String img_path,String approve,String reason) {
        this.heading = heading;
        this.description=description;
        this.contact = contact;
        this.category = category;
        this.subcateg = subcateg;
        this.city = city;
        this.state = state;
        this.img_path=img_path;
        this.approve=approve;
        this.reason=reason;
    }

    public DataModel(String heading, String description, String venue,String contact, String category, String subcateg, String city, String state, String country, String img_path, String category_id, String provider_name, String event_Id, String prov_Id, String enddate) {
        this.heading = heading;
        this.description = description;
        this.venue=venue;
        this.contact = contact;
        this.category = category;
        this.subcateg = subcateg;
        this.city = city;
        this.state = state;
        this.country=country;
        this.img_path = img_path;
        this.category_id = category_id;
        this.provider_name = provider_name;
        this.event_Id = event_Id;
        this.prov_Id = prov_Id;
        this.enddate = enddate;
    }

    public DataModel(String heading, String description,String venue, String contact, String category, String subcateg, String city, String state, String country,String img_path, String category_id, String provider_name, String event_Id, String prov_Id, String enddate, String approve,String reason) {
        this.heading = heading;
        this.description = description;
        this.venue=venue;
        this.contact = contact;
        this.category = category;
        this.subcateg = subcateg;
        this.city = city;
        this.state = state;
        this.country=country;
        this.img_path = img_path;
        this.category_id = category_id;
        this.provider_name = provider_name;
        this.event_Id = event_Id;
        this.prov_Id = prov_Id;
        this.enddate = enddate;
        this.approve = approve;
        this.reason=reason;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcateg() {
        return subcateg;
    }

    public void setSubcateg(String subcateg) {
        this.subcateg = subcateg;
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

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getEvent_Id() {
        return event_Id;
    }

    public void setEvent_Id(String event_Id) {
        this.event_Id = event_Id;
    }

    public String getProv_Id() {
        return prov_Id;
    }

    public void setProv_Id(String prov_Id) {
        this.prov_Id = prov_Id;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
