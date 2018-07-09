package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.dataclasses.SkillRatingQuery;
import org.fossasia.susi.ai.dataclasses.SkillsListQuery;
import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor;
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse;
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse;
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse;
import org.fossasia.susi.ai.rest.services.SusiService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saurabh on 1/10/16.
 * <p>
 * Singleton class to get Susi client.
 */
public class ClientBuilder {

    private static Retrofit retrofit;
    private static SusiService susiService;

    /**
     * Instantiates a new Client builder.
     */
    public ClientBuilder() {
        createSusiService();
    }

    private static void init() {
        susiService = createApi(SusiService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    /**
     * Create susi service.
     */
    public static void createSusiService() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //Must maintain the order of interceptors here, logging needs to be last.
        httpClient.addInterceptor(new TokenInterceptor());
        httpClient.addInterceptor(logging);

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(PrefManager.getSusiRunningBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
            init();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets susi api.
     *
     * @return the susi api
     */
    public SusiService getSusiApi() {
        return susiService;
    }

    public static Call<GetSkillFeedbackResponse> fetchFeedbackCall(FetchFeedbackQuery queryObject){
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("model", queryObject.getModel());
        queryMap.put("group", queryObject.getGroup());
        queryMap.put("language", queryObject.getLanguage());
        queryMap.put("skill", queryObject.getSkill());
        return susiService.fetchFeedback(queryMap);
    }

    public static Call<SkillRatingResponse> rateSkillCall(SkillRatingQuery queryObject) {
        Map<String, String> queryMap= new HashMap<String, String>();
        queryMap.put("model", queryObject.getModel());
        queryMap.put("group", queryObject.getGroup());
        queryMap.put("language", queryObject.getLanguage());
        queryMap.put("skill", queryObject.getSkill());
        queryMap.put("rating", queryObject.getRating());
        return susiService.rateSkill(queryMap);
    }

    public static Call<ListSkillsResponse> fetchListSkillsCall(SkillsListQuery queryObject) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("group", queryObject.getGroup());
        queryMap.put("language", queryObject.getLanguage());
        queryMap.put("applyFilter", queryObject.getApplyFilter());
        queryMap.put("filter_name", queryObject.getFilterName());
        queryMap.put("filter_type", queryObject.getFilterType());
        return susiService.fetchListSkills(queryMap);
    }
}