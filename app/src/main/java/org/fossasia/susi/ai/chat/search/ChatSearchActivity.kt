package org.fossasia.susi.ai.chat.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.realm.Realm
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.search.adapters.ChatSearchAdapter
import org.fossasia.susi.ai.data.db.DatabaseRepository
import android.content.Intent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat_search.chatSearchToolbar
import kotlinx.android.synthetic.main.activity_chat_search.search_not_found
import org.fossasia.susi.ai.chat.ChatActivity

/**
 *This Activity handles the operations related
 * to searching of chat messages.
 *
 * Created by atm1504 on 01/04/1968
 *
 */

class ChatSearchActivity : AppCompatActivity() {

    var realm = Realm.getDefaultInstance()
    var databaseRepository = DatabaseRepository()
    var query: String? = ""

    var searchChat: ArrayList<SearchDataFormat> = ArrayList()
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle: Bundle ? = intent.extras
        query = bundle?.getString("query")
        setSupportActionBar(chatSearchToolbar)

        setContentView(R.layout.activity_chat_search)
        loadQueryList(realm, query)
    }

    //Initialise the recylerview and sends the array list to it.
    fun viewQueryString() {
        mRecyclerView = findViewById(R.id.search_chat_feed)
        var mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = mLayoutManager
        mAdapter = ChatSearchAdapter(searchChat)
        mRecyclerView?.adapter = mAdapter
    }

    //Take data from realm databse according to the query provided
    fun loadQueryList(realm: Realm, query: String?) {
        var result = databaseRepository.getSearchResults(query.toString())
        result.forEach { result ->
            val searchData = SearchDataFormat()
            searchData.content = result.content
            searchData.date = result.date
            searchData.isMine = result.isMine

            searchChat.add(searchData)
        }
        if (searchChat.size>0) {
            search_not_found.setVisibility(View.INVISIBLE)
            viewQueryString()
        } else {
            Toast.makeText(this, R.string.chat_search_status, Toast.LENGTH_LONG).show()
            search_not_found.setVisibility(View.VISIBLE)
        }
    }

    // HAndles the when back is pressedbutton or back is pressed in mobile.
    override fun onBackPressed() {
        var intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

//Custom arraylist to store data as required.
class SearchDataFormat {
    var content: String? = null
    var date: String? = null
    var isMine: Boolean = false
}
