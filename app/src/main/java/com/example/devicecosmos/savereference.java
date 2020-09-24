package com.example.devicecosmos;

public class savereference {
    String heading;
    String photo;

    public savereference(){

    }

    public savereference(String heading, String photo) {
        this.heading = heading;
        this.photo = photo;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}


