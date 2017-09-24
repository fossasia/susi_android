package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

import io.realm.RealmList

/**
 * <h1>Kotlin Data class to parse answer in retrofit response from susi client.</h1>
 */

class Answer {

    @SerializedName("data")
    @Expose
    val data = RealmList<Datum>()

    @SerializedName("metadata")
    @Expose
    val metadata: Metadata? = null

    @SerializedName("actions")
    @Expose
    val actions: List<Action> = ArrayList()

    @SerializedName("skills")
    @Expose
    val skills: List<String> = ArrayList()

}
