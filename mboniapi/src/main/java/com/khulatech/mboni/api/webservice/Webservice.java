package com.khulatech.mboni.api.webservice;

import com.khulatech.mboni.api.utils.CodeMBoniResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Joane SETANGNI on 09/06/2016.
 */
public interface Webservice {

    String BASE_URL= "http://api.mboni.net/data/";
//    String BASE_URL= "http://192.168.1.22:8888/khulatech/mboni/data/";
    String BASE_URL_STATIC_MAP_IMG = BASE_URL + "/getLocationStaticMapImg?";



    //recherche d'une localisation par codeUnique
    @FormUrlEncoded
    @POST("getCodeInfo")
    Call<CodeMBoniResult> getCodeInfo(
            @Field("apikey") String apikey,
            @Field("UniqueCode") String codeUnique
    );


}
