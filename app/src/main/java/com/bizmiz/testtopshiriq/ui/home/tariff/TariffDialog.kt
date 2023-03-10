package com.bizmiz.testtopshiriq.ui.home.tariff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bizmiz.testtopshiriq.R
import com.bizmiz.testtopshiriq.databinding.TariffDialogBinding
import com.bizmiz.testtopshiriq.ui.home.HomeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TariffDialog(mFragment: HomeFragment) : BottomSheetDialogFragment() {

    private lateinit var binding: TariffDialogBinding
    private var savedViewInstance: View? = null

    init {
        show(mFragment.requireActivity().supportFragmentManager, "tag")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return if (savedInstanceState != null) {
            savedViewInstance
        } else {
            savedViewInstance =
                inflater.inflate(R.layout.tariff_dialog, container, true)
            savedViewInstance
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TariffDialogBinding.bind(view)
    }
}

