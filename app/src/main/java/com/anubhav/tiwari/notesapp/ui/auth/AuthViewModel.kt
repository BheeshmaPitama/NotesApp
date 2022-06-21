package com.anubhav.tiwari.notesapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anubhav.tiwari.notesapp.repositories.NoteRepository
import com.anubhav.tiwari.notesapp.utils.Resource
import com.anubhav.tiwari.notesapp.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: NoteRepository
):ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus : LiveData<Resource<String>> = _registerStatus

    fun register(email: String,password: String, repeatedPassword: String){
        _registerStatus.postValue(Resource.loading(null))
        if(email.isEmpty()||password.isEmpty()||repeatedPassword.isEmpty()){
            _registerStatus.postValue(Resource.error("Please Fill Out All The Fields!",null))
            return
        }
        if(password!=repeatedPassword){
            _registerStatus.postValue(Resource.error("Passwords do not match!",null))
            return
        }
        viewModelScope.launch {
            val result = repository.register(email,password)
            _registerStatus.postValue(result)
        }
    }

}