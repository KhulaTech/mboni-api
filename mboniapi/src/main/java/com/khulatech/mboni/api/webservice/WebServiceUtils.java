package com.khulatech.mboni.api.webservice;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Joane SETANGNI on 09/06/2016.
 */
public class WebServiceUtils {

    /**
     * Renvoie l'objet Webservice necessaire pour faire les appel avec Retrofit
     * @return Webservice
     */
    public static Webservice getWebserviceManager() {
        Converter.Factory factory;

//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(5, TimeUnit.MINUTES)
//                .readTimeout(5, TimeUnit.MINUTES)
//                .build();

        factory = GsonConverterFactory.create();

        Retrofit retrofit = new Retrofit.Builder()
//                .client(client)
                .baseUrl(Webservice.BASE_URL)
                .addConverterFactory(factory)
                .build();
        return retrofit.create(Webservice.class);
    }

}
