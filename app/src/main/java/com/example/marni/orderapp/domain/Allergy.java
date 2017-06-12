package com.example.marni.orderapp.domain;

public class Allergy {
    private String imageUrl;
    private String informationtext;

    public Allergy(String imageUrl, String informationtext){
        this.imageUrl = imageUrl;
        this.informationtext = informationtext;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getInformationtext(){
        return informationtext;
    }

    public void setImage(String imageid){
        this.imageUrl = imageid;
    }

    public void setInformationtext(String informationtext){
        this.informationtext = informationtext;
    }

}
