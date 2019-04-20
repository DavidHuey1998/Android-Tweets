package edu.gwu.androidtweets.data.tweet

import android.location.Address
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

    /**
     * Calls the Twitter OAuth API. To use this function, pass in two lambdas:
     *  - [successCallback] will be called if the token is retrieved and is passed the token.
     *  - [errorCallback] will be called if the API call fails and is passed the [Exception].
     */
    fun retrieveOAuthToken(
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        // If the token is cached, we don't need to call the API
        if (oAuthToken != null) {
            successCallback(oAuthToken!!)
            return
        }

        // You would normally use your own secret keys from Twitter, but we can use mine for lecture
        // since it is difficult for everyone in class to get their own credentials.
        val encodedKey =
            "TEtwV0l1ZlJ4bWpOY1kwSUlCeVJVblR2NDo1UGY4SXVvdEdjSHJpelZncHRNSVlkOGI2SHlRTGNvbXBjeTNZd1Q4WkFMbU9zandBNA=="

        // Builds the OAuth request, which is comprised of:
        //   - URL: https://api.twitter.com/oauth2/token"
        //   - One header (the encoded key above)
        //   - It's a POST call
        //   - The body type is a special type (x-www-form-urlencoded). Usually, you will just see
        //     a JSON-based body.
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

        // Let OkHttp handle the actual networking. It will call one of the two callbacks...
        okHttpClient.newCall(request).enqueue(object : Callback {
            /**
             * [onFailure] is called if OkHttp is has an issue making the request (for example,
             * no network connectivity).
             */
            override fun onFailure(call: Call, e: IOException) {
                // Invoke the callback passed to our [retrieveOAuthToken] function.
                errorCallback(e)
            }

            /**
             * [onResponse] is called if OkHttp is able to get any response (successful or not)
             * back from the server
             */
            override fun onResponse(call: Call, response: Response) {
                // The token would be part of the JSON response body
                val responseBody = response.body()?.string()

                // Check if the response was successful (200 code) and the body is non-null
                if (response.isSuccessful && responseBody != null) {
                    // Parse the token out of the JSON
                    val jsonObject = JSONObject(responseBody)
                    val token = jsonObject.getString("access_token")
                    oAuthToken = token

                    // Invoke the callback passed to our [retrieveOAuthToken] function.
                    successCallback(token)
                } else {
                    // Invoke the callback passed to our [retrieveOAuthToken] function.
                    errorCallback(Exception("OAuth call failed"))
                }
            }
        })
    }

    fun retrieveTweets(
        oAuthToken: String,
        address: Address,
        successCallback: (List<Tweet>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        // Data setup
        val lat = address.latitude
        val lon = address.longitude
        val topic = "Android"
        val radius = "30mi"


        // Building the request, passing the OAuth token as a header
        val request = Request.Builder()
            .url("https://api.twitter.com/1.1/search/tweets.json?q=$topic&geocode=$lat,$lon,$radius")
            .header("Authorization", "Bearer $oAuthToken")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Same error handling to last time
                errorCallback(e)
            }

            override fun onResponse(call: Call, response: Response) {
                // Similar success / error handling to last time
                val tweets = mutableListOf<Tweet>()
                val responseString = response.body()?.string()

                if (response.isSuccessful && responseString != null) {

//                    val statuses = JSONObject(responseString).getJSONArray("statuses")
//                    for (i in 0 until statuses.length()) {
//                        val curr = statuses.getJSONObject(i)
//                        val text = curr.getString("text")
//                        val user = curr.getJSONObject("user")
//                        val name = user.getString("name")
//                        val handle = user.getString("screen_name")
//                        val profilePictureUrl = user.getString("profile_image_url")
//                        tweets.add(
//                            Tweet(
//                                iconUrl = profilePictureUrl,
//                                username = name,
//                                handle = handle,
//                                content = text
//                            )
//                        )
//                    }
                    successCallback(listOf())
                //...
                } else {
                    // Invoke the callback passed to our [retrieveTweets] function.
                    errorCallback(Exception("Search Tweets call failed"))
                }
            }
        })
    }
}