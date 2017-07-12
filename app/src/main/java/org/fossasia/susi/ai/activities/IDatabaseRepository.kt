package org.fossasia.susi.ai.activities

/**
 * Created by chiragw15 on 12/7/17.
 */
interface IDatabaseRepository {
    fun getMessageCount() : Long
    fun deleteAllMessages() : Boolean
}