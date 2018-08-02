package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.dataclasses.SkillMetricsDataQuery;
import org.fossasia.susi.ai.dataclasses.SkillRatingQuery;
import org.fossasia.susi.ai.dataclasses.SkillsListQuery;
import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor;
import org.fossasia.susi.ai.rest.responses.susi.ListSkillMetricsResponse;
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
import timber.log.Timber;

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

    /**
     * Create susi service.
     */
    public static void createSusiService() {

        try {
            retrofit = RetrofitInstance.getRetrofit();
            susiService = getSusiApi();
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
    }

    /**
     * Gets susi api.
     *
     * @return the susi api
     */
    public static SusiService getSusiApi() {
        if (susiService == null) {
            if (retrofit == null)
                retrofit = RetrofitInstance.getRetrofit();
            susiService = retrofit.create(SusiService.class);
        }
        return susiService;
    }

    public static Call<GetSkillFeedbackResponse> fetchFeedbackCall(FetchFeedbackQuery queryObject) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("model", queryObject.getModel());
        queryMap.put("group", queryObject.getGroup());
        queryMap.put("language", queryObject.getLanguage());
        queryMap.put("skill", queryObject.getSkill());
        return getSusiApi().fetchFeedback(queryMap);
    }

    public static Call<SkillRatingResponse> rateSkillCall(SkillRatingQuery queryObject) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("model", queryObject.getModel());
        queryMap.put("group", queryObject.getGroup());
        queryMap.put("language", queryObject.getLanguage());
        queryMap.put("skill", queryObject.getSkill());
        queryMap.put("rating", queryObject.getRating());
        return getSusiApi().rateSkill(queryMap);
    }

    public static Call<ListSkillsResponse> fetchListSkillsCall(SkillsListQuery queryObject) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("group", queryObject.getGroup());
        queryMap.put("language", queryObject.getLanguage());
        queryMap.put("applyFilter", queryObject.getApplyFilter());
        queryMap.put("filter_name", queryObject.getFilterName());
        queryMap.put("filter_type", queryObject.getFilterType());
        return getSusiApi().fetchListSkills(queryMap);
    }
}