package com.aoinc.wknd6_a_biddingwars.data.model

data class AuctionItem(
    // User input data
    val photoUrl: String = "",
    val name: String = "",
    val description: String = "",
    val seller: String = "",   // TODO: use Firebase user id here (for now)

    // Data populated in auction list
    var lastBidder: String = "", // TODO: user id again (for now)
    var currentBid: Double = 0.0,
    var numBids: Int = 0,
    var isSold: Boolean = false,

    // Firebase data
    var idKey: String = ""
)
