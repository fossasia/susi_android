package org.fossasia.susi.ai.rest.responses.susi

import io.realm.RealmObject

open class Datum : RealmObject() {
    var _0: String? = null
    var _1: String? = null
    var query: String? = null
    var answer: String? = null
    var place: String? = null
    var lat: Double = 0.toDouble()
    var lon: Double = 0.toDouble()
    var percent: Float = 0.toFloat()
    var president: String? = null
    var title: String? = null
    var description: String? = null
    var link: String? = null
}
