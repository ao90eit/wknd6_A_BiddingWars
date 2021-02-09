package com.aoinc.wknd6_a_biddingwars.view.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.aoinc.wknd6_a_biddingwars.R
import com.aoinc.wknd6_a_biddingwars.util.AppAlert
import com.aoinc.wknd6_a_biddingwars.view.MainNav.MainNavActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Child fragments
    private val signUpFragment = SignUpFragment()

    // Layout items
    private lateinit var userEmailInput: EditText
    private lateinit var userPasswordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Link to layout items
        userEmailInput = findViewById(R.id.login_email_editText)
        userPasswordInput = findViewById(R.id.login_password_editText)
        loginButton = findViewById(R.id.login_button)
        signUpButton = findViewById(R.id.login_sign_up_text_button)

        loginButton.setOnClickListener {
            val email = userEmailInput.text.toString().trim()
            val password = userPasswordInput.text.toString().trim()

            if (isInputValid(email, password)) {

                firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                        if (it.isSuccessful) {
                            if (firebaseAuth.currentUser?.isEmailVerified == true) {
                                loadMainNavActivity()
                            } else {
                                AppAlert.makeToast(this,getString(R.string.verify_email_message),
                                    Toast.LENGTH_LONG)
                            }

                        } else {
                            AppAlert.makeToast(this,it.exception?.localizedMessage
                                ?: getString(R.string.unkown_login_error))
                        }
                    }
            }
        }

        signUpButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    android.R.anim.slide_out_right,
                    R.anim.slide_in_from_right,
                    android.R.anim.slide_out_right)
                .add(R.id.sign_up_fragment_container, signUpFragment)
                .addToBackStack(signUpFragment.tag)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()

        // FIXME: fix issue showing page transition on app start
        //      -> launch different page from base application?
        checkIfUserLoggedIn()
    }

    private fun checkIfUserLoggedIn() {
        firebaseAuth.currentUser?.let {
            if (it.isEmailVerified)
                loadMainNavActivity()
            else
                AppAlert.makeToast(this,getString(R.string.verify_email_message))
        }
    }

    private fun isInputValid(email: String, password: String): Boolean {
        var inputErrorMsg = ""

        when {
            email.isBlank() ->
                inputErrorMsg = getString(R.string.email_empty_message)

            password.isBlank() ->
                inputErrorMsg = getString(R.string.password_empty_message)
        }

        if (inputErrorMsg.isNotBlank()) {
            AppAlert.makeToast(this, inputErrorMsg)
            return false
        }

        return true
    }

    private fun loadMainNavActivity() {
        startActivity(Intent(this, MainNavActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}