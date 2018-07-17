package jp.gcreate.product.filteredhatebu.ui.option

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import jp.gcreate.product.filteredhatebu.BuildConfig
import jp.gcreate.product.filteredhatebu.databinding.FragmentOptionBinding

/**
 * Copyright 2018 G-CREATE
 */
class OptionsFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentOptionBinding.inflate(inflater, container, false)
        binding.ossLicense.setOnClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
        }
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        showAppVersion()
    }
    
    private fun showAppVersion() {
        binding.versionName.text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
    }
}