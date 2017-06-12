package com.example.marni.orderapp.domain;

import java.io.Serializable;

public class Allergy implements Serializable {
    private String imageUrl;
    private String informationText;

    public Allergy(String imageUrl, String informationText){
        this.imageUrl = imageUrl;
        this.informationText = informationText;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getInformationText(){
        return informationText;
    }

    public void setImage(String imageid){
        this.imageUrl = imageid;
    }

    public void setInformationText(String informationText){
        this.informationText = informationText;
    }

}
