package org.fossasia.susi.ai.chat

import org.fossasia.susi.ai.rest.responses.susi.TableSusiResponse
import com.google.gson.Gson
import org.fossasia.susi.ai.data.model.TableDatas
import org.fossasia.susi.ai.rest.responses.susi.TableBody
import retrofit2.Response
import kotlin.collections.ArrayList


/**
 * Created by meeera on 20/8/17.
 */
class ParseTableSusiResponseHelper {
    var listColumn: ArrayList<String> = ArrayList<String>()
    var listTableData: ArrayList<String> = ArrayList<String>()
    var tableData: TableDatas?= null
    fun parseSusiResponse(response: Response<TableSusiResponse>) {
        try {
            var response1 = Gson().toJson(response)
            var tableresponse = Gson().fromJson(response1, TableBody::class.java)
            for (tableanswer in tableresponse.body.answers) {
                for (answer in tableanswer.actions) {
                    var map = answer.columns
                    val set = map?.entries
                    val iterator = set?.iterator()
                    while (iterator?.hasNext().toString().toBoolean()) {

                        val entry = iterator?.next()
                        listColumn.add(entry?.key.toString())
                    }
                }
                val map2 = tableanswer.data
                val iterator2 = map2?.iterator()
                while (iterator2?.hasNext().toString().toBoolean()) {
                    val entry2 = iterator2?.next()
                    for (ad in 0..listColumn.size-1) {
                        val obj = listColumn.get(ad)
                        listTableData.add(entry2?.get(obj).toString())
                    }
                }
                tableData = TableDatas(listColumn, listTableData)
            }
        } catch (e: Exception) {
             tableData = null
        }
    }
}