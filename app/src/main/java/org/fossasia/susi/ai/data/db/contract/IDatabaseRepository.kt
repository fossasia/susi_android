package org.fossasia.susi.ai.data.db.contract

import io.realm.RealmResults
import org.fossasia.susi.ai.data.model.ChatMessage

/**
 *
 * Created by chiragw15 on 12/7/17.
 */
interface IDatabaseRepository {
    fun getMessageCount() : Long
    fun deleteAllMessages() : Boolean
    fun getUndeliveredMessages() : RealmResults<ChatMessage>
    fun getAllMessages(): RealmResults<ChatMessage>
    fun getSearchResults(query: String): RealmResults<ChatMessage>
}