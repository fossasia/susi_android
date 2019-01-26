package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.recycleradapters.SearchResultsAdapter
import org.fossasia.susi.ai.chat.adapters.recycleradapters.WebSearchAdapter
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.data.model.WebSearchModel
import org.fossasia.susi.ai.rest.clients.WebSearchClient
import org.fossasia.susi.ai.rest.responses.others.WebSearch
import org.fossasia.susi.ai.rest.services.WebSearchService

import io.realm.Realm
import io.realm.RealmList
import kotterknife.bindView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SearchResultsListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)
    private var webquery: String? = null
    private val realm: Realm = Realm.getDefaultInstance()

    /**
     * Inflate search_list
     *
     * @param model the ChatMessage object
     * @param isClientSearch the boolean value to find client search type
     * @param currContext the Context
     */
    fun setView(model: ChatMessage?, isClientSearch: Boolean, currContext: Context) {
        if (isClientSearch) {
            if (model != null) {
                webquery = model.webquery
                val webSearchList = model.webSearchList
                if (webSearchList == null || webSearchList.size == 0) {
                    val apiService = WebSearchClient.retrofit.create(WebSearchService::class.java)
                    val call = webquery?.let { apiService.getResult(it) }
                    call?.enqueue(object : Callback<WebSearch> {
                        override fun onResponse(call: Call<WebSearch>, response: Response<WebSearch>) {
                            Timber.e(response.toString())
                            if (response.body() != null) {
                                realm.beginTransaction()
                                val searchResults = RealmList<WebSearchModel>()
                                val webSearchResponse = response.body()
                                val relatedTopics = webSearchResponse?.relatedTopics
                                if (relatedTopics != null) {
                                    for (relatedTopic in relatedTopics) {
                                        try {
                                            val url = relatedTopic.url
                                            val text = relatedTopic.text
                                            val iconUrl = relatedTopic.icon?.url
                                            val htmlText = relatedTopic.result
                                            val realm = Realm.getDefaultInstance()
                                            val webSearch = realm.createObject(WebSearchModel::class.java)
                                            try {
                                                val tempStr = htmlText?.split("\">".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                                val tempStr2 = tempStr?.get(tempStr.size - 1)?.split("</a>".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                                webSearch.headline = tempStr2?.get(0)
                                                webSearch.body = tempStr2?.get(tempStr2.size - 1)
                                            } catch (e: Exception) {
                                                webSearch.body = text
                                                webSearch.headline = webquery
                                                Timber.e(e)
                                            }

                                            webSearch.imageURL = iconUrl
                                            webSearch.url = url
                                            searchResults.add(webSearch)
                                        } catch (e: Exception) {
                                            Timber.e(e)
                                        }
                                    }
                                }

                                if (searchResults.size == 0) {
                                    val realm = Realm.getDefaultInstance()
                                    val webSearch = realm.createObject(WebSearchModel::class.java)
                                    webSearch.body = null
                                    webSearch.headline = "No Results Found"
                                    webSearch.imageURL = null
                                    webSearch.url = null
                                    searchResults.add(webSearch)
                                }
                                val layoutManager = LinearLayoutManager(currContext,
                                        LinearLayoutManager.HORIZONTAL, false)
                                recyclerView.layoutManager = layoutManager
                                val resultsAdapter = WebSearchAdapter(currContext, searchResults)
                                recyclerView.adapter = resultsAdapter
                                model.webSearchList = searchResults
                                realm.copyToRealmOrUpdate(model)
                                realm.commitTransaction()
                            } else {
                                recyclerView.adapter = null
                                recyclerView.layoutManager = null
                            }
                        }

                        override fun onFailure(call: Call<WebSearch>, t: Throwable) {
                            Timber.e("error %s", t.toString())
                        }
                    })
                } else {
                    val layoutManager = LinearLayoutManager(currContext,
                            LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.layoutManager = layoutManager
                    val resultsAdapter = WebSearchAdapter(currContext, webSearchList)
                    recyclerView.adapter = resultsAdapter
                }
            } else {
                recyclerView.adapter = null
                recyclerView.layoutManager = null
            }
        } else {
            if (model != null) {
                val layoutManager = LinearLayoutManager(currContext,
                        LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = layoutManager
                val resultsAdapter = SearchResultsAdapter(currContext, model.datumRealmList)
                recyclerView.adapter = resultsAdapter
            } else {
                recyclerView.adapter = null
                recyclerView.layoutManager = null
            }
        }
    }
}