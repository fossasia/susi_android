package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SusiResponse {
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("query_date")
    @Expose
    private String queryDate;
    @SerializedName("answer_date")
    @Expose
    private String answerDate;
    @SerializedName("answer_time")
    @Expose
    private Integer answerTime;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("answers")
    @Expose
    private List<Answer> answers = new ArrayList<>();
    @SerializedName("session")
    @Expose
    private Session session;

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
