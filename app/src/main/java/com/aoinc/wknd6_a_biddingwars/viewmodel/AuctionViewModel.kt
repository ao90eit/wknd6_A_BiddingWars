package com.aoinc.wknd6_a_biddingwars.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aoinc.wknd6_a_biddingwars.data.model.AuctionItem
import com.aoinc.wknd6_a_biddingwars.data.repository.AuctionRepository

class AuctionViewModel : ViewModel() {

    private lateinit var addedAuctionItem: LiveData<AuctionItem>
    private lateinit var changedAuctionItem: LiveData<AuctionItem>

    fun initializeCallbackLiveData(callbackRefs: AuctionRepository.DatabaseCallbackRefItems) {
        addedAuctionItem = callbackRefs.addedAuctionItem
        changedAuctionItem = callbackRefs.changedAuctionItem
    }

    fun getAddedAuctionItem(): LiveData<AuctionItem> {
        if (this::addedAuctionItem.isInitialized)
            return addedAuctionItem

        initializeCallbackLiveData(AuctionRepository.getNewAuctionItem())
        return addedAuctionItem
    }

    fun getChangedAuctionItem(): LiveData<AuctionItem> {
        if (this::changedAuctionItem.isInitialized)
            return changedAuctionItem

        initializeCallbackLiveData(AuctionRepository.getNewAuctionItem())
        return changedAuctionItem
    }

    fun publishNewAuctionItem(auctionItem: AuctionItem) =
        AuctionRepository.publishNewAuctionItem(auctionItem)
}