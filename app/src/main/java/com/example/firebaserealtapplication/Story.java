package com.example.firebaserealtapplication;

public class Story {
    private String title;
    private String content;
    private String image;
    private String key;
    private String id;
    private String vdo_url;



    public  Story() {

    }

    public Story(String id,String title, String content, String myImg,String vdo_url) {
        this.title = title;
        this.content = content;
        this.image = myImg;
        this.id=id;
        this.vdo_url=vdo_url;


    }
    public String getKey(){
        return key;
    }
    public void setKey(String key){
        this.key=key;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }
    public String getId(){
        return id;
    }
    public void setIds(String id){
        this.id=id;
    }
    public String getVdo_url(){
        return vdo_url;
    }
    public void setVdo_url(String vdo_url) {
        this.vdo_url = vdo_url;
    }

}

