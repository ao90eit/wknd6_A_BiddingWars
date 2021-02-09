package com.aoinc.wknd6_a_biddingwars.view.Login

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.util.AppAlert
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Layout items
    private lateinit var userEmailInput: EditText
    private lateinit var userPasswordInput: EditText
    private lateinit var signUpButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.sign_up_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Link to layout items
        userEmailInput = view.findViewById(R.id.sign_up_email_editText)
        userPasswordInput = view.findViewById(R.id.sign_up_password_editText)
        signUpButton = view.findViewById(R.id.sign_up_button)

        signUpButton.setOnClickListener {
            val email = userEmailInput.text.toString().trim()
            val password = userPasswordInput.text.toString().trim()

            if (isInputValid(email, password)) {

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                        if (it.isSuccessful) {
                            Log.d("TAG_X", "sign up successful")

                            if (firebaseAuth.currentUser?.isEmailVerified == true) {
                                // do nothing, this page closes, should log in from login page
                                //      -> really, this should also never happen...
                            } else
                                firebaseAuth.currentUser?.sendEmailVerification()

                            // pop sign up fragment on success
                            parentFragmentManager.popBackStack()

                        } else    // sign up failed
                            context?.let { con ->
                                AppAlert.makeToast(con, getString(R.string.sign_up_fail_msg, it.result),
                                    Toast.LENGTH_LONG) }
                    }
                clearInputFields()
            }
        }
    }

    private fun clearInputFields() {
        userEmailInput.text.clear()
        userPasswordInput.text.clear()
    }

    private fun isInputValid(email: String, password: String): Boolean {
        var inputErrorMsg = ""

        when {
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
}