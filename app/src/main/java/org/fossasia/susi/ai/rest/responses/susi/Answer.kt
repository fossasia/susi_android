package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

import io.realm.RealmList

/**
 * <h1>Kotlin Data class to parse answer in retrofit response from susi client.</h1>
 */

data class Answer(

        var data: List<Map<String, String>> = ArrayList(),

        val metadata: Metadata? = null,

        val actions: List<Action> = ArrayList(),

        val skills: List<String> = ArrayList()
)
