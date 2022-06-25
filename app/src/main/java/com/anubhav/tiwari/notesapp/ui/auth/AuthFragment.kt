package com.anubhav.tiwari.notesapp.ui.auth

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.anubhav.tiwari.notesapp.R
import com.anubhav.tiwari.notesapp.data.remote.BasicAuthInterceptor
import com.anubhav.tiwari.notesapp.ui.BaseFragment
import com.anubhav.tiwari.notesapp.utils.Constants.KEY_LOGGED_IN_EMAIL
import com.anubhav.tiwari.notesapp.utils.Constants.KEY_PASSWORD
import com.anubhav.tiwari.notesapp.utils.Constants.NO_EMAIL
import com.anubhav.tiwari.notesapp.utils.Constants.NO_PASSWORD
import com.anubhav.tiwari.notesapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment:BaseFragment(R.layout.fragment_auth) {

    private val viewModel:AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private var currentEmail: String? = null
    private var currentPassword: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isLoggedIn()){
            redirectLogin()
        }

            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            subscribeToObservers()

            btnRegister.setOnClickListener {
                val email = etRegisterEmail.text.toString()
                val password = etRegisterPassword.text.toString()
                val confirmedPassword = etRegisterPasswordConfirm.text.toString()
                viewModel.register(email,password,confirmedPassword)
            }

            btnLogin.setOnClickListener{
                val email = etLoginEmail.text.toString()
                val password = etLoginPassword.text.toString()
                currentEmail = email
                currentPassword = password
                viewModel.login(email,password)
            }

    }

    private fun authenticateAPI(email:String,password:String){
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }

    private fun redirectLogin(){
        val navOption = NavOptions.Builder().setPopUpTo(R.id.authFragment,true).build()
        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToNotesFragment(),
            navOption
        )
    }

    private fun isLoggedIn() : Boolean{
        currentEmail = sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL)?: NO_EMAIL
        currentPassword = sharedPreferences.getString(KEY_PASSWORD, NO_PASSWORD)?: NO_PASSWORD
        return currentEmail!= NO_EMAIL && currentPassword!= NO_PASSWORD
    }

    private fun subscribeToObservers(){
        viewModel.loginStatus.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        loginProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully Logged in!")
                        sharedPreferences.edit().putString(KEY_LOGGED_IN_EMAIL,currentEmail).apply()
                        sharedPreferences.edit().putString(KEY_PASSWORD,currentEmail).apply()
                        authenticateAPI(currentEmail?:"",currentPassword?:"")
                        redirectLogin()
                    }
                    Status.ERROR -> {
                        loginProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occurred!")
                    }
                    Status.LOADING -> {
                        loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.registerStatus.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully Registered an Account!")
                    }
                    Status.ERROR -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "Unknown Error Occurred!")
                    }
                    Status.LOADING -> {
                        registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}