package com.aoinc.wknd6_a_biddingwars.view.Login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.util.AppAlert
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class SignUpFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Photo storage
    private var photoStoragePath: String? = null
    private var photoBitmap: Bitmap? = null
    private val CAMERA_REQUEST_CODE = 321

    // Layout items
    private lateinit var photoImageView: ShapeableImageView
    private lateinit var userPhotoButton: ImageButton
    private lateinit var userNameInput: EditText
    private lateinit var userEmailInput: EditText
    private lateinit var userPasswordInput: EditText
    private lateinit var signUpButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.sign_up_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Link to layout items
        photoImageView = view.findViewById(R.id.sign_up_photo_imageView)
        userPhotoButton = view.findViewById(R.id.sign_up_photo_button)
        userNameInput = view.findViewById(R.id.sign_up_user_name_editText)
        userEmailInput = view.findViewById(R.id.sign_up_email_editText)
        userPasswordInput = view.findViewById(R.id.sign_up_password_editText)
        signUpButton = view.findViewById(R.id.sign_up_button)

        userPhotoButton.setOnClickListener {
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

        signUpButton.setOnClickListener {
            val userName = userNameInput.text.toString().trim()
            val email = userEmailInput.text.toString().trim()
            val password = userPasswordInput.text.toString().trim()

            if (isInputValid(userName, email, password)) {
                photoBitmap?.let {
                    createUser(userName, email, password)
                } ?: context?.let {
                    AppAlert.makeToast(it, getString(R.string.error_no_item_photo))
                }
            }
        }
    }

    private fun createUser(userName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TAG_X", "sign up successful")

                    if (firebaseAuth.currentUser?.isEmailVerified == true) {
                        // do nothing, this page closes, should log in from login page
                        //      -> really, this should also never happen...
                    } else {
                        context?.let { con ->
                            AppAlert.makeToast(con, getString(R.string.verification_email_alert))
                        }

                        firebaseAuth.currentUser?.let { curUser ->
                            curUser.sendEmailVerification()

                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build()

                            curUser.updateProfile(profileUpdates)
                                .addOnFailureListener{ e ->
                                    Log.d("TAG_X", "USER NAME UPDATE ERROR -> ${e.localizedMessage}")
                                }
                        }

                        // upload saved profile photo to Firebase
                        uploadProfilePhoto()
                    }

                    // pop sign up fragment on success
                    parentFragmentManager.popBackStack()

                    // tidy up
                    clearInputFields()

            } else    // sign up failed
                context?.let { con ->
                    AppAlert.makeToast(con, getString(R.string.sign_up_fail_msg, it.result),
                        Toast.LENGTH_LONG)
                }
        }
    }

    private fun uploadProfilePhoto() {
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
                            // update user profile with result photo uri
                            updateProfilePhoto(dlTask.result)
                        } else Log.e("TAG_X", "photo DOWNload error")
                    }

                } else Log.e("TAG_X", "photo UPload error")
            }
        }
    }

    private fun updateProfilePhoto(uri: Uri?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()

        firebaseAuth.currentUser?.let { curUser ->
            curUser.updateProfile(profileUpdates)
                .addOnFailureListener{ e ->
                    Log.d("TAG_X", "USER PHOTO UPDATE ERROR -> ${e.localizedMessage}")
                }
        }
    }

    private fun createTemporaryFile(): File? {
        val tempName = "temp_profile_${Date().time}"
        val tempDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val imageFile = File.createTempFile(tempName, ".jpg", tempDir)
        imageFile.deleteOnExit()

        photoStoragePath = imageFile.absolutePath
        return imageFile
    }

    private fun clearInputFields() {
        userEmailInput.text.clear()
        userPasswordInput.text.clear()
    }

    private fun isInputValid(userName: String, email: String, password: String): Boolean {
        var inputErrorMsg = ""

        when {
            userName.isBlank() ->
                inputErrorMsg = getString(R.string.sign_up_user_name_error)

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                inputErrorMsg = getString(R.string.sign_up_email_invalid)

            // TODO: ideally, use regex to check password, but need to know Firebase's password requirements...
            password.length < 6 ->
                inputErrorMsg = getString(R.string.sign_up_password_too_short)
        }

        if (inputErrorMsg.isNotBlank()) {
            context?.let { AppAlert.makeToast(it, inputErrorMsg) }
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE) {
            
            // get temporary local photo for in-app display (sent to firebase on publish)
            photoBitmap = BitmapFactory.decodeFile(photoStoragePath)
            photoBitmap?.let {
                photoImageView.setImageBitmap(it).also {
                    userPhotoButton.visibility = View.GONE
                }
            }
        }
    }
}