package com.diu.mlab.foodie.zone.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.zone.databinding.ItemFoodBinding
import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.util.getDrawable

class FoodListViewAdapter(
    private val foodList: List<FoodItem>,
    private val shopEmail: String,
    private val fillWidth: Int = 0,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemFoodBinding,
        private val contest: Context,
        private val fillWidth: Int,
        private val shopEmail: String
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            list: List<FoodItem>,
            position: Int
        ) {
            binding.nm.text = list[position].nm
            binding.taha.text = list[position].price.split(",")[0]
            binding.time.text = list[position].time
            list[position].pic.getDrawable{ binding.pic.setImageDrawable(it) }
            if(fillWidth == 1){
                binding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            binding.card.setOnClickListener {
//                manager.beginTransaction()
//                    .run {
//                        addToBackStack("FoodListFragment")
//                        hide(foodListFragment)
//                        add(R.id.sellFragment, FoodAddFragment.newInstance(list[position].key))
//                        commit()
//                    }

                val bundle = Bundle()
                bundle.putString("shopEmail", shopEmail)
                bundle.putString("foodId", list[position].foodId)
                Log.d("TAG", "bindView: ${list[position].foodId}")
                contest.startActivity(Intent(contest, ShopFoodViewActivity::class.java).putExtras(bundle))
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemFoodBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context, fillWidth, shopEmail
        )

    override fun getItemCount() = foodList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(foodList[position].status!="Unavailable")
            (holder as ViewHolder).bindView(foodList, position)
    }
}
