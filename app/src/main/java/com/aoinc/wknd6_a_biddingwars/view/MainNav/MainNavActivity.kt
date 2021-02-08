package com.aoinc.wknd6_a_biddingwars.view.MainNav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.view.MainNav.adapter.MainFragmentsAdapter
import com.aoinc.wknd6_a_biddingwars.viewmodel.AuctionViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainNavActivity : AppCompatActivity() {

    // View model
    private val auctionViewModel: AuctionViewModel by viewModels()

    // Layout views
    private lateinit var viewPager: ViewPager2
    private lateinit var navMenu: BottomNavigationView

    // ViewPager2 adapter
    private lateinit var mainFragmentsAdapter: MainFragmentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)

        // Link to layout items
        navMenu = findViewById(R.id.main_nav_bottomNavigationView)
        viewPager = findViewById(R.id.main_nav_viewPager2)

        // Set fragments adapter
        mainFragmentsAdapter = MainFragmentsAdapter(this)
        viewPager.adapter = mainFragmentsAdapter

        // Link pages to navigation
        navMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.auction_page_menu_item -> loadFragment(0)
                else -> loadFragment(1)
            }
            true
        }

        viewPager.registerOnPageChangeCallback( object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                navMenu.selectedItemId = when (position) {
                    0 -> R.id.auction_page_menu_item
                    else -> R.id.profile_page_menu_item
                }
            }
        })
    }

    private fun loadFragment(fragmentId: Int) {
        viewPager.currentItem = fragmentId
    }
}