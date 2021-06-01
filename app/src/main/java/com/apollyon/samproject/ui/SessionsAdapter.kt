package com.apollyon.samproject.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.apollyon.samproject.R
import com.apollyon.samproject.data.RunningSession

class SessionsAdapter : RecyclerView.Adapter<SessionsAdapter.ViewHolder>() {

    //dati che contiene
    var data = listOf<RunningSession>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var timeText: TextView
        var distanceText : TextView
        var mapscreen : ImageView

        init {
            timeText = itemView.findViewById(R.id.time_text) as TextView
            distanceText = itemView.findViewById(R.id.distance_text) as TextView
            mapscreen = itemView.findViewById(R.id.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.run_card, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.timeText.text = item.timeMilli.toString()
        holder.distanceText.text = item.distanceInMeters.toString()
        holder.mapscreen.setImageResource(R.drawable.raccoon)
    }

    override fun getItemCount() = data.size

}