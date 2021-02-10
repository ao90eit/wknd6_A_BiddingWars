package com.aoinc.wknd6_a_biddingwars.view.AuctionItem

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth

class ItemBiddingFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

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
    private lateinit var bidAmountEditText: EditText
    private lateinit var bidButton: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.item_bidding_page_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoImageView = view.findViewById(R.id.bid_photo_imageView)
        nameTextView = view.findViewById(R.id.bid_name_textView)
        sellerTextView = view.findViewById(R.id.bid_seller_textView)
        currentPriceTextView = view.findViewById(R.id.bid_current_price_textView)
        lastBidderTextView = view.findViewById(R.id.bid_last_bidder_textView)
        totalBidsTextView = view.findViewById(R.id.bid_num_bids_textView)
        descriptionTextView = view.findViewById(R.id.bid_description_textView)
        bidAmountEditText = view.findViewById(R.id.bid_amount_editText)
        bidButton = view.findViewById(R.id.simple_bid_button)
    }

    override fun onResume() {
        super.onResume()

        arguments?.let { args ->
            auctionItem = (args.get(Constants.AUCTION_ITEM_ARG) as AuctionItem).also {

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
                bidAmountEditText.setText(String.format("%.2f", it.currentBid + 1))
                Log.d("TAG_X", String.format("%.2f", it.currentBid + 1))
                descriptionTextView.text = it.description

                bidButton.setOnClickListener { v ->
                    val newBid = bidAmountEditText.text.toString().toDouble()

                    if (isInputValid(it.currentBid, newBid)) {
                        it.numBids++
                        it.currentBid = newBid

                        firebaseAuth.currentUser?.let { user ->
                            it.lastBidder = user.displayName.toString()
                        }
                        auctionViewModel.updateAuctionItem(it)

                        context?.let { con ->
                            AppAlert.makeSimpleDialog(con,
                                getString(R.string.bid_amount_notify, newBid, it.name), getString(R.string.awesome),
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

    private fun isInputValid(curBid: Double, newBid: Double): Boolean {
        var inputErrorMsg = ""

        when {
            newBid < (curBid + 1) ->
                inputErrorMsg = getString(R.string.bid_too_low_alert)
        }

        if (inputErrorMsg.isNotBlank()) {
            context?.let { AppAlert.makeToast(it, inputErrorMsg) }
            return false
        }

        return true
    }
}