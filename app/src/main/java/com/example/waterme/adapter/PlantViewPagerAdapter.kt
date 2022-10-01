package com.example.waterme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.viewpager.widget.PagerAdapter
import com.example.waterme.R
import com.example.waterme.model.Plants
import com.example.waterme.ui.PlantListFragmentDirections
import com.example.waterme.viewmodel.PlantViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.doublelift.DoubleLiftLayout
import com.squareup.picasso.Picasso

class PlantViewPagerAdapter(
    private val context: Context,
    private val plantList: ArrayList<Plants>,
    private val navController: NavController,
    private val fragmentManager: FragmentManager,
    private val plantViewModel: PlantViewModel,
    private val onItemClicked: (Plants) -> Unit,
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
        val doubleLiftLayout3 = view.findViewById<DoubleLiftLayout>(R.id.doubleLiftLayout3)

        if (currentModel.plantImage != null) {
            Picasso.get().load(currentModel.plantImage).into(plantImage)
        } else {
            Picasso.get().load(R.drawable.plants).into(plantImage)
        }
        plantTitle.text = currentModel.plantTitle
        plantDescription.text = currentModel.plantTitle
        plantReminder.text = currentModel.plantTitle


        view.findViewById<LinearLayout>(R.id.card_view).setOnClickListener {
            onItemClicked(currentModel)
            val action = PlantListFragmentDirections.actionFirstFragmentToPlantDetailFragment(plants = currentModel)
            navController.navigate(action)
        }

        view.findViewById<AppCompatButton>(R.id.edit_plant).setOnClickListener {
//            val modalBottomSheet = EditPlantFragment(currentModel)
//            modalBottomSheet.show(fragmentManager, EditPlantFragment.TAG)
        }

        view.findViewById<AppCompatButton>(R.id.delete_plant).setOnClickListener {
            delete(currentModel)
        }
        doubleLiftLayout3.setOnClickListener {
            if (doubleLiftLayout3.isExpanded) {
           //     doubleLiftLayout3.collapse()
            } else {
                doubleLiftLayout3.expand()
            }
        }

        container.addView(view)

        return view
    }

    private fun delete(plants: Plants) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_supporting_text)
            .setNegativeButton(R.string.cancel) { dialog, which ->
                // Respond to neutral button press
                dialog.cancel()
            }
            .setPositiveButton(R.string.accept) { dialog, which ->
                // Respond to positive button press
                plantViewModel.deletePlant(plants)
                dialog.cancel()
            }
            .show()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}