package org.fossasia.susi.ai.activities

import io.realm.Realm
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.model.ChatMessage

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
}