package com.aoinc.wknd6_a_biddingwars.data

data class AuctionItem(
    // User input data
    val photoUrl: String,
    val name: String,
    val description: String,
    val seller: String,   // TODO: use Firebase user id here (for now)

    // Data populated in auction list
    val lastBidder: String = "", // TODO: user id again (for now)
    val currentBid: Double = 0.0,
    val numBids: Int = 0,
    val isSold: Boolean = false
)
