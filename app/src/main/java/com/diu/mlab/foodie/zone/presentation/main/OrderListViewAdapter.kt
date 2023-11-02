package com.diu.mlab.foodie.zone.presentation.main

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.ItemCartBinding
import com.diu.mlab.foodie.zone.databinding.ItemOrderBinding
import com.diu.mlab.foodie.zone.databinding.ItemProcessInfoBinding
import com.diu.mlab.foodie.zone.domain.model.CartItem
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.presentation.order.OrderActivity
import com.diu.mlab.foodie.zone.util.getDrawable

class OrderListViewAdapter(
    private val progressList: List<OrderInfo>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemOrderBinding,
        private val contest: Context,
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            list: List<OrderInfo>,
            position: Int
        ) {
            binding.nm.text = list[position].foodInfo.nm
            binding.shopNm.text = list[position].shopInfo.nm
            binding.price.text = (list[position].typePrice * list[position].quantity + list[position].deliveryCharge).toString()
            binding.quantity.text = list[position].quantity.toString()
            list[position].foodInfo.pic.getDrawable{ binding.pic.setImageDrawable(it) }

            if(list[position].isUserReceived)
                binding.orderStatusTxt.text = "Received"
            else if(!list[position].isUserReceived && list[position].userReceivedTime != 0L)
                binding.orderStatusTxt.text = "Not Received"
            else if(list[position].isFoodHandover2User)
                binding.orderStatusTxt.text = "Delivered"
            else if(list[position].isFoodHandover2RunnerNdPaid)
                binding.orderStatusTxt.text = "On The Way"
            else if(list[position].isCanceled)
                binding.orderStatusTxt.text = "Canceled"
            else if(list[position].isPaymentConfirmed)
                binding.orderStatusTxt.text = "Payment Successful"
            else if(!list[position].isPaymentConfirmed && list[position].paymentConfirmationTime != 0L)
                binding.orderStatusTxt.text = "Payment Failed"
            else if(list[position].isPaid)
                binding.orderStatusTxt.text = "Processing"
            else if(!list[position].isPaid && list[position].paymentTime == 0L)
                binding.orderStatusTxt.text = "Not Paid"
            else
                binding.orderStatusTxt.text = "Processing"



            if((!list[position].isPaymentConfirmed && list[position].paymentConfirmationTime != 0L) || list[position].isCanceled)
                binding.orderStatusCard.backgroundTintList = ColorStateList.valueOf(contest.getColor(R.color.redZ))
            if(list[position].isFoodHandover2User || list[position].isUserReceived)
                binding.orderStatusCard.backgroundTintList = ColorStateList.valueOf(contest.getColor(R.color.greenX))

            val bundle = Bundle()
            bundle.putString("shopEmail", list[position].shopInfo.email)
            bundle.putString("foodId", list[position].foodInfo.foodId)
            bundle.putString("currentType", list[position].type)
            bundle.putInt("currentTypePrice", list[position].typePrice)
            bundle.putInt("quantity", list[position].quantity)
            bundle.putBoolean("newOrder", false)
            bundle.putString("orderId", list[position].orderId)

            binding.btnViewOrder.setOnClickListener {
                contest.startActivity(Intent(contest, OrderActivity::class.java).putExtras(bundle))
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context,
        )

    override fun getItemCount() = progressList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindView(progressList, position)
    }
}
