package com.aoinc.wknd6_a_biddingwars.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aoinc.wknd6_a_biddingwars.data.model.AuctionItem
import com.aoinc.wknd6_a_biddingwars.util.Constants
import com.google.firebase.database.*

object AuctionRepository {
    val firebaseDatabase = FirebaseDatabase.getInstance()

    init {
        // store data and operations to on-device storage for persistence after app restart
        firebaseDatabase.setPersistenceEnabled(true)
    }

    class DatabaseCallbackRefItems {
        val addedAuctionItem: MutableLiveData<AuctionItem> = MutableLiveData()
        val changedAuctionItem: MutableLiveData<AuctionItem> = MutableLiveData()
    }

    // TODO: try to add incoming items to a list and post the values every 10 seconds - batch updates
    // Retrieve the latest live item data from the Firebase repo
    fun getNewAuctionItem(): DatabaseCallbackRefItems {
        val callbackRefs = DatabaseCallbackRefItems()

        firebaseDatabase.reference.child(Constants.AUCTION_ITEM_REF)
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(AuctionItem::class.java)?.let {
                        snapshot.key?.let { key -> it.idKey = key }
                        callbackRefs.addedAuctionItem.value = it
                    }

//                    Log.d("TAG_X", "ADDED ITEM -> ${callbackRefs.addedAuctionItem.value}")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(AuctionItem::class.java)?.let {
                        callbackRefs.changedAuctionItem.value = it
                    }

//                    Log.d("TAG_X", "CHANGED ITEM -> ${callbackRefs.changedAuctionItem.value}")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
//                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG_X", "items ChildEventListener error -> ${error.message}")
                }
            })

        return callbackRefs
    }

    // Push a new auction item to the Firebase repo
    fun publishNewAuctionItem (auctionItem: AuctionItem) {
        firebaseDatabase.reference.child(Constants.AUCTION_ITEM_REF).push().setValue(auctionItem)
//        Log.d("TAG_X", "Item Posted -> \n ${auctionItem.name}")
    }

    // Push a new auction item to the Firebase repo
    fun updateAuctionItem (auctionItem: AuctionItem) {
        firebaseDatabase.reference.child(Constants.AUCTION_ITEM_REF)
            .child(auctionItem.idKey).setValue(auctionItem)
//        Log.d("TAG_X", "Item Updated -> \n ${auctionItem.name}")
    }
}