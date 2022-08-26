package com.example.waterme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.waterme.R
import com.example.waterme.model.PlantData

class PlantViewPagerAdapter(
    private val context: Context,
    private val plantList: ArrayList<PlantData>,
) : PagerAdapter() {

    override fun getCount(): Int = plantList.size


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        // inflate layout with plant_list_detail.xml
        val view =
            LayoutInflater.from(context).inflate(R.layout.plant_list_detail, container, false)

        val currentModel = plantList[position]

        val plantImage: ImageView = view.findViewById(R.id.dummy_image)
        val plantTitle: TextView = view.findViewById(R.id.dummy_title)
        val plantDescription: TextView = view.findViewById(R.id.dummy_description)
        val plantReminder: TextView = view.findViewById(R.id.dummy_reminder)

        plantImage.setImageResource(currentModel.imageResources)
        plantTitle.text = currentModel.plantTitle
        plantDescription.text = currentModel.plantTitle
        plantReminder.text = currentModel.plantTitle

        view.setOnClickListener {  }

        container.addView(view)

        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}