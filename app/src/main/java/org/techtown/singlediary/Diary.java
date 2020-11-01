package org.techtown.singlediary;

import android.graphics.Bitmap;

public class Diary {
    String content;
    String date;
    String address;
    int condition;
    int weather;
    Bitmap photo;

    public Diary(String content, String date, String address, int condition, int weather, Bitmap photo) {
        this.content = content;
        this.date = date;
        this.address = address;
        this.condition = condition;
        this.weather = weather;
        this.photo = photo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
