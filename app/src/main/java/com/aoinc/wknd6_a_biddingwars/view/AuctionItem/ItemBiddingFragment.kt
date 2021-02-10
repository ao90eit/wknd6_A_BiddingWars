package com.aoinc.wknd6_a_biddingwars.view.AuctionItem

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.data.model.AuctionItem
import com.aoinc.wknd6_a_biddingwars.util.AppAlert
import com.aoinc.wknd6_a_biddingwars.util.Constants
import com.aoinc.wknd6_a_biddingwars.viewmodel.AuctionViewModel
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ItemBiddingFragment : Fragment() {

    // Firebase
//    private val firebaseAuth = FirebaseAuth.getInstance()

    // View model
    private val auctionViewModel: AuctionViewModel by activityViewModels()

    // Data
    private lateinit var auctionItem: AuctionItem

    // Layout items
    private lateinit var photoImageView: ShapeableImageView
    private lateinit var nameTextView: MaterialTextView
    private lateinit var sellerTextView: MaterialTextView
    private lateinit var currentPriceTextView: MaterialTextView
    private lateinit var lastBidderTextView: MaterialTextView
    private lateinit var totalBidsTextView: MaterialTextView
    private lateinit var descriptionTextView: MaterialTextView
    private lateinit var bidButton: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.item_bidding_page_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            auctionItem = (args.get(Constants.AUCTION_ITEM_ARG) as AuctionItem).also {
                photoImageView = view.findViewById(R.id.bid_photo_imageView)
                nameTextView = view.findViewById(R.id.bid_name_textView)
                sellerTextView = view.findViewById(R.id.bid_seller_textView)
                currentPriceTextView = view.findViewById(R.id.bid_current_price_textView)
                lastBidderTextView = view.findViewById(R.id.bid_last_bidder_textView)
                totalBidsTextView = view.findViewById(R.id.bid_num_bids_textView)
                descriptionTextView = view.findViewById(R.id.bid_description_textView)
                bidButton = view.findViewById(R.id.simple_bid_button)

                context?.let { con ->
                    Glide.with(con)
                        .load(it.photoUri)
                        .placeholder(R.drawable.ic_baseline_photo_24)
                        .into(photoImageView)
                }

                nameTextView.text = it.name
                sellerTextView.text = getString(R.string.bid_seller_label, it.seller)
                currentPriceTextView.text = getString(R.string.current_bid, it.currentBid)
                lastBidderTextView.text = getString(R.string.last_bidder_text, it.lastBidder)
                totalBidsTextView.text = getString(R.string.num_bids_display, it.numBids)
                descriptionTextView.text = it.description

                bidButton.setOnClickListener { v ->
                    it.numBids++
                    auctionViewModel.updateAuctionItem(it)

                    context?.let { con ->
                        AppAlert.makeSimpleDialog(con,
                        "You bid on ${it.name}", "AWESOME!",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                dialog.dismiss()
                                parentFragmentManager.popBackStack()
                            }
                        })
                    }
                }
            }
        }
    }
}