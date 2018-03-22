package ru.com.penza.school58.web;

/**
 * Created by Константин on 22.03.2018.
 */

public class ApiUtils {
    public static final String BASE_URL = "http://xn--58-6kc3bfr2e.xn--p1ai";

    public static SOService getSOService() {
        return RetrofitClient.getClient(BASE_URL).create(SOService.class);
    }



}
