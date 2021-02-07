package com.aoinc.wknd6_a_biddingwars.view.MainNav.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aoinc.wknd6_a_biddingwars.view.MainNav.AuctionPageFragment
import com.aoinc.wknd6_a_biddingwars.view.MainNav.MainNavActivity
import com.aoinc.wknd6_a_biddingwars.view.MainNav.ProfilePageFragment

class MainFragmentsAdapter(mainNavActivity: MainNavActivity)
    : FragmentStateAdapter(mainNavActivity) {

    private val auctionPageFragment = AuctionPageFragment()
    private val profilePageFragment = ProfilePageFragment()

    // known number of fragments, does not change
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> auctionPageFragment
            else -> profilePageFragment
        }
    }
}