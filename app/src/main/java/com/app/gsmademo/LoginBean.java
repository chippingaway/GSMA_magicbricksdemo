package com.app.gsmademo;

import java.util.ArrayList;

/**
 * Created by anil on 14/7/16.
 */
public class LoginBean  {
    private String clientId;
    private String clientSecret;
    private String servingOperator;
    private String country;

    public ArrayList<UrlBean> getLinkArray() {
        return linkArray;
    }

    public void setLinkArray(ArrayList<UrlBean> linkArray) {
        this.linkArray = linkArray;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getServingOperator() {
        return servingOperator;
    }

    public void setServingOperator(String servingOperator) {
        this.servingOperator = servingOperator;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private ArrayList<UrlBean> linkArray;


}
