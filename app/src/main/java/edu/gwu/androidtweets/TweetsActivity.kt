package edu.gwu.androidtweets

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class TweetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweets)

        val intent: Intent = intent
        val location: String = intent.getStringExtra("location")

        title = getString(R.string.tweets_title, location)
    }

}