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
import org.fossasia.susi.ai.chat.categories.adapters.MediaCategoryAdapter
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.helper.Constant

class MediaCategoryFragment : Fragment() {

    private val databaseRepository = DatabaseRepository()
    val medias: ArrayList<MediaCategoryFormat> = ArrayList()
    lateinit var mediaCategoryRecyclerView: RecyclerView
    private var mediaCategoryAdapter: RecyclerView.Adapter<*>? = null
    private lateinit var mediaStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_media_category, container, false)
        mediaCategoryRecyclerView = rootView.findViewById(R.id.media_category_recyclerview)
        mediaStatus = rootView.findViewById(R.id.media_not_found)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadMedias()
        super.onViewCreated(view, savedInstanceState)
    }

    // Finding out the ones that have images
    private fun loadMedias() {
        val results = databaseRepository.getAllMessages()
        results.forEach { result ->
            if (result.actionType == Constant.ANSWER) {
                val content = result.content
                if ((content?.endsWith(".jpg") as Boolean) || content.endsWith(".png")) {
                    val media = MediaCategoryFormat()
                    media.content_url = content
                    media.date = result.date
                    medias.add(media)
                }
            }
        }

        if (medias.size> 0) {
            mediaStatus.visibility = View.GONE
            viewMedias()
        } else {
            mediaStatus.visibility = View.VISIBLE
            mediaStatus.text = getString(R.string.empty_media_category)
        }
    }

    // Inflates the media category
    private fun viewMedias() {
        var mediaLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mediaCategoryRecyclerView.layoutManager = mediaLayoutManager
        mediaCategoryAdapter = MediaCategoryAdapter(medias)
        mediaCategoryRecyclerView.adapter = mediaCategoryAdapter
    }
}

class MediaCategoryFormat {
    var content_url: String? = null
    var date: String? = null
}