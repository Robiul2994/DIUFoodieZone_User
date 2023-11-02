package com.diu.mlab.foodie.zone.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.zone.databinding.FragmentHomeBinding
import com.diu.mlab.foodie.zone.domain.model.ShopProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by activityViewModels<UserMainViewModel>()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val shopList = mutableListOf<ShopProfile>()
        val adapter = ShopListViewAdapter(shopList)
        binding.shopRecyclerView.adapter = adapter

        viewModel.shopProfileList.observe(viewLifecycleOwner){
            shopList.clear()
            shopList.addAll(it)
            Log.d("TAG", "observe: new food")
            adapter.notifyDataSetChanged()
        }

        viewModel.getShopProfileList{
            MainScope().launch{
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}