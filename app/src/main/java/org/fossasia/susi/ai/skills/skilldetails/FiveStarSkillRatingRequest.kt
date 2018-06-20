package org.fossasia.susi.ai.skills.skilldetails

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse
import org.fossasia.susi.ai.rest.services.SusiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/**
 *
 *  Created by arundhati24 on 09/06/18
 */
class FiveStarSkillRatingRequest {

    companion object {
        lateinit var interceptor: HttpLoggingInterceptor
        lateinit var client: OkHttpClient
        lateinit var retrofit: Retrofit
        val BASE_URL: String = "https://api.susi.ai/"

        /**
         * Sends the user rating to the server via "cms/fiveStarRateskill.json/" API
         *
         * @param model : e.g. general
         * @param group : Skill group e.g. Knowledge
         * @param language : Language directory in which the skill resides in the susi_skill_data repo e.g. en
         * @param skillTag : Tells which skill has been rated inside the skills object
         * @param rating : User rating for the skill
         * @param accessToken : Access token for the logged in user
         *
         */
        fun sendFiveStarRating(map: Map<String, String>) {
            interceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }

            client = OkHttpClient.Builder().apply {
                this.addInterceptor(interceptor)
            }.build()

            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            val service: SusiService = retrofit.create(SusiService::class.java)
            service.fiveStarRateSkill(map)
                    .enqueue(object : Callback<FiveStarSkillRatingResponse> {
                        override fun onResponse(call: Call<FiveStarSkillRatingResponse>?,
                                                response: Response<FiveStarSkillRatingResponse>?) {
                            if (response!!.isSuccessful) {
                                Timber.d("Request successful : %s", response.body().message)
                                Timber.d("Accepted: %s", response.body().accepted)
                                Timber.d("Rating: %s", response.body().ratings)
                                Timber.d("sesson: %s", response.body().session)
                            }
                        }

                        override fun onFailure(call: Call<FiveStarSkillRatingResponse>?, t: Throwable?) {
                            Timber.d("Request failed")
                        }
                    })
        }
    }
}