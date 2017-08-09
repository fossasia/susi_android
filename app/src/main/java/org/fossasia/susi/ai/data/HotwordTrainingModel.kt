package org.fossasia.susi.ai.data

import ai.kitt.snowboy.Constants
import org.fossasia.susi.ai.data.contract.IHotwordTrainingModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *
 * Created by chiragw15 on 14/8/17.
 */
class HotwordTrainingModel: IHotwordTrainingModel {

    private val apiToken: String = "2125cad4acfc1055f9998b1fee613b35e3c2db4c"
    override fun downloadModel(recordings: Array<String?>, listener: IHotwordTrainingModel.onHotwordTrainedFinishedListener) {

        val jsonArray = JSONArray()
        for (i in 0..2) {
            val jsonOb = JSONObject()
            jsonOb.put("wave", recordings.get(i))
            jsonArray.put(jsonOb)
        }
        Thread(Runnable {
            try {
                val url = URL("https://snowboy.kitt.ai/api/v1/train/")
                val conn: (HttpURLConnection) = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.doInput = true

                val jsonParam = JSONObject()
                jsonParam.put("microphone", "android")
                jsonParam.put("name", "susi")
                jsonParam.put("language", "en")
                jsonParam.put("token", apiToken)
                jsonParam.put("voice_samples", jsonArray)
                val os = DataOutputStream(conn.outputStream)
                os.writeBytes(jsonParam.toString().replace("\\", ""))

                os.flush()
                os.close()

                val fout = FileOutputStream(File(Constants.DEFAULT_WORK_SPACE + Constants.ACTIVE_PMDL), false)
                val ins = conn.inputStream

                var works = false
                var n = ins.read()
                while (n != -1) {
                    works = true
                    fout.write(n)
                    n = ins.read()
                }
                conn.disconnect()
                if (works)
                    listener.onTrainingSuccess()
                else
                    listener.onTrainingFailure()

            } catch (e: Exception) {

            }
        }).run()
    }
}