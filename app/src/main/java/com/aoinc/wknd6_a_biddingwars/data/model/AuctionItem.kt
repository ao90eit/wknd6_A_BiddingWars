package com.aoinc.wknd6_a_biddingwars.data.model

import android.os.Parcel
import android.os.Parcelable

data class AuctionItem(// User input data
    val photoUri: String = "",
    val name: String = "",
    val description: String = "",
    val seller: String = "",   // TODO: use Firebase user id here (for now)

    // Data populated in auction list
    var currentBid: Double = 0.0,
    var lastBidder: String = "", // TODO: user id again (for now)
    var numBids: Int = 0,
    var isSold: Boolean = false,

    // Firebase data
    var idKey: String = ""
)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(photoUri)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(seller)
        parcel.writeDouble(currentBid)
        parcel.writeString(lastBidder)
        parcel.writeInt(numBids)
        parcel.writeByte(if (isSold) 1 else 0)
        parcel.writeString(idKey)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AuctionItem> {
        override fun createFromParcel(parcel: Parcel): AuctionItem {
            return AuctionItem(parcel)
        }

        override fun newArray(size: Int): Array<AuctionItem?> {
            return arrayOfNulls(size)
        }
    }

}
