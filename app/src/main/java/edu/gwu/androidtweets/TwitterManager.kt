package edu.gwu.androidtweets

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class TwitterManager {

    // OkHttp is a library used to make network calls
    private val okHttpClient: OkHttpClient

    private var oAuthToken: String? = null

    // This runs extra code when TwitterManager is created (e.g. the constructor)
    init {
        val builder = OkHttpClient.Builder()

        // This sets network timeouts (in case the phone can't connect
        // to the server or the server is down)
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)

        // This causes all network traffic to be logged to the console
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        okHttpClient = builder.build()
    }

    fun retrieveOAuthToken(
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        if (oAuthToken != null) {
            successCallback(oAuthToken!!)
            return
        }

        val encodedKey = "TEtwV0l1ZlJ4bWpOY1kwSUlCeVJVblR2NDo1UGY4SXVvdEdjSHJpelZncHRNSVlkOGI2SHlRTGNvbXBjeTNZd1Q4WkFMbU9zandBNA=="

        val request: Request = Request.Builder()
            .url("https://api.twitter.com/oauth2/token")
            .header("Authorization", "Basic $encodedKey")
            .post(
                RequestBody.create(
                    MediaType.parse("application/x-www-form-urlencoded"),
                    "grant_type=client_credentials"
                )
            )
            .build()

        val response = okHttpClient.newCall(request).execute()

        val responseBody = response.body()?.string()
        if (response.isSuccessful && responseBody != null) {
            val jsonObject = JSONObject(responseBody)
            val token = jsonObject.getString("access_token")
            oAuthToken = token
            successCallback(token)
        } else {
            errorCallback(Exception("OAuth call failed"))
        }
    }

    fun retrieveTweets(
        successCallback: (List<Tweet>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        
    }
}