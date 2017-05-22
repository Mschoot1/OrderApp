package com.example.marni.orderapp.Domain;

public class Allergy {
    private String image_url;
    private String informationtext;

    public Allergy(String image_url, String informationtext){
        this.image_url = image_url;
        this.informationtext = informationtext;
    }

    public String getImage_url(){
        return image_url;
    }

    public String getInformationtext(){
        return informationtext;
    }

    public void setImage(String imageid){
        this.image_url = imageid;
    }

    public void setInformationtext(String informationtext){
        this.informationtext = informationtext;
    }

}
