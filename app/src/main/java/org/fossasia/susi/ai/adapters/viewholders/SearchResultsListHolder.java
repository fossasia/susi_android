package org.fossasia.susi.ai.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recycleradapters.SearchResultsAdapter;
import org.fossasia.susi.ai.adapters.recycleradapters.WebSearchAdapter;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.model.WebSearchModel;
import org.fossasia.susi.ai.rest.clients.WebSearchClient;
import org.fossasia.susi.ai.rest.responses.others.WebSearch;
import org.fossasia.susi.ai.rest.services.WebSearchService;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by saurabh on 19/11/16.
 */

public class SearchResultsListHolder extends MessageViewHolder {

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    private Realm realm;
    private Context currContext;
    private String webquery;

    public SearchResultsListHolder(View itemView, ClickListener listener,Realm realm,Context currContext) {
        super(itemView,listener);
        ButterKnife.bind(this, itemView);
        this.realm = realm;
        this.currContext = currContext;
    }

    public void setView(final ChatMessage model,boolean isClientSearch) {
        if(isClientSearch) {
            if(model!=null) {
                webquery = model.getWebquery();

                if(model.getWebSearchList()==null || model.getWebSearchList().size()==0) {
                    final WebSearchService apiService = WebSearchClient.getClient().create(WebSearchService.class);

                    Call<WebSearch> call = apiService.getresult(webquery);

                    call.enqueue(new Callback<WebSearch>() {
                        @Override
                        public void onResponse(Call<WebSearch> call, Response<WebSearch> response) {
                            Log.e(TAG, response.toString());
                            if (response.body() != null ) {
                                realm.beginTransaction();
                                RealmList<WebSearchModel> searchResults = new RealmList<>();

                                for(int i=0 ; i<response.body().getRelatedTopics().size() ; i++) {
                                    try {
                                        String url = response.body().getRelatedTopics().get(i).getUrl();
                                        String text = response.body().getRelatedTopics().get(i).getText();
                                        String iconUrl = response.body().getRelatedTopics().get(i).getIcon().getUrl();
                                        String htmlText = response.body().getRelatedTopics().get(i).getResult();
                                        Realm realm = Realm.getDefaultInstance();
                                        final WebSearchModel webSearch = realm.createObject(WebSearchModel.class);
                                        try {
                                            String[] tempStr = htmlText.split("\">");
                                            String[] tempStr2 = tempStr[tempStr.length-1].split("</a>");
                                            webSearch.setHeadline(tempStr2[0]);
                                            webSearch.setBody(tempStr2[tempStr2.length-1]);
                                        } catch (Exception e ) {
                                            webSearch.setBody(text);
                                            webSearch.setHeadline(webquery);
                                        }
                                        webSearch.setImageURL(iconUrl);
                                        webSearch.setUrl(url);
                                        searchResults.add(webSearch);
                                    } catch (Exception e) {
                                        Log.v(TAG,e.getLocalizedMessage());
                                    }
                                }
                                if(searchResults.size()==0) {
                                    Realm realm = Realm.getDefaultInstance();
                                    final WebSearchModel webSearch = realm.createObject(WebSearchModel.class);
                                    webSearch.setBody(null);
                                    webSearch.setHeadline("No Results Found");
                                    webSearch.setImageURL(null);
                                    webSearch.setUrl(null);
                                    searchResults.add(webSearch);
                                }
                                LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                                        LinearLayoutManager.HORIZONTAL, false);
                                recyclerView.setLayoutManager(layoutManager);
                                WebSearchAdapter resultsAdapter = new WebSearchAdapter(currContext, searchResults);
                                recyclerView.setAdapter(resultsAdapter);
                                model.setWebSearchList(searchResults);
                                realm.copyToRealmOrUpdate(model);
                                realm.commitTransaction();
                            } else {
                                recyclerView.setAdapter(null);
                                recyclerView.setLayoutManager(null);
                            }
                        }

                        @Override
                        public void onFailure(Call<WebSearch> call, Throwable t) {
                            Log.e(TAG, "error" + t.toString());
                        }
                    });
                } else {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                            LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    WebSearchAdapter resultsAdapter = new WebSearchAdapter(currContext, model.getWebSearchList());
                    recyclerView.setAdapter(resultsAdapter);
                }

            } else{
                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(null);
            }
        } else {
            if (model != null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                        LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
                SearchResultsAdapter resultsAdapter = new SearchResultsAdapter(currContext, model.getDatumRealmList());
                recyclerView.setAdapter(resultsAdapter);
            } else {
                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(null);
            }
        }
    }
}
