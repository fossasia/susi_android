package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SusiResponse {
    @SerializedName("client_id")
    @Expose
    String clientId;
    @SerializedName("query")
    @Expose
    String query;
    @SerializedName("query_date")
    @Expose
    String queryDate;
    @SerializedName("answer_date")
    @Expose
    String answerDate;
    @SerializedName("answer_time")
    @Expose
    Integer answerTime;
    @SerializedName("count")
    @Expose
    Integer count;
    @SerializedName("answers")
    @Expose
    List<Answer> answers = new ArrayList<Answer>();
    @SerializedName("session")
    @Expose
    Session session;

    public String getClientId() {
        return clientId;
    }

    public String getQuery() {
        return query;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public Integer getAnswerTime() {
        return answerTime;
    }

    public Integer getCount() {
        return count;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Session getSession() {
        return session;
    }
}
