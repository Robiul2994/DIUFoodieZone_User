package com.diu.mlab.foodie.zone.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.zone.databinding.ItemCartBinding
import com.diu.mlab.foodie.zone.domain.model.CartItem
import com.diu.mlab.foodie.zone.presentation.auth.LoginActivity
import com.diu.mlab.foodie.zone.presentation.order.OrderActivity
import com.diu.mlab.foodie.zone.util.getDrawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CartListViewAdapter(
    private val cartList: List<CartItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemCartBinding,
        private val contest: Context,
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            list: List<CartItem>,
            position: Int
        ) {
            binding.nm.text = list[position].foodItem.nm
            binding.price.text = (list[position].typePrice * list[position].quantity).toString()
            binding.quantity.text = list[position].quantity.toString()
            list[position].foodItem.pic.getDrawable{ binding.pic.setImageDrawable(it) }

            val bundle = Bundle()
            bundle.putString("shopEmail", list[position].shopEmail)
            bundle.putString("foodId", list[position].foodItem.foodId)

            binding.btnFoodView.setOnClickListener {
                contest.startActivity(Intent(contest, ShopFoodViewActivity::class.java).putExtras(bundle))
            }
            binding.btnOrder.setOnClickListener {
                bundle.putString("currentType", list[position].type)
                bundle.putInt("currentTypePrice", list[position].typePrice)
                bundle.putInt("quantity", list[position].quantity)
                if(Firebase.auth.currentUser != null)
                    contest.startActivity(Intent(contest, OrderActivity::class.java).putExtras(bundle))
                else {
                    Toast.makeText(contest, "You must login first", Toast.LENGTH_SHORT).show()
                    contest.startActivity(Intent(contest, LoginActivity::class.java))
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context,
        )

    override fun getItemCount() = cartList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindView(cartList, position)
    }
}
