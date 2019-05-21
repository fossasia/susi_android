package org.fossasia.susi.ai.chat.categories

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.search.adapters.ChatCategoryAdapter
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.helper.Constant

class ChatCategoryFragment : Fragment() {

    private val databaseRepository = DatabaseRepository()
    val chats: ArrayList<ChatCategoryFormat> = ArrayList()
    lateinit var chatCategoryRecyclerView: RecyclerView
    private var chatCategoryAdapter: RecyclerView.Adapter<*>? = null
    private lateinit var chatStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_chat_category, container, false)
        chatCategoryRecyclerView = rootView.findViewById(R.id.chat_category_recyclerview)
        chatStatus = rootView.findViewById(R.id.chat_not_found)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadChats()
        super.onViewCreated(view, savedInstanceState)
    }

    // It loads the chats in an array adapter
    fun loadChats() {
        val results = databaseRepository.getAllMessages()
        results.forEach { result ->
            if (result.actionType == Constant.ANSWER) {
                val content = result.content
                if (!(content!!.endsWith(".jpg") || content!!.endsWith(".png") || content.startsWith("Playing"))) {
                    val chat = ChatCategoryFormat()
                    chat.content = result.content
                    chat.date = result.date
                    chat.isMine = result.isMine
                    chats.add(chat)
                }
            }
        }
        if (chats.size > 0) {
            chatStatus.visibility = View.GONE
            viewChats()
        } else {
            chatStatus.visibility = View.VISIBLE
            chatStatus.text = getString(R.string.empty_chat_category)
        }
    }

    // It passes the list of the gathered chats to ChatCategoryAdapter
    fun viewChats() {

        var mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        chatCategoryRecyclerView?.layoutManager = mLayoutManager
        chatCategoryAdapter = ChatCategoryAdapter(chats)
        chatCategoryRecyclerView?.adapter = chatCategoryAdapter
    }
}

// Format to get normal chats
class ChatCategoryFormat {
    var content: String? = null
    var date: String? = null
    var isMine: Boolean = false
}
