package org.fossasia.susi.ai.data.db

import io.realm.Case
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.data.model.*
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.Datum
import timber.log.Timber

/**
 * The Database repository. Does all database operations like updating database,
 * deleting from database, searching from database etc
 *
 * Created by chiragw15 on 12/7/17.
 */
class DatabaseRepository : IDatabaseRepository {

    var realm: Realm = Realm.getDefaultInstance()

    override fun getMessageCount(): Long {
        val temp = realm.where(ChatMessage::class.java).max(Constant.ID)
        return if (temp == null)
            -1
        else
            temp as Long
    }

    override fun getAMessage(index: Long): ChatMessage? {
        return realm.where(ChatMessage::class.java).equalTo("id", index).findFirst()
    }

    override fun deleteAllMessages() {
        realm.executeTransaction { realm -> realm.deleteAll() }
    }

    override fun getUndeliveredMessages(): RealmResults<ChatMessage> {
        return realm.where(ChatMessage::class.java).equalTo(Constant.IS_DELIVERED, false).findAll().sort(Constant.ID)
    }

    override fun getAllMessages(): RealmResults<ChatMessage> {
        return realm.where(ChatMessage::class.java).findAllSorted(Constant.ID)
    }

    override fun getSearchResults(query: String): RealmResults<ChatMessage> {
        return realm.where(ChatMessage::class.java).contains(Constant.CONTENT,
                query, Case.INSENSITIVE).findAll()
    }

    override fun updateDatabase(prevId: Long, message: String, isDate: Boolean, date: String,
                                timeStamp: String, mine: Boolean, actionType: String, mapData: MapData?,
                                isHavingLink: Boolean, datumList: List<Datum>?, webSearch: String, tableData: TableDatas?, skillLocation: String,
                                listener: IDatabaseRepository.OnDatabaseUpdateListener) {

        val id = PrefManager.getLong(Constant.MESSAGE_COUNT, 0)
        listener.updateMessageCount()

        realm.executeTransactionAsync({ bgRealm ->
            val chatMessage = bgRealm.createObject(ChatMessage::class.java, id)
            chatMessage.content = message
            chatMessage.date = date
            chatMessage.setIsDate(isDate)
            chatMessage.setIsMine(mine)
            chatMessage.timeStamp = timeStamp
            chatMessage.isHavingLink = isHavingLink
            if (mine)
                chatMessage.isDelivered = false
            else {
                chatMessage.actionType = actionType
                chatMessage.webquery = webSearch
                chatMessage.isDelivered = true
                if (mapData != null) {
                    chatMessage.latitude = mapData.latitude
                    chatMessage.longitude = mapData.longitude
                    chatMessage.zoom = mapData.zoom
                }
                if (tableData != null) {
                    val columnRealmList = RealmList<TableColumn>()
                    val tableDataRealmList = RealmList<TableData>()
                    for (column in tableData.columns) {
                        val realmColumn = bgRealm.createObject(TableColumn::class.java)
                        realmColumn.columnName = column
                        columnRealmList.add(realmColumn)
                    }
                    chatMessage.tableColumns = columnRealmList
                    for (tableData in tableData.tableData) {
                        val realmData = bgRealm.createObject(TableData::class.java)
                        realmData.tableData = tableData
                        tableDataRealmList.add(realmData)
                    }
                    chatMessage.tableData = tableDataRealmList
                }
                if (datumList != null) {
                    val datumRealmList = RealmList<Datum>()
                    for (datum in datumList) {
                        val realmDatum = bgRealm.createObject(Datum::class.java)
                        realmDatum.description = datum.description
                        realmDatum.link = datum.link
                        realmDatum.title = datum.title
                        datumRealmList.add(realmDatum)
                    }
                    chatMessage.datumRealmList = datumRealmList
                }
                chatMessage.skillLocation = skillLocation
            }
        }, {
            if (!mine) {
                realm.executeTransactionAsync { bgRealm ->
                    try {
                        val previouschatMessage = bgRealm.where(ChatMessage::class.java).equalTo("id", prevId).findFirst()
                        if (previouschatMessage != null && previouschatMessage.isMine) {
                            previouschatMessage.isDelivered = true
                            previouschatMessage.date = date
                            previouschatMessage.timeStamp = timeStamp
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }

                    try {
                        val previousChatMessage = bgRealm.where(ChatMessage::class.java).equalTo("id", prevId - 1).findFirst()
                        if (previousChatMessage != null && previousChatMessage.isDate) {
                            previousChatMessage.date = date
                            previousChatMessage.timeStamp = timeStamp
                            val previousChatMessage2 = bgRealm.where(ChatMessage::class.java).equalTo("id", prevId - 2).findFirst()
                            if (previousChatMessage2 != null && previousChatMessage2.date == previousChatMessage.date) {
                                previousChatMessage.deleteFromRealm()
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }

                listener.onDatabaseUpdateSuccess()
            }
        }) { error -> error.printStackTrace() }
    }

    override fun closeDatabase() {
        realm.close()
    }
}