package com.apollyon.samproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.intl.Locale
import androidx.recyclerview.widget.RecyclerView
import com.apollyon.samproject.R
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.utilities.RunUtil
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

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
        var caloriesText : TextView
        var dateText : TextView
        var mapscreen : ImageView

        init {
            timeText = itemView.findViewById(R.id.time_text) as TextView
            distanceText = itemView.findViewById(R.id.distance_text) as TextView
            caloriesText = itemView.findViewById(R.id.calories_text) as TextView
            dateText = itemView.findViewById(R.id.date_text) as TextView
            mapscreen = itemView.findViewById(R.id.image) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.run_card, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.timeText.text = "Time: ${RunUtil.getFormattedTime(item.timeMilli)}"
        holder.distanceText.text = "Distance: ${RunUtil.getDistanceKm( item.distanceInMeters)} km"
        holder.caloriesText.text = item.caloriesBurned.toString()
        holder.dateText.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(item.timestamp)
        holder.mapscreen.setImageBitmap(item.map_screen)
    }

    override fun getItemCount() = data.size

}