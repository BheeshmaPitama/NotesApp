package com.anubhav.tiwari.notesapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.anubhav.tiwari.notesapp.constants.Constants.BASE_URL
import com.anubhav.tiwari.notesapp.constants.Constants.DATABASE_NAME
import com.anubhav.tiwari.notesapp.constants.Constants.ENCRYPTED_SHARED_PREF_NAME
import com.anubhav.tiwari.notesapp.data.local.NotesDatabase
import com.anubhav.tiwari.notesapp.data.remote.BasicAuthInterceptor
import com.anubhav.tiwari.notesapp.data.remote.NoteApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNotesDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(context,NotesDatabase::class.java,DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideNoteDao(db:NotesDatabase)=db.noteDao()

    @Singleton
    @Provides
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

    @Singleton
    @Provides
    fun provideNoteApi(basicAuthInterceptor: BasicAuthInterceptor):NoteApi{
        val client = OkHttpClient.Builder().addInterceptor(basicAuthInterceptor).build()
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(client).build().create(NoteApi::class.java)
    }

    @Singleton
    @Provides
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ):SharedPreferences{
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_SHARED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}