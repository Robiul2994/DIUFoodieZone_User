package com.diu.mlab.foodie.zone.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.zone.databinding.ItemCartBinding
import com.diu.mlab.foodie.zone.databinding.ItemProcessInfoBinding
import com.diu.mlab.foodie.zone.domain.model.CartItem
import com.diu.mlab.foodie.zone.util.getDrawable

class ProgressListViewAdapter(
    private val progressList: List<Pair<String, String>>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemProcessInfoBinding,
        private val contest: Context,
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            list: List<Pair<String, String>>,
            position: Int
        ) {
            binding.type.text = list[position].first
            binding.time.text = list[position].second
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemProcessInfoBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context,
        )

    override fun getItemCount() = progressList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindView(progressList, position)
    }
}
