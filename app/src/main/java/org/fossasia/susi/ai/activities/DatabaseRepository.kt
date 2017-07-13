package org.fossasia.susi.ai.activities

import io.realm.Realm
import io.realm.RealmResults
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant

/**
 * Created by chiragw15 on 12/7/17.
 */
class DatabaseRepository: IDatabaseRepository {

    var realm: Realm = Realm.getDefaultInstance()

    override fun getMessageCount(): Long {
        val temp = realm.where(ChatMessage::class.java).max(Constant.ID)
        if(temp == null)
            return -1
        else
            return temp as Long
    }

    override fun deleteAllMessages(): Boolean {
        return false
    }

    override fun getUndeliveredMessages(): RealmResults<ChatMessage> {
        return realm.where(ChatMessage::class.java).equalTo(Constant.IS_DELIVERED, false).findAll().sort(Constant.ID);
    }

    override fun getAllMessages(): RealmResults<ChatMessage> {
        return realm.where(ChatMessage::class.java).findAllSorted(Constant.ID);
    }
}