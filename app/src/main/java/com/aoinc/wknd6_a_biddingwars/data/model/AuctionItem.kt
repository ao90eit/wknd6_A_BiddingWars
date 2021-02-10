package com.aoinc.wknd6_a_biddingwars.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuctionItem(
    // User input data
    val photoUri: String = "",
    val name: String = "",
    val description: String = "",
    val seller: String = "",

    // Data populated in auction list
    var currentBid: Double = 0.0,
    var lastBidder: String = "",
    var numBids: Int = 0,
    var isSold: Boolean = false,

    // Firebase data
    var idKey: String = ""
): Parcelable