package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.viewholders.ChatViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.DateViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.ImageViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.LinkPreviewViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.MapViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.MessageViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.PieChartViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.SearchResultsListHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.TableViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.TypingDotsHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.YoutubeVideoViewHolder
import org.fossasia.susi.ai.chat.adapters.viewholders.ZeroHeightHolder
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.ConstraintsHelper
import org.fossasia.susi.ai.data.model.ChatMessage

import java.util.ArrayList

import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class ChatFeedRecyclerAdapter(private val currContext: Context, data: OrderedRealmCollection<ChatMessage>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<ChatMessage, RecyclerView.ViewHolder>(data, autoUpdate), MessageViewHolder.ClickListener {
    private var realm: Realm? = null
    private var lastMsgCount: Int = 0
    private var recyclerView: RecyclerView? = null
    private val clickListener: MessageViewHolder.ClickListener
    // For typing dots from Susi
    private val dotsHolder: TypingDotsHolder
    private val nullHolder: ZeroHeightHolder
    private var isSusiTyping = false

    init {
        this.clickListener = this
        lastMsgCount = itemCount

        if (data is RealmResults) {
            data.addChangeListener(RealmChangeListener {
                //only scroll if new is added.
                if (lastMsgCount < itemCount) {
                    scrollToBottom()
                }
                lastMsgCount = itemCount
            })
        }
        val inflater = LayoutInflater.from(currContext)
        val view = inflater.inflate(R.layout.item_waiting_dots, null)
        dotsHolder = TypingDotsHolder(view)
        val dots = dotsHolder.dotsTextView
        dots.start()
        val view1 = inflater.inflate(R.layout.item_without_height, null)
        nullHolder = ZeroHeightHolder(view1)
    }

    /**
     * Show dots while susi is typing.
     */
    fun showDots() {
        isSusiTyping = true
    }

    /**
     * Hide dots when susi is not typing.
     */
    fun hideDots() {
        isSusiTyping = false
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
        realm = Realm.getDefaultInstance()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        this.recyclerView = null
        realm?.close()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(viewGroup.context)
        val view: View
        when (viewType) {
            USER_MESSAGE -> {
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false)
                return ChatViewHolder(view, clickListener, USER_MESSAGE)
            }
            SUSI_MESSAGE -> {
                view = inflater.inflate(R.layout.item_susi_message, viewGroup, false)
                return ChatViewHolder(view, clickListener, SUSI_MESSAGE)
            }
            USER_IMAGE -> {
                view = inflater.inflate(R.layout.item_user_image, viewGroup, false)
                return ChatViewHolder(view, clickListener, USER_IMAGE)
            }
            SUSI_IMAGE -> {
                view = inflater.inflate(R.layout.item_susi_image, viewGroup, false)
                return ChatViewHolder(view, clickListener, SUSI_IMAGE)
            }
            MAP -> {
                view = inflater.inflate(R.layout.item_susi_map, viewGroup, false)
                return MapViewHolder(view)
            }
            USER_WITHLINK -> {
                view = inflater.inflate(R.layout.item_user_link_preview, viewGroup, false)
                return LinkPreviewViewHolder(view, clickListener)
            }
            SUSI_WITHLINK -> {
                view = inflater.inflate(R.layout.item_susi_link_preview, viewGroup, false)
                return LinkPreviewViewHolder(view, clickListener)
            }
            PIECHART -> {
                view = inflater.inflate(R.layout.item_susi_piechart, viewGroup, false)
                return PieChartViewHolder(view, clickListener)
            }
            SEARCH_RESULT -> {
                view = inflater.inflate(R.layout.search_list, viewGroup, false)
                val listResultHolder = SearchResultsListHolder(view)
                listResultHolder.recyclerView.addItemDecoration(ConstraintsHelper(6, currContext))
                return listResultHolder
            }
            TABLE -> {
                view = inflater.inflate(R.layout.susi_table, viewGroup, false)
                return TableViewHolder(view, clickListener)
            }
            WEB_SEARCH -> {
                view = inflater.inflate(R.layout.search_list, viewGroup, false)
                val webResultsListHolder = SearchResultsListHolder(view)
                webResultsListHolder.recyclerView.addItemDecoration(ConstraintsHelper(6, currContext))
                return webResultsListHolder
            }
            DATE_VIEW -> {
                view = inflater.inflate(R.layout.date_view, viewGroup, false)
                return DateViewHolder(view)
            }
            DOTS -> return dotsHolder
            NULL_HOLDER -> return nullHolder
            STOP -> return nullHolder
            AUDIOPLAY -> {
                view = inflater.inflate(R.layout.youtube_video, viewGroup, false)
                return YoutubeVideoViewHolder(view, clickListener)
            }
            VIDEOPLAY -> {
                view = inflater.inflate(R.layout.youtube_video, viewGroup, false)
                return YoutubeVideoViewHolder(view, clickListener)
            }
            IMAGE -> {
                view = inflater.inflate(R.layout.image_holder, viewGroup, false)
                return ImageViewHolder(view, clickListener)
            }
            else -> {
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false)
                return ChatViewHolder(view, clickListener, USER_MESSAGE)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return SUSI_MESSAGE
        val itemContent = item.content

        if (item.id == -404L)
            return DOTS
        else if (item.id == -405L)
            return NULL_HOLDER
        else if (item.isDate)
            return DATE_VIEW
        else if (itemContent != null && (itemContent.endsWith(".jpg") || itemContent.endsWith(".png")))
            return IMAGE
        else if (item.isMine && item.isHavingLink)
            return USER_WITHLINK
        else if (!item.isMine && item.isHavingLink)
            return SUSI_WITHLINK
        else if (item.isMine && !item.isHavingLink) return USER_MESSAGE

        return when (item.actionType) {
            Constant.ANCHOR -> SUSI_MESSAGE
            Constant.ANSWER -> SUSI_MESSAGE
            Constant.MAP -> MAP
            Constant.WEBSEARCH -> WEB_SEARCH
            Constant.RSS -> SEARCH_RESULT
            Constant.TABLE -> TABLE
            Constant.PIECHART -> PIECHART
            Constant.AUDIOPLAY -> AUDIOPLAY
            Constant.VIDEOPLAY -> VIDEOPLAY
            else -> SUSI_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        val data = data
        return if (data != null && data.isValid) {
            data.size + 1
        } else 0
    }

    override fun getItem(index: Int): ChatMessage? {
        val finalData = data
        return if (finalData != null && finalData.isValid) {
            ChatMessage()
            if (index == finalData.size) {
                val chatMessage = ChatMessage(
                        id = -404,
                        content = "",
                        date = "",
                        timeStamp = "",
                        datumRealmList = null,
                        webquery = "",
                        skillLocation = "",
                        identifier = ""
                )
                if (!isSusiTyping) {
                    chatMessage.id = -405
                }

                chatMessage
            } else finalData[index]
        } else null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatViewHolder) {
            holder.setView(data?.get(position), getItemViewType(position), currContext)
        } else if (holder is MapViewHolder) {
            holder.setView(data?.get(position), currContext)
        } else if (holder is YoutubeVideoViewHolder) {
            holder.setPlayerView(data?.get(position))
        } else if (holder is PieChartViewHolder) {
            holder.setView(data?.get(position))
        } else if (holder is TableViewHolder) {
            holder.setView(data?.get(position))
        } else if (holder is LinkPreviewViewHolder) {
            setOnLinkLongClickListener(position, holder)
            data?.get(position)?.let { holder.setView(it, getItemViewType(position), currContext) }
        } else if (holder is SearchResultsListHolder && getItemViewType(position) == SEARCH_RESULT) {
            holder.setView(data?.get(position), false, currContext)
        } else if (holder is SearchResultsListHolder && getItemViewType(position) == WEB_SEARCH) {
            holder.setView(data?.get(position), true, currContext)
        } else if (holder is DateViewHolder) {
            holder.textDate.text = data?.get(position)?.date
        } else if (holder is ImageViewHolder) {
            holder.setView(data?.get(position))
        }
    }

    private fun setOnLinkLongClickListener(position: Int, viewHolder: LinkPreviewViewHolder) {

        viewHolder.previewLayout.setOnLongClickListener {
            onItemLongClicked(position)
            true
        }

        viewHolder.text.setOnLongClickListener {
            onItemLongClicked(position)
            true
        }
    }

    /**
     * To set clipboard for copying text messages
     *
     * @param text text to be copied
     */
    private fun setClipboard(text: String?) {
        val clipboard = currContext.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager?
        val clip = android.content.ClipData.newPlainText("Copied Text", text)
        if (clipboard != null) {
            clipboard.primaryClip = clip
        }
    }

    private fun shareMessage(message: String?) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.type = "text/plain"
        currContext.startActivity(Intent.createChooser(sendIntent, currContext.getString(R.string.share_message)))
    }

    /**
     * Scroll to bottom
     */
    private fun scrollToBottom() {
        val data = data
        val recyclerView = recyclerView
        if (data != null && !data.isEmpty() && recyclerView != null) {
            recyclerView.smoothScrollToPosition(itemCount - 1)
        }
    }

    override fun onItemClicked(position: Int) {
        // Add something here when needed
    }

    private fun setBackGroundColor(holder: RecyclerView.ViewHolder, isSelected: Boolean, isUserMessage: Boolean) {
        if (holder is ChatViewHolder) {
            if (isUserMessage)
                holder.backgroundLayout.setBackgroundDrawable(if (isSelected)
                    currContext.resources.getDrawable(R.drawable.rounded_layout_selected)
                else
                    currContext.resources.getDrawable(R.drawable.rounded_layout_grey))
            else
                holder.backgroundLayout.setBackgroundDrawable(if (isSelected)
                    currContext.resources.getDrawable(R.drawable.rounded_layout_selected)
                else
                    currContext.resources.getDrawable(R.drawable.rounded_layout))
        } else if (holder is LinkPreviewViewHolder) {
            if (isUserMessage)
                holder.backgroundLayout.setBackgroundDrawable(if (isSelected)
                    currContext.resources.getDrawable(R.drawable.rounded_layout_selected)
                else
                    currContext.resources.getDrawable(R.drawable.rounded_layout_grey))
            else
                holder.backgroundLayout.setBackgroundDrawable(if (isSelected)
                    currContext.resources.getDrawable(R.drawable.rounded_layout_selected)
                else
                    currContext.resources.getDrawable(R.drawable.rounded_layout))
        }
    }

    override fun onItemLongClicked(position: Int): Boolean {
        val holder = recyclerView?.findViewHolderForAdapterPosition(position)
        val viewType = getItemViewType(position)
        if (holder != null)
            setBackGroundColor(holder, true, viewType == USER_WITHLINK || viewType == USER_MESSAGE)

        val optionList = ArrayList<Pair<String, Drawable>>()
        optionList.add(Pair("Copy", currContext.resources.getDrawable(R.drawable.ic_content_copy_white_24dp)))
        optionList.add(Pair("Share", currContext.resources.getDrawable(R.drawable.ic_share_white_24dp)))

        val dialog = AlertDialog.Builder(currContext)
        val arrayAdapter = SelectionDialogListAdapter(currContext, optionList)

        dialog.setCancelable(true)

        dialog.setAdapter(arrayAdapter) { _, which ->
            if (holder != null)
                setBackGroundColor(holder, false, viewType == USER_WITHLINK || viewType == USER_MESSAGE)
            when (which) {
                0 -> {
                    setClipboard(getItem(position)?.content)
                    val toast = Toast.makeText(recyclerView?.context, R.string.message_copied, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
                1 -> shareMessage(getItem(position)?.content)
            }
        }

        dialog.setOnCancelListener {
            if (holder != null)
                setBackGroundColor(holder, false, viewType == USER_WITHLINK || viewType == USER_MESSAGE)
        }

        val alert = dialog.create()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(alert.window?.attributes)
        lp.width = 500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        alert.show()
        alert.window?.attributes = lp
        return true
    }

    companion object {

        const val USER_MESSAGE = 0
        const val SUSI_MESSAGE = 1
        const val USER_IMAGE = 2
        const val SUSI_IMAGE = 3
        private const val MAP = 4
        private const val PIECHART = 7
        const val USER_WITHLINK = 5
        private const val SUSI_WITHLINK = 6
        private const val DOTS = 8
        private const val NULL_HOLDER = 9
        private const val SEARCH_RESULT = 10
        private const val WEB_SEARCH = 11
        private const val DATE_VIEW = 12
        private const val TABLE = 13
        private const val STOP = 14
        private const val AUDIOPLAY = 15
        private const val VIDEOPLAY = 16
        private const val IMAGE = 17

        /**
         * Extract links from text
         *
         * @param text String text
         * @return List of urls
         */
        fun extractLinks(text: String): List<String> {
            val links = ArrayList<String>()
            val m = Patterns.WEB_URL.matcher(text)
            while (m.find()) {
                val url = m.group()
                links.add(url)
            }
            return links
        }
    }
}