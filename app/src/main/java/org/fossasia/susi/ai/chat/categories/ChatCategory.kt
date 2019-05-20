package org.fossasia.susi.ai.chat.categories

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import org.fossasia.susi.ai.R

/** This is an activity used to
 * catgories the chat into 3 different types,
 * mainly Chats, Media, Maps.
 * More categories can be extended later depending
 * on the increase of susi skills.**/
class ChatCategory : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_chat -> {
                loadChats()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_media -> {
                loadMedia()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                loadMaps()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    // Function that loads the chatCategoryFragment into the Category Fragment
    fun loadChats() {
        val chatCategoryFragment = ChatCategoryFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.chat_category_parent_container, chatCategoryFragment)
                .commit()
    }

    // Function that loads the mediaCategoryFragment into the Category Fragment
    fun loadMedia() {
        val mediaCategoryFragment = MediaCategoryFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.chat_category_parent_container, mediaCategoryFragment)
                .commit()
    }

    // Function that loads the mapCategoryFragment into the Category Fragment
    fun loadMaps() {
        val mapCategoryFragment = MapCategoryFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.chat_category_parent_container, mapCategoryFragment)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_category)

        val chatCategoryFragment = ChatCategoryFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.chat_category_parent_container, chatCategoryFragment)
                .commit()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }
}
