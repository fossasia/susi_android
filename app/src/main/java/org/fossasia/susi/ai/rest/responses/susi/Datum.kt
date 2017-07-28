package org.fossasia.susi.ai.rest.responses.susi

import io.realm.RealmObject

/**
 * <h1>Kotlin Data class to parse data object in retrofit response from susi client.</h1>
 */
open class Datum (

    val _0: String? = null,

    val _1: String? = null,

    val answer: String? = null,

    val query: String? = null,

    val lon: Double = 0.toDouble(),

    val place: String? = null,

    val lat: Double = 0.toDouble(),

    val population: Int = 0,

    val percent: Float = 0.toFloat(),

    val president: String? = null,

    var title: String? = null,

    var description: String? = null,

    var link: String? = null

) : RealmObject()
