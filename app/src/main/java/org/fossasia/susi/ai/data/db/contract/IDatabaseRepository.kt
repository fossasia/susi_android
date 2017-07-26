package org.fossasia.susi.ai.data.db.contract

import io.realm.RealmResults
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.data.model.MapData
import org.fossasia.susi.ai.rest.responses.susi.Datum

/**
 * The interface for Database Repository
 *
 * Created by chiragw15 on 12/7/17.
 */
interface IDatabaseRepository {

    interface onDatabaseUpdateListener {
        fun onDatabaseUpdateSuccess()
        fun updateMessageCount()
    }

    fun getMessageCount() : Long
    fun getAMessage(index: Long): ChatMessage
    fun deleteAllMessages()
    fun getUndeliveredMessages() : RealmResults<ChatMessage>
    fun getAllMessages(): RealmResults<ChatMessage>
    fun getSearchResults(query: String): RealmResults<ChatMessage>
    fun closeDatabase()
    fun updateDatabase(prevId: Long, message: String, isDate: Boolean, date: String, timeStamp: String,
                       mine: Boolean, actionType: String, mapData: MapData?, isHavingLink: Boolean,
                       datumList: List<Datum>?, webSearch: String, count: Int, listener: onDatabaseUpdateListener )
}