package com.anubhav.tiwari.notesapp.repositories

import android.app.Application
import com.anubhav.tiwari.notesapp.data.local.NoteDao
import com.anubhav.tiwari.notesapp.data.local.entities.Note
import com.anubhav.tiwari.notesapp.data.remote.NoteApi
import com.anubhav.tiwari.notesapp.data.remote.requests.AccountRequest
import com.anubhav.tiwari.notesapp.utils.Resource
import com.anubhav.tiwari.notesapp.utils.checkForInternet
import com.anubhav.tiwari.notesapp.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application,
) {

    fun getAllNotes(): Flow<Resource<List<Note>>>{
        return networkBoundResource(
            query = {noteDao.getAllNotes()},
            fetch = {noteApi.getNotes()},
            saveFetchResult = { response ->
                response.body()?.let {
                    //TODO: Insert Notes In Database
                }
            },
            shouldFetch = { checkForInternet(context)}
        )
    }

    suspend fun login(email:String,password:String)= withContext(Dispatchers.IO){
        try {
            val response = noteApi.login(AccountRequest(email,password))
            if (response.isSuccessful&&response.body()!!.successful){
                Resource.success(response.body()?.message)
            }else{
                Resource.error(response.body()?.message?:response.message(),null)
            }
        }catch (e: Exception){
            Resource.error("Could not connect to Servers. Check your Internet Connection!",null)
        }
    }


    suspend fun register(email:String,password:String)= withContext(Dispatchers.IO){
        try {
            val response = noteApi.register(AccountRequest(email,password))
            if (response.isSuccessful&&response.body()!!.successful){
                Resource.success(response.body()?.message)
            }else{
                Resource.error(response.body()?.message?:response.message(),null)
            }
        }catch (e: Exception){
            Resource.error("Could not connect to Servers. Check your Internet Connection!",null)
        }
    }
}