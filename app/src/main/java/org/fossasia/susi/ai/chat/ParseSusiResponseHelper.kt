package org.fossasia.susi.ai.chat

import android.util.Patterns
import io.realm.RealmList
import org.fossasia.susi.ai.data.model.MapData
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.rest.responses.susi.Datum
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import timber.log.Timber
import kotlin.collections.ArrayList

/**
 * Helper class to parse susi response
 *
 * Created by chiragw15 on 10/7/17.
 */
class ParseSusiResponseHelper {

    var answer: String = ""
    var actionType: String = Constant.ANSWER
    var datumList: RealmList<Datum>? = null
    var mapData: MapData? = null
    var webSearch = ""
    var stop = "Stopped"
    var isHavingLink = false

    fun parseSusiResponse(susiResponse: SusiResponse, i: Int, error: String) {

        actionType = susiResponse.answers[0].actions[i].type

        when (actionType) {
            Constant.ANCHOR -> answer = try {
                "<a href=\"" + susiResponse.answers[0].actions[i].anchorLink + "\">" + susiResponse.answers[0].actions[1].anchorText + "</a>"
            } catch (e: Exception) {
                Timber.e(e)
                error
            }

            Constant.ANSWER -> try {
                answer = susiResponse.answers[0].actions[i].expression
                val urlList = extractUrls(answer)
                isHavingLink = true
                if (urlList.isEmpty()) isHavingLink = false
            } catch (e: Exception) {
                Timber.e(e)
                answer = error
                isHavingLink = false
            }

            Constant.MAP -> mapData = try {
                val latitude = susiResponse.answers[0].actions[i].latitude
                val longitude = susiResponse.answers[0].actions[i].longitude
                val zoom = susiResponse.answers[0].actions[i].zoom
                MapData(latitude, longitude, zoom)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }

            Constant.PIECHART -> datumList = try {
                susiResponse.answers[0].data
            } catch (e: Exception) {
                Timber.e(e)
                null
            }

            Constant.RSS -> datumList = try {
                susiResponse.answers[0].data
            } catch (e: Exception) {
                Timber.e(e)
                null
            }

            Constant.WEBSEARCH -> webSearch = try {
                susiResponse.answers[0].actions[1].query
            } catch (e: Exception) {
                Timber.e(e)
                ""
            }

            Constant.STOP -> try {
                stop = susiResponse.answers[0].actions[1].type
            } catch (e: Exception) {
                Timber.e(e)
            }

            Constant.VIDEOPLAY -> try {
                answer = susiResponse.answers[0].actions[1].expression
            } catch (e: Exception) {
                Timber.e(e)
            }

            Constant.AUDIOPLAY -> try {
                answer = susiResponse.answers[0].actions[1].expression
            } catch (e: Exception) {
                Timber.e(e)
            }


            else -> answer = error
        }
    }

    companion object {
        fun extractUrls(text: String): List<String> {
            val links = ArrayList<String>()
            val m = Patterns.WEB_URL.matcher(text)
            while (m.find()) {
                val url = m.group()
                links.add(url)
            }
            return links
        }

        fun getSkillLocation(locationUrl: String): Map<String, String> {
            val susiLocation = mutableMapOf<String, String>()

            try {
                val locationArray = locationUrl.split("/")
                susiLocation["model"] = locationArray[3]
                susiLocation["group"] = locationArray[4]
                susiLocation["language"] = locationArray[5]
                susiLocation["skill"] = locationArray[6].split(".")[0]
            } catch (e: Exception) {
                Timber.e(e)
            }
            return susiLocation
        }
    }
}