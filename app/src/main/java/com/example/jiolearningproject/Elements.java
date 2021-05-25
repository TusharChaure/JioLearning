package com.example.jiolearningproject;

import java.util.ArrayList;

public class Elements {

    public final String DEFAULT_MEDIA_URI = "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv";
    public ArrayList<String> Aduri = new ArrayList<>();
    public ArrayList<String> Adid = new ArrayList<>();
    public ArrayList<Integer> timeOffset = new ArrayList<>();
    public ArrayList<String> temp = new ArrayList<>();

    void URI(String s){
        Aduri.add(s);
    }

    void ID(String s){
        Adid.add(s);
    }

    void time(int s){
        timeOffset.add(s);
    }

    void tem(String s){
        temp.add(s);
    }

    String get_URI(int a){
        return Aduri.get(a);
    }

    String get_ID(int a){
        return Adid.get(a);
    }

    int get_time(int a){
        return timeOffset.get(a);
    }

    String get_tem(int a){
       return temp.get(a);
    }

    int size_get_URI(){
        return Aduri.size();
    }

    int size_get_ID(){
        return Adid.size();
    }

    int size_get_time(){
        return timeOffset.size();
    }

    int size_get_tem(){
        return temp.size();
    }


}
