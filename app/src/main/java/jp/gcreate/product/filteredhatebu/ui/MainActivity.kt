package jp.gcreate.product.filteredhatebu.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.android.support.DaggerAppCompatActivity
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.ActivityMainBinding
import timber.log.Timber

/**
 * Copyright 2018 G-CREATE
 */
class MainActivity : DaggerAppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        
        setSupportActionBar(binding.toolbar)
        
        navController = findNavController(R.id.contents_container)
        
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        
        binding.bottomNavigation.apply {
            setOnNavigationItemReselectedListener { item ->
                Timber.d("itemReselectedListener: $item")
            }
        }
    }
}