package org.fossasia.susi.ai.activities

import android.util.Log
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.model.MapData
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse

/**
 * Created by chiragw15 on 10/7/17.
 */
class ParseSusiResponseHelper {

    val answer: String? = null
    val actionType: String? = null

    private fun parseSusiResponse(susiResponse: SusiResponse, i: Int) {

        actionType = susiResponse.answers[0].actions[i].type
        datumList = null
        mapData = null
        webSearch = ""
        isHavingLink = false
        answer = null

        when (actionType) {
            Constant.ANCHOR -> try {
                answer = "<a href=\"" + susiResponse.answers[0].actions[i].anchorLink + "\">" + susiResponse.answers[0].actions[1].anchorText + "</a>"
            } catch (e: Exception) {
                Log.d(TAG, e.localizedMessage)
                answer = getString(R.string.error_occurred_try_again)
            }

            Constant.ANSWER -> try {
                answer = susiResponse.answers[0].actions[i].expression
                val urlList = extractUrls(answer)
                Log.d(TAG, urlList!!.toString())
                isHavingLink = urlList != null
                if (urlList!!.size == 0) isHavingLink = false
            } catch (e: Exception) {
                answer = getString(R.string.error_occurred_try_again)
                isHavingLink = false
            }

            Constant.MAP -> try {
                val latitude = susiResponse.answers[0].actions[i].latitude
                val longitude = susiResponse.answers[0].actions[i].longitude
                val zoom = susiResponse.answers[0].actions[i].zoom
                mapData = MapData(latitude, longitude, zoom)
            } catch (e: Exception) {
                mapData = null
            }

            Constant.PIECHART -> try {
                datumList = susiResponse.answers[0].data
            } catch (e: Exception) {
                datumList = null
            }

            Constant.RSS -> try {
                datumList = susiResponse.answers[0].data
                count = susiResponse.answers[0].actions[i].count
            } catch (e: Exception) {
                datumList = null
            }

            Constant.WEBSEARCH -> try {
                webSearch = susiResponse.answers[0].actions[1].query
            } catch (e: Exception) {
                webSearch = ""
            }

            else -> answer = getString(R.string.error_occurred_try_again)
        }
    }
}