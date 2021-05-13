package com.apollyon.samproject.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.apollyon.samproject.R

class Adapter(
        private var models: List<TrainingMode>,
        private var context: Context?
) : PagerAdapter() {



    private lateinit var layoutInflater: LayoutInflater


    override fun getCount(): Int {
        return models.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item, container, false)

        val imageView: ImageView
        val titleView: TextView
        val descrView: TextView

        imageView = view.findViewById(R.id.image)
        titleView = view.findViewById(R.id.title)
        descrView = view.findViewById(R.id.description)

        imageView.setImageResource(models[position].image)
        titleView.text = models[position].title
        descrView.text = models[position].description

        container.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}