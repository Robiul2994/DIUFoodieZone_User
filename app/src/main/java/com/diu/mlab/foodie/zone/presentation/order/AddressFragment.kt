package com.diu.mlab.foodie.zone.presentation.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.FragmentAddressBinding
import com.diu.mlab.foodie.zone.databinding.FragmentOrderConfirmationBinding
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
@AndroidEntryPoint
class AddressFragment : Fragment() {
    private lateinit var binding : FragmentAddressBinding

    private val viewModel by activityViewModels<OrderViewModel>()
    private var orderInfo = OrderInfo()
    private var locList = emptyList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)

        viewModel.orderInfo.observe(requireActivity()){
            orderInfo = it
            binding.pnNo.setText(it.userInfo.phone)
            if(it.userInfo.loc.isNotEmpty()){
                locList =  it.userInfo.loc.split(", ")
                if(locList.size>2) {
                    binding.buildingNm.setText(locList[2], false)
                    binding.floorNo.setText(locList[1], false)
                    binding.roomNo.setText(locList[0])
                }
            }
        }

        binding.btnUpdateLoc.setBounceClickListener {
            val buildingNm = binding.buildingNm.text.toString()
            val floorNo = binding.floorNo.text.toString()
            val roomNo = binding.roomNo.text.toString()
            val pnNo = binding.pnNo.text.toString()

            val loc = "$roomNo, $floorNo, $buildingNm"

            if(buildingNm.isNotEmpty() &&
                floorNo.isNotEmpty() &&
                roomNo.isNotEmpty() &&
                pnNo.isNotEmpty()
            ){
                viewModel.updateUserProfile(
                    orderInfo.userInfo.copy(loc = loc, phone = pnNo)
                ){
                     MainScope().launch {
                         Toast.makeText(requireContext(), "Server Error", Toast.LENGTH_SHORT).show()
                     }
                }
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }else{
                Toast.makeText(requireContext(), "Can't be empty", Toast.LENGTH_SHORT).show()
            }

        }
        return binding.root
    }

}