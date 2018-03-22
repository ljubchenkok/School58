package ru.com.penza.school58.web;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.com.penza.school58.datamodel.Message;


/**
 * Created by Константин on 22.03.2018.
 */

public interface SOService {
    @FormUrlEncoded
    @POST("/ajax/")
    Call<Message> getAnswer( @Field("card") String card, @Field("act") String act);
}
