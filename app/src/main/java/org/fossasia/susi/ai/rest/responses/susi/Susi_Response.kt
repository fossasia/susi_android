package org.fossasia.susi.ai.rest.responses.susi

import io.realm.RealmList
import io.realm.RealmObject
import java.util.*

/**
 * <h1>Kotlin Data class to parse action types in retrofit response from susi client.</h1>
 */

class Action (

    val delay: Long = 0,

    val expression: String = "",

    val type: String = "answer",

    val anchorLink: String? = null,

    val anchorText: String? = null,

    val query: String = "",

    val latitude: Double = 0.toDouble(),

    val longitude: Double = 0.toDouble(),

    val zoom: Double = 0.toDouble(),

    val count: Int = 0,

    val language: String = "en"

)

class Answer (


        val data: RealmList<Datum> = RealmList<Datum>(),

        val metadata: Metadata? = null,

        val actions: List<Action> = ArrayList(),

        val skills: List<String> = ArrayList()

)

class ChangeSettingResponse (

        /**
         * Gets session
         *
         * @return the session
         */

        val session: Session? = null,

        /**
         * Gets message
         *
         * @return the message
         */

        val message: String? = null
)

class Datum : RealmObject() (

/**
 * Gets 0.

 * @return the 0
 */

val _0: String? = null
/**
 * Gets 1.

 * @return the 1
 */

val _1: String? = null
/**
 * Gets answer.

 * @return the answer
 */

val answer: String? = null
/**
 * Gets query.

 * @return the query
 */

val query: String? = null
/**
 * Gets lon.

 * @return the lon
 */

val lon: Double = 0.toDouble()
/**
 * Gets place.

 * @return the place
 */

val place: String? = null
/**
 * Gets lat.

 * @return the lat
 */

val lat: Double = 0.toDouble()
/**
 * Gets population.

 * @return the population
 */

val population: Int = 0
/**
 * Gets percent.

 * @return the percent
 */

val percent: Float = 0.toFloat()
/**
 * Gets president.

 * @return the president
 */

val president: String? = null
/**
 * Gets title.

 * @return the title
 */
/**
 * Sets title.

 * @param title the title
 */

var title: String? = null
/**
 * Gets description.

 * @return the description
 */
/**
 * Sets description.

 * @param description the description
 */

var description: String? = null
/**
 * Gets link.

 * @return the link
 */
/**
 * Sets link.

 * @param link the link
 */

var link: String? = null

)


class ForgotPasswordResponse (

        val message: String? = null
)

class Identity (

        val name: String? = null,

        val type: String? = null,

        val anonymous: Boolean = false
)

class ListGroupsResponse (

        val groups: List<String> = ArrayList()
)

class ListSkillsResponse (

        val group: String = "Knowledge",

        val skillMap: Map<String, SkillData> = HashMap()
)


class LoginResponse (

        val message: String? = null,

        val session: Session? = null,

        val validSeconds: Long = 0,

        var accessToken: String? = null
                //internal set

)


class MemoryResponse (


        var cognitionsList: List<SusiResponse> = ArrayList(),

        var session: Session? = null

)

class Metadata (

        val count: Int = 0

)

class ResetPasswordResponse (

        val session: Session? = null,

        val accepted: Boolean = false,

        val message: String? = null

)

class Session (


        val identity: Identity? = null

)

class Settings (

        val speechAlways: Boolean = false,

        val enterSend: Boolean = false,

        val speechOutput: Boolean = true,

        val micInput: Boolean = true,

        val language: String = "default"

)

class SignUpResponse (

        val message: String? = null,

        val session: Session? = null

)

class SkillData (

        var image: String = "",

        var authorUrl: String = "",

        var examples: List<String> = ArrayList(),

        var developerPrivacyPolicy: String = "",

        var author: String = "",

        var skillName: String = "",

        var dynamicContent: Boolean ?= null,

        var termsOfUse: String = "",

        var descriptions: String = "",

        var skillRating: SkillRating ?= null

)

class SkillRating (

        var positive: Int = 0,

        var negative: Int = 0
)

class SkillRatingResponse (
        val session: Session ?= null,

        val accepted: Boolean = false,

        val message: String = ""
)

class Skills (

        val skillData: SkillData ?= null
)

class SusiResponse (

        val clientId: String? = null,

        val query: String = "",

        val queryDate: String = "",

        val answerDate: String = "",

        val answerTime: Int = 0,

        val count: Int = 0,

        val answers: List<Answer> = ArrayList(),

        val session: Session? = null

)

class UserSetting (

        /**
         * Gets session
         *
         * @return the session
         */

        val session: Session? = null,

        /**
         * Gets settings
         *
         * @return the settings
         */

        val settings: Settings? = null
)
