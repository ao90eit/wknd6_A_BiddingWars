package com.aoinc.wknd6_a_biddingwars.view.AuctionItem

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.FileProvider
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
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.util.*

class AddAuctionItemFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    // View model
    private val auctionViewModel: AuctionViewModel by activityViewModels()
    
    // Photo storage
    private var photoStoragePath: String? = null
    private var photoBitmap: Bitmap? = null
    private val CAMERA_REQUEST_CODE = 123

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
            // create implicit camera intent
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context?.let { con ->
                if (cameraIntent.resolveActivity(con.packageManager) != null) {

                    try {
                        // create temp file for photo storage
                        val tempFile = createTemporaryFile()
                        tempFile?.let { file ->
                            // get the image uri for the created file with proper authority
                            val imageUri = FileProvider.getUriForFile(
                                con, getString(R.string.file_provider_authority), file
                            )

                            /* set camera intent to output media to temp file location
                            * the image uri contains the token data the camera needs to verify the storage access
                            * (possibly perceive this backwards, but it works) */
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                            // start camera activity and await result
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                        }
                    } catch (e: Exception) {
                        Log.e("TAG_X", "CAMERA INTENT ERROR -> ${e.localizedMessage}")
                    }
                }
            }
        }

        // attempts to upload photo to Firebase return its Uri value
        publishButton.setOnClickListener {
            val name = nameEditTextView.text.toString().trim()
            val description = descriptionEditTextView.text.toString().trim()

            // validate text input, then also alert if no photo added
            if (isInputValid(name, description)) {

                photoBitmap?.let { photo ->
                    // prep photo for Firebase - compress and convert to byte array
                    val byteOutputStream = ByteArrayOutputStream()
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, byteOutputStream)
                    val imageBytes = byteOutputStream.toByteArray()

                    // create unique storage reference for Firebase
                    val userId = firebaseAuth.currentUser?.uid ?: "default"
                    val timestamp = Date().time
                    val storageReference = FirebaseStorage.getInstance()
                        .reference.child("$userId/$timestamp.jpeg")

                    // create upload task with storage reference and put compressed image item there
                    val uploadTask = storageReference.putBytes(imageBytes)
                    // listen for results
                    uploadTask.addOnCompleteListener {

                        if (it.isSuccessful) {
                            storageReference.downloadUrl.addOnCompleteListener { dlTask ->
                                if (dlTask.isSuccessful) {
                                    publishItem(name, description, dlTask.result)
                                } else Log.e("TAG_X", "photo DOWNload error")
                            }

                        } else Log.e("TAG_X", "photo UPload error")
                    }

                } ?: context?.let {
                    AppAlert.makeToast(it, getString(R.string.error_no_item_photo))
                }
            }
        }
    }

    // runs after photo is uploaded to Firebase and string Uri is returned
    private fun publishItem(name: String, description: String, firebasePhotoUri: Uri?) {
        val photoUri = firebasePhotoUri.toString()
        val item = AuctionItem(
            photoUri, name, description,
            firebaseAuth.currentUser?.displayName.toString(),
            0.00)

        auctionViewModel.publishNewAuctionItem(item)
            .also{
                clearInputFields()
                parentFragmentManager.popBackStack()
            }
    }

    private fun createTemporaryFile(): File? {
        val tempName = "${firebaseAuth.currentUser?.uid}_${Date().time}"
        val tempDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val imageFile = File.createTempFile(tempName, ".jpg", tempDir)
        imageFile.deleteOnExit()

        photoStoragePath = imageFile.absolutePath
        return imageFile
    }

    private fun clearInputFields() {
        photoImageView.setImageResource(0)
        addPhotoButton.visibility = View.VISIBLE
        nameEditTextView.text?.clear()
        descriptionEditTextView.text?.clear()
    }

    private fun isInputValid(name: String, description: String): Boolean {
        var inputErrorMsg = ""

        when {
            name.isBlank() ->
                inputErrorMsg = getString(R.string.error_no_item_name)

            description.isBlank() ->
                inputErrorMsg = getString(R.string.error_no_item_description)
        }

        if (inputErrorMsg.isNotBlank()) {
            context?.let { AppAlert.makeToast(it, inputErrorMsg) }
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && data != null) {
            // get temporary local photo for in-app display (sent to firebase on publish)
            photoBitmap = BitmapFactory.decodeFile(photoStoragePath)
            photoBitmap?.let {
                photoImageView.setImageBitmap(it).also {
                    addPhotoButton.visibility = View.GONE
                }
            }
        }
    }
}