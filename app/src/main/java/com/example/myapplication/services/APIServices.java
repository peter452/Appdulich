package com.example.myapplication.services;

public class APIServices {
    private static String baseurl="http://192.168.1.2:8080/ApiAppDuLichLaravel/public/";

    public static DataService getService(){

        return APIRetrofitClient.getClient(baseurl).create(DataService.class);
    }
}
