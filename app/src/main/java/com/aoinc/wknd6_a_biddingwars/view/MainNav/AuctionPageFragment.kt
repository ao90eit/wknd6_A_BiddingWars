package com.aoinc.wknd6_a_biddingwars.view.MainNav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.data.model.AuctionItem
import com.aoinc.wknd6_a_biddingwars.data.repository.AuctionRepository
import com.aoinc.wknd6_a_biddingwars.view.AddItem.AddAuctionItemFragment
import com.aoinc.wknd6_a_biddingwars.view.MainNav.adapter.AuctionRecyclerAdapter
import com.aoinc.wknd6_a_biddingwars.viewmodel.AuctionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class AuctionPageFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()
    var currentUserId = ""

    // View model
    private val auctionViewModel: AuctionViewModel by activityViewModels()

    // Fragments
    private val addAuctionItemFragment = AddAuctionItemFragment()

    // Layout views
    private lateinit var auctionRecyclerView: RecyclerView
    private val auctionRecyclerAdapter = AuctionRecyclerAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.auction_page_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth.currentUser?.let { currentUserId = it.uid }

        auctionRecyclerView = view.findViewById(R.id.auction_items_recyclerView)
        auctionRecyclerView.adapter = auctionRecyclerAdapter

        // TEST ITEMS
//        val testItems1 = mutableListOf(
//            AuctionItem("https://i.ytimg.com/vi/xbNuhPIwjjc/maxresdefault.jpg",
//                "Spiky Balls",
//                "Some balls",
//                currentUserId
//            ),
//            AuctionItem("https://i.ytimg.com/vi/CddOyuyu_hc/maxresdefault.jpg",
//                "Things Things Things",
//                "musical album",
//                currentUserId,
//                isSold = true
//            )
//        )

//        for (t in testItems1)
//            auctionViewModel.publishNewAuctionItem(t)

//        view.findViewById<Button>(R.id.test_push_button).setOnClickListener {
////            for (t in testItems1)
////                auctionViewModel.publishNewAuctionItem(t)
//
//            for (i in auctionRecyclerAdapter.getItemList()) {
//                i.isSold = !i.isSold
//                AuctionRepository.updateAuctionItem(i)
//            }
//        }
        // END TESTS

        // '+' button event
        view.findViewById<FloatingActionButton>(R.id.add_item_fab).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_bottom,
                    R.anim.slide_out_to_bottom,
                    R.anim.slide_in_from_bottom,
                    R.anim.slide_out_to_bottom
                )
                .add(R.id.add_item_fragment_container, addAuctionItemFragment)
                .addToBackStack(addAuctionItemFragment.tag)
                .commit()
        }

        auctionViewModel.getAddedAuctionItem().observe(viewLifecycleOwner, {
//            Log.d("TAG_X", "observed added -> $it")
            auctionRecyclerAdapter.insertSingleItem(it)
        })

        auctionViewModel.getChangedAuctionItem().observe(viewLifecycleOwner, {
//            Log.d("TAG_X", "observed updated -> $it")
            val position: Int = auctionRecyclerAdapter.getItemList().indexOfFirst {
                    i -> i.idKey == it.idKey
            }
            auctionRecyclerAdapter.updateSingleItem(it, position)
        })
    }
}