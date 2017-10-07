package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import io.realm.RealmList
import java.util.*

/**
 * <h1>Kotlin Data class to parse answer in retrofit response from susi client.</h1>
 */

class Answer {


    @Expose
    val data = RealmList<Datum>()


    @Expose
    val metadata: Metadata? = null


    @Expose
    val actions: List<Action> = ArrayList()


    @Expose
    val skills: List<String> = ArrayList()

}
