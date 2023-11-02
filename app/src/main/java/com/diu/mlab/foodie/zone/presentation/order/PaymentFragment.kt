package com.diu.mlab.foodie.zone.presentation.order

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.zone.R
import com.diu.mlab.foodie.zone.databinding.FragmentPaymentBinding
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.util.getDrawable
import com.diu.mlab.foodie.zone.util.setBounceClickListener
import com.jem.fliptabs.FlipTab
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private lateinit var binding : FragmentPaymentBinding
    private var orderInfo = OrderInfo()
    private var paymentType = "Bkash"

    private val viewModel by activityViewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        viewModel.orderInfo.observe(requireActivity()){info->
            orderInfo = info
            binding.pnNo.setText(info.userInfo.phone)
            binding.shopNm.text = info.shopInfo.nm
            binding.shopNm2.text = info.shopInfo.nm
            binding.shopBkashNo.text = info.shopInfo.phone
            binding.shopBkashNo.setOnClickListener {
                Toast.makeText(context, "Bkash Number copied", Toast.LENGTH_SHORT).show()
                val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("bkash", info.shopInfo.phone)
                clipboard.setPrimaryClip(clip)
            }
            binding.money.text = "${info.typePrice * info.quantity + info.deliveryCharge}"
            binding.paymentType.text = info.shopInfo.paymentType
            info.shopInfo.qr.getDrawable { binding.qr.setImageDrawable(it) }
        }

        binding.flipTab.setTabSelectedListener(object: FlipTab.TabSelectedListener {
            override fun onTabSelected(isLeftTab: Boolean, tabTextValue: String) {
                if (isLeftTab){
                    viewModel.setLeftTabSelected(true)
                    binding.bkashPaymentLayout.visibility = View.VISIBLE
                    binding.oneCardPaymentLayout.visibility = View.GONE
                }else{
                    viewModel.setLeftTabSelected(false)
                    binding.bkashPaymentLayout.visibility = View.GONE
                    binding.oneCardPaymentLayout.visibility = View.VISIBLE
                }
            }
            override fun onTabReselected(isLeftTab: Boolean, tabTextValue: String) {}
        })

        viewModel.isLeftTabSelected.observe(requireActivity()){
            if (it) {
                binding.flipTab.selectLeftTab(false)
                binding.bkashPaymentLayout.visibility = View.VISIBLE
                binding.oneCardPaymentLayout.visibility = View.GONE
                paymentType = "Bkash"
            }
            else {
                binding.flipTab.selectRightTab(false)
                binding.bkashPaymentLayout.visibility = View.GONE
                binding.oneCardPaymentLayout.visibility = View.VISIBLE
                paymentType = "1Card"
            }
        }

        binding.btnPaymentDone.setBounceClickListener {
            viewModel.updateOrderInfo(
                varBoolName = "paid",
                value = true,
                varTimeName = "paymentTime",
                shopEmail = orderInfo.shopInfo.email,
                success = {},
                failed = {}
            )
            viewModel.updatePaymentType(
                type = paymentType,
                shopEmail = orderInfo.shopInfo.email,
                success = {},
                failed = {}
            )
            val newPn = binding.pnNo.text.toString()
            if(orderInfo.userInfo.phone != newPn && newPn.isNotEmpty()){
                viewModel.updateUserProfile(orderInfo.userInfo.copy(phone = newPn)){}
            }
            requireActivity().supportFragmentManager
                .beginTransaction()
                .run {
                    hide(this@PaymentFragment)
                    add(R.id.orderFragment, OrderInfoFragment())
                    commit()
                }
        }
        return binding.root
    }

}