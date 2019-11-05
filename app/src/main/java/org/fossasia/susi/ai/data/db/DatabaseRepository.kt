package org.fossasia.susi.ai.data.db

import io.realm.Case
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.data.model.TableColumn
import org.fossasia.susi.ai.data.model.TableData
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
        return temp?.toLong() ?: -1
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
        return realm.where(ChatMessage::class.java).findAll().sort(Constant.ID)
    }

    override fun getSearchResults(query: String): RealmResults<ChatMessage> {
        return realm.where(ChatMessage::class.java).contains(Constant.CONTENT,
                query, Case.INSENSITIVE).findAll()
    }

    override fun updateDatabase(
        chatArgs: ChatArgs,
        listener: IDatabaseRepository.OnDatabaseUpdateListener
    ) {

        val id = PrefManager.getLong(Constant.MESSAGE_COUNT, 0)
        listener.updateMessageCount()

        realm.executeTransactionAsync({ bgRealm ->
            val chatMessage = bgRealm.createObject(ChatMessage::class.java, id)
            chatMessage.content = chatArgs.message
            chatMessage.date = chatArgs.date
            chatMessage.isDate = chatArgs.isDate
            chatMessage.isMine = chatArgs.mine
            chatMessage.timeStamp = chatArgs.timeStamp
            chatMessage.isHavingLink = chatArgs.isHavingLink
            if (chatArgs.mine)
                chatMessage.isDelivered = false
            else {
                chatMessage.actionType = chatArgs.actionType
                chatMessage.webquery = chatArgs.webSearch
                chatMessage.isDelivered = true
                if (chatArgs.mapData != null) {
                    chatMessage.latitude = chatArgs.mapData.latitude
                    chatMessage.longitude = chatArgs.mapData.longitude
                    chatMessage.zoom = chatArgs.mapData.zoom
                }
                if (chatArgs.tableItem != null) {
                    val columnRealmList = RealmList<TableColumn>()
                    val tableDataRealmList = RealmList<TableData>()
                    val columns = chatArgs.tableItem.columns
                    if (columns != null) {
                        for (column in columns) {
                            val realmColumn = bgRealm.createObject(TableColumn::class.java)
                            realmColumn.columnName = column
                            columnRealmList.add(realmColumn)
                        }
                    }
                    chatMessage.tableColumns = columnRealmList
                    val tableData = chatArgs.tableItem.tableData
                    if (tableData != null) {
                        for (tableDatum in tableData) {
                            val realmData = bgRealm.createObject(TableData::class.java)
                            realmData.tableData = tableDatum
                            tableDataRealmList.add(realmData)
                        }
                    }
                    chatMessage.tableData = tableDataRealmList
                }
                if (chatArgs.identifier != null) {
                    chatMessage.identifier = chatArgs.identifier
                }
                if (chatArgs.datumList != null) {
                    val datumRealmList = RealmList<Datum>()
                    for (datum in chatArgs.datumList) {
                        val realmDatum = bgRealm.createObject(Datum::class.java)
                        realmDatum.description = datum.description
                        realmDatum.link = datum.link
                        realmDatum.title = datum.title
                        datumRealmList.add(realmDatum)
                    }
                    chatMessage.datumRealmList = datumRealmList
                }
                chatMessage.skillLocation = chatArgs.skillLocation
            }
        }, {
            if (!chatArgs.mine) {
                realm.executeTransactionAsync { bgRealm ->
                    try {
                        val previousChatMessage = bgRealm.where(ChatMessage::class.java).equalTo("id", chatArgs.prevId).findFirst()
                        if (previousChatMessage != null && previousChatMessage.isMine) {
                            previousChatMessage.isDelivered = true
                            previousChatMessage.date = chatArgs.date
                            previousChatMessage.timeStamp = chatArgs.timeStamp
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }

                    try {
                        val previousChatMessage = bgRealm.where(ChatMessage::class.java).equalTo("id", chatArgs.prevId - 1).findFirst()
                        if (previousChatMessage != null && previousChatMessage.isDate) {
                            previousChatMessage.date = chatArgs.date
                            previousChatMessage.timeStamp = chatArgs.timeStamp
                            val previousChatMessage2 = bgRealm.where(ChatMessage::class.java).equalTo("id", chatArgs.prevId - 2).findFirst()
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
        }) { error -> Timber.e(error) }
    }

    override fun closeDatabase() {
        realm.close()
    }
}
