package edu.gwu.androidtweets

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class TweetsAdapter constructor(private val tweets: List<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {


    // Open & parse our XML file for our row and return the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Open & parse our XML file
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_tweet, parent, false)

        // Create a new ViewHolder
        return ViewHolder(view)
    }

    // Returns the number of rows to render
    override fun getItemCount(): Int = tweets.size


    // Given a new row, fill it with data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTweet = tweets[position]

        holder.usernameTextView.text = currentTweet.username
        holder.handleTextView.text = currentTweet.handle
        holder.contentTextView.text = currentTweet.content

        // TODO Download the image by its URL and load it into the ImageView
    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        val usernameTextView: TextView = view.findViewById(R.id.username)

        val handleTextView: TextView = view.findViewById(R.id.handle)

        val contentTextView: TextView = view.findViewById(R.id.tweet_content)

        val iconImageView: ImageView = view.findViewById(R.id.icon)

    }
}