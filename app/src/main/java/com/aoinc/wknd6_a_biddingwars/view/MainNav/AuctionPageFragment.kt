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
import com.aoinc.wknd6_a_biddingwars.view.MainNav.adapter.AuctionRecyclerAdapter
import com.aoinc.wknd6_a_biddingwars.viewmodel.AuctionViewModel
import com.google.firebase.auth.FirebaseAuth

class AuctionPageFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()
    var currentUserId = ""

    // View model
    private val auctionViewModel: AuctionViewModel by activityViewModels()

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

        // TEST ITEMS
        val testItems1 = mutableListOf(
            AuctionItem("https://i.ytimg.com/vi/xbNuhPIwjjc/maxresdefault.jpg",
                "Spiky Balls",
                "Some balls",
                currentUserId
            ),
            AuctionItem("https://i.ytimg.com/vi/CddOyuyu_hc/maxresdefault.jpg",
                "Things Things Things",
                "musical album",
                currentUserId,
                isSold = true
            )
        )

        val testItems2 = mutableListOf(
            AuctionItem("https://blog.sfgate.com/soccer/files/2015/04/5-things-you-need-to-know-about-body-waxing.jpg",
                "5 Surprise Things",
                "Self explanatory",
                currentUserId
            ),
            AuctionItem("https://curlyqtop.files.wordpress.com/2013/08/things-that-are-orange-021.jpg",
                "A Pretty Flower",
                "Very pretty, smells nice",
                currentUserId,
                isSold = true
            )
        )
        // END TEST ITEMS

//        auctionRecyclerAdapter.updateAllItems(testItems1)
        auctionRecyclerView.adapter = auctionRecyclerAdapter


        // MORE TESTS
//        for (t in testItems1)
//            auctionViewModel.publishNewAuctionItem(t)

        view.findViewById<Button>(R.id.test_push_button).setOnClickListener {
            for (t in testItems2)
                auctionViewModel.publishNewAuctionItem(t)
        }
        // END TESTS

        auctionViewModel.getAddedAuctionItem().observe(viewLifecycleOwner, {
            Log.d("TAG_X", "observed added -> $it")
            auctionRecyclerAdapter.insertSingleItem(it)
        })
    }
}