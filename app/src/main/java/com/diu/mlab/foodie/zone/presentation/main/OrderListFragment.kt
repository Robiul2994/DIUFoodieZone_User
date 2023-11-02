package com.diu.mlab.foodie.zone.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.FragmentCartBinding
import com.diu.mlab.foodie.zone.databinding.FragmentOrderListBinding
import com.diu.mlab.foodie.zone.presentation.order.OrderViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class OrderListFragment : Fragment() {

    private lateinit var binding : FragmentOrderListBinding
    private val viewModel by activityViewModels<OrderViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderListBinding.inflate(inflater, container, false)

        viewModel.getMyOrderList {
            MainScope().launch {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.myOrderList.observe(viewLifecycleOwner){
            binding.orderRecyclerView.adapter = OrderListViewAdapter(it)
        }
        return binding.root
    }
}