package com.apollyon.samproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.apollyon.samproject.R
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.utilities.RunUtil
import java.text.SimpleDateFormat

class SessionsAdapter : RecyclerView.Adapter<SessionsAdapter.ViewHolder>() {

    //dati che contiene
    var data = listOf<RunningSession>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var timeText: TextView = itemView.findViewById(R.id.time_text) as TextView
        var distanceText : TextView = itemView.findViewById(R.id.distance_text) as TextView
        var caloriesText : TextView = itemView.findViewById(R.id.calories_text) as TextView
        var dateText : TextView = itemView.findViewById(R.id.date_text) as TextView
        var mapscreen : ImageView = itemView.findViewById(R.id.image) as ImageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.run_card, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.timeText.text = "Time: ${RunUtil.getFormattedTime(item.timeMilli)}"
        holder.distanceText.text = String.format("Distance: %.2f km", RunUtil.getDistanceKm( item.distanceInMeters))
        holder.caloriesText.text = "Calories: ${item.caloriesBurned}"
        holder.dateText.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(item.timestamp)
        holder.mapscreen.setImageBitmap(item.map_screen)
    }

    override fun getItemCount() = data.size

}