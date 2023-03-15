package com.bizmiz.testtopshiriq.ui.home.orders

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bizmiz.testtopshiriq.R
import com.bizmiz.testtopshiriq.app.App
import com.bizmiz.testtopshiriq.databinding.FragmentOrdersBinding
import com.bizmiz.testtopshiriq.ui.home.orders.adapter.OrdersAdapter
import com.bizmiz.testtopshiriq.util.isUsingNightModeResources
import javax.inject.Inject


class OrdersFragment : Fragment(R.layout.fragment_orders) {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var ordersAdapter: OrdersAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)
        _binding = FragmentOrdersBinding.bind(view)
        if (isUsingNightModeResources(requireContext())) {
            setStatusBarBackground(0)
        } else {
            setStatusBarBackground(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }
        binding.ordersRecView.adapter = ordersAdapter
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setStatusBarBackground(appearance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
                appearance,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireActivity(), R.color.orders_color)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}