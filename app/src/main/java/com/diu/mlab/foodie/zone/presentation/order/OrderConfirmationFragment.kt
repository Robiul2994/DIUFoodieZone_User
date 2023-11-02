package com.diu.mlab.foodie.zone.presentation.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.FragmentCartBinding
import com.diu.mlab.foodie.zone.databinding.FragmentOrderConfirmationBinding
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.presentation.main.UserMainViewModel
import com.diu.mlab.foodie.zone.util.getDeliveryCharge
import com.diu.mlab.foodie.zone.util.getDrawable
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment() {
    private lateinit var binding : FragmentOrderConfirmationBinding
    private var orderInfo = OrderInfo()

    private val viewModel by activityViewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)

        viewModel.orderInfo.observe(requireActivity()){ info->
            orderInfo = info
            var totalPrice = orderInfo.typePrice * orderInfo.quantity
            orderInfo.deliveryCharge = totalPrice.getDeliveryCharge()
            totalPrice += orderInfo.deliveryCharge
            if(orderInfo.foodInfo.nm.isNotEmpty()){
                binding.foodInfo.nm.text = orderInfo.foodInfo.nm
                binding.foodInfo.time.text = orderInfo.foodInfo.time
                binding.foodInfo.priceCard.visibility = View.GONE

                orderInfo.foodInfo.pic.getDrawable { binding.foodInfo.pic.setImageDrawable(it) }
                binding.type.text = orderInfo.type
                binding.unitPrice.text = orderInfo.typePrice.toString()
                binding.quantity.text = orderInfo.quantity.toString()
                binding.charge.text = orderInfo.deliveryCharge.toString()
                binding.totalPrice.text = totalPrice.toString()
                binding.address.text = orderInfo.userInfo.loc
            }
        }

        binding.btnUpdateLocation.setBounceClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .run {
                    addToBackStack("OrderConfirmationFragment")
                    hide(this@OrderConfirmationFragment)
                    add(R.id.orderFragment, AddressFragment())
                    commit()
                }
        }

        binding.btnOrder.setBounceClickListener{
            viewModel.placeOrder(orderInfo){
                MainScope().launch {
                    Toast.makeText(requireContext(), "Server Error", Toast.LENGTH_SHORT).show()
                }
            }
            requireActivity().supportFragmentManager
                .beginTransaction()
                .run {
                    hide(this@OrderConfirmationFragment)
                    add(R.id.orderFragment, PaymentFragment())
                    commit()
                }
        }

        return binding.root
    }
}