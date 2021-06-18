package com.apollyon.samproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.apollyon.samproject.R
import com.apollyon.samproject.data.Mission

class MissionsAdapter : RecyclerView.Adapter<MissionsAdapter.ViewHolder>() {

    var data = listOf<Mission>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nameText: TextView = itemView.findViewById(R.id.name_text)
        var descrText: TextView = itemView.findViewById(R.id.description_text)
        var iconImage: ImageView = itemView.findViewById(R.id.image)
        var checkObtained: CheckBox = itemView.findViewById(R.id.check_obtained)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.mission_card, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.nameText.text = item.name
        holder.descrText.text = item.description
        item.iconBitmap.let {
            if(it!=null) holder.iconImage.setImageBitmap(it)
            else holder.iconImage.setImageResource(R.drawable.material_01)
        }
        holder.checkObtained.isChecked = item.complete == true
    }

    override fun getItemCount(): Int {
        return data.size
    }
}