package com.example.marni.orderapp.Presentation;

/**
 * Created by Wallaard on 4-5-2017.
 */

public class Allergy {
    private String imageid;
    private String informationtext;

            public Allergy(String imageid, String informationtext){
                this.imageid = imageid;
                this.informationtext = informationtext;
            }

            public String getImageid(){
                return imageid;
            }

            public String getInformationtext(){
                return informationtext;
            }

            public void setImage(String imageid){
                this.imageid = imageid;
            }

            public void setInformationtext(String informationtext){
                this.informationtext = informationtext;
            }


}
