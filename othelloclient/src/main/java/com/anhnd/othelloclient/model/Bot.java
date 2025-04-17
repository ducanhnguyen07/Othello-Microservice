package com.anhnd.othelloclient.model;


public class Bot {
    private int id;
    private String urlModel;

    public Bot() {
    }

    public Bot(int id, String urlModel) {
        this.id = id;
        this.urlModel = urlModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlModel() {
        return urlModel;
    }

    public void setUrlModel(String urlModel) {
        this.urlModel = urlModel;
    }
}
