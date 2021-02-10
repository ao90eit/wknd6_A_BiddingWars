package com.aoinc.wknd6_a_biddingwars.view.MainNav

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.viewmodel.AuctionViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfilePageFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    // View model
//    private val auctionViewModel: AuctionViewModel by activityViewModels()
    // TODO: implement selling/buying lists, need queries from view model

    // Layout items
    private lateinit var profilePhotoImageView: ShapeableImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userIdTextView: TextView
    private lateinit var userEmailTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.profile_page_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilePhotoImageView = view.findViewById(R.id.profile_photo_imageView)
        userNameTextView = view.findViewById(R.id.profile_user_name_textView)
        userIdTextView = view.findViewById(R.id.profile_user_id_textView)
        userEmailTextView = view.findViewById(R.id.profile_email_textView)

        firebaseAuth.currentUser?.let {
            context?.let { con ->
                Glide.with(con)
                    .load(it.photoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profilePhotoImageView)
            }

            userNameTextView.text = it.displayName
            userIdTextView.text = getString(R.string.user_id_text, it.uid)
            userEmailTextView.text = getString(R.string.user_email_text, it.email)
        }
    }
}