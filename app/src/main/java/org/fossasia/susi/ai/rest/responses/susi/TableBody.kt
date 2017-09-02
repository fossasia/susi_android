package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 26/8/17.
 */
class TableBody {
    @SerializedName("body")
    @Expose
    lateinit var body: TableSusiResponse

}