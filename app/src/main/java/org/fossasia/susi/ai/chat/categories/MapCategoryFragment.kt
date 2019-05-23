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
import org.fossasia.susi.ai.chat.search.adapters.MapCategoryAdapter
import org.fossasia.susi.ai.data.db.DatabaseRepository

class MapCategoryFragment : Fragment() {

    private val databaseRepository = DatabaseRepository()
    val maps: ArrayList<MapCategoryFormat> = ArrayList()
    lateinit var mapCategoryRecyclerView: RecyclerView
    private var mapCategoryAdapter: RecyclerView.Adapter<*>? = null
    private lateinit var mapStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_map_category, container, false)
        mapCategoryRecyclerView = rootView.findViewById(R.id.map_category_recyclerview)
        mapStatus = rootView.findViewById(R.id.map_not_found)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadMaps()
        super.onViewCreated(view, savedInstanceState)
    }

    // It loads the map urls in an array adapter
    fun loadMaps() {
        val results = databaseRepository.getAllMessages()
        results.forEach { result ->
            if (result.content!!.contains("openstreetmap", ignoreCase = true)) {

                val actual_url = MapCategoryFormat()
                val map_url = result.content!!.split('"')

                actual_url.url = map_url[1]
                maps.add(actual_url)
            }
        }
        if (maps.size > 0) {
            mapStatus.visibility = View.GONE
            viewChats()
        } else {
            mapStatus.visibility = View.VISIBLE
            mapStatus.text = getString(R.string.empty_chat_category)
        }
    }

    // It passes the list of the gathered maps to MapCategoryAdapter
    fun viewChats() {

        var mapLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mapCategoryRecyclerView?.layoutManager = mapLayoutManager
        mapCategoryAdapter = MapCategoryAdapter(maps)
        mapCategoryRecyclerView?.adapter = mapCategoryAdapter
    }
}

// Format to get maps
class MapCategoryFormat {
    var url: String? = null
}
