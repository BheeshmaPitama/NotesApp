package com.anubhav.tiwari.notesapp.ui.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.anubhav.tiwari.notesapp.R
import com.anubhav.tiwari.notesapp.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment:BaseFragment(R.layout.fragment_auth) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnLogin.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToNotesFragment())

        }
    }
}