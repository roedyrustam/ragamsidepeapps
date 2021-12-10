package com.sidepe.multicontent.models;

public class AboutModel {
    String aboutImage;
    String aboutTitle;
    String aboutSubTitle;

    public AboutModel(String aboutImage, String aboutTitle, String aboutSubTitle) {
        this.aboutImage = aboutImage;
        this.aboutTitle = aboutTitle;
        this.aboutSubTitle = aboutSubTitle;
    }

    public String getAboutImage() {
        return aboutImage;
    }

    public void setAboutImage(String aboutImage) {
        this.aboutImage = aboutImage;
    }

    public String getAboutTitle() {
        return aboutTitle;
    }

    public void setAboutTitle(String aboutTitle) {
        this.aboutTitle = aboutTitle;
    }

    public String getAboutSubTitle() {
        return aboutSubTitle;
    }

    public void setAboutSubTitle(String aboutSubTitle) {
        this.aboutSubTitle = aboutSubTitle;
    }
}
