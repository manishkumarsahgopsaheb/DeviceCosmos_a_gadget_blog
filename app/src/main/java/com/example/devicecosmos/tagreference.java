package com.example.devicecosmos;

public class tagreference {
    String photo;
    String heading;
    String time;
    String date;
    String count;
    String tag;

    public tagreference()
    {

    }

    public tagreference(String photo, String heading, String time, String date, String count, String tag) {
        this.photo = photo;
        this.heading = heading;
        this.time = time;
        this.date = date;
        this.count = count;
        this.tag = tag;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
