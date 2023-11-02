package com.diu.mlab.foodie.zone.presentation.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.FragmentAddressBinding
import com.diu.mlab.foodie.zone.databinding.FragmentOrderInfoBinding
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.diu.mlab.foodie.zone.util.toDateTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderInfoFragment : Fragment() {

    private lateinit var binding : FragmentOrderInfoBinding

    private val viewModel by activityViewModels<OrderViewModel>()
    lateinit var orderInfo: OrderInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderInfoBinding.inflate(inflater, container, false)
        viewModel.orderInfo.observe(requireActivity()){
            orderInfo = it
            if(orderInfo.orderTime != 0L)
                binding.time.text = orderInfo.orderTime.toDateTime()
        }
        viewModel.progressInfoList.observe(requireActivity()){lst ->
            val adapter = ProgressListViewAdapter(lst)

            binding.precessRecyclerView.adapter = adapter
//            adapter.notifyDataSetChanged()

            if( lst.map{it.first}.contains("Food Delivered") && !lst.map{it.first}.contains("Food Received")) {
                binding.foodConfirmation.visibility = View.VISIBLE
            }

            if( lst.map{it.first}.contains("Canceled") || lst.map{it.first}.contains("Food Received"))
                binding.processingBar.visibility = View.INVISIBLE

        }
        binding.btnYes.setBounceClickListener {
            viewModel.updateOrderInfo(
                varBoolName = "userReceived",
                value = true,
                varTimeName = "userReceivedTime",
                shopEmail = orderInfo.shopInfo.email,
                runnerEmail = orderInfo.runnerInfo.email,
                success = {},
                failed = {}
            )
            binding.foodConfirmation.visibility = View.GONE
        }

        binding.btnNo.setBounceClickListener {
            viewModel.updateOrderInfo(
                varBoolName = "userReceived",
                value = false,
                varTimeName = "userReceivedTime",
                shopEmail = orderInfo.shopInfo.email,
                runnerEmail = orderInfo.runnerInfo.email,
                success = {},
                failed = {}
            )
            binding.foodConfirmation.visibility = View.GONE
        }

        return binding.root
    }

}