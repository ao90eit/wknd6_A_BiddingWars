package com.aoinc.wknd6_a_biddingwars.view.AddItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.data.model.AuctionItem
import com.aoinc.wknd6_a_biddingwars.util.AppAlert
import com.aoinc.wknd6_a_biddingwars.viewmodel.AuctionViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class AddAuctionItemFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    // View model
    private val auctionViewModel: AuctionViewModel by activityViewModels()

    // Layout items
    private lateinit var photoImageView: ShapeableImageView
    private lateinit var addPhotoButton: ImageButton
    private lateinit var nameEditTextView: TextInputEditText
    private lateinit var descriptionEditTextView: TextInputEditText
    private lateinit var publishButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.add_auction_item_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find layout items
        photoImageView = view.findViewById(R.id.add_photo_imageView)
        addPhotoButton = view.findViewById(R.id.add_photo_button)
        nameEditTextView = view.findViewById(R.id.add_name_editText)
        descriptionEditTextView = view.findViewById(R.id.add_description_editText)
        publishButton = view.findViewById(R.id.add_publish_button)

        addPhotoButton.setOnClickListener {
            // TODO: implement camera intent
        }

        publishButton.setOnClickListener {
            // TODO: add photo url after camera result is returned
            val photoUrl = "FIX ME LATER"
            val name = nameEditTextView.text.toString().trim()
            val description = descriptionEditTextView.text.toString().trim()

            if (isInputValid(photoUrl, name, description)) {
                firebaseAuth.currentUser?.let{
                    val item = AuctionItem(
                        photoUrl,
                        name,
                        description,
                        it.uid,
                        0.00)

                    auctionViewModel.publishNewAuctionItem(item)
                        .also{
                            clearInputFields()
                            parentFragmentManager.popBackStack()
                        }
                }
            }
        }
    }

    private fun clearInputFields() {
        photoImageView.setImageResource(0)
        nameEditTextView.text?.clear()
        descriptionEditTextView.text?.clear()
    }

    private fun isInputValid(photoUrl: String, name: String, description: String): Boolean {
        var inputErrorMsg = ""

        when {
            photoUrl.isNullOrBlank() ->
                inputErrorMsg = getString(R.string.error_no_item_photo)

            name.isBlank() ->
                inputErrorMsg = getString(R.string.error_no_item_name)

            description.isBlank() ->
                inputErrorMsg = getString(R.string.error_no_item_description)
        }

        if (inputErrorMsg.isNotBlank()) {
            context?.let { AppAlert.makeToast(it, inputErrorMsg, Toast.LENGTH_LONG) }
            return false
        }

        return true
    }
}