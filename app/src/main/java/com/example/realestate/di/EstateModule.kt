package com.example.realestate.di

import android.content.Context
import androidx.room.Room
import com.example.realestate.database.EstateDatabase
import com.example.realestate.database.dao.EstateDao
import com.example.realestate.network.GeocodeMapsService
import com.example.realestate.repository.EstateModel
import com.example.realestate.repository.EstateRepository
import com.example.realestate.utils.Constante.DB_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EstateModule {

    @Provides
    @Singleton
    fun providesGeocodeMap(): GeocodeMapsService {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodeMapsService::class.java)
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): EstateDatabase {
        return Room.databaseBuilder(context, EstateDatabase::class.java, DB_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEstateDao(estateDatabase: EstateDatabase): EstateDao {
        return estateDatabase.estateDao()
    }

    @Provides
    @Singleton
    fun provideEstateModel(
        db: FirebaseFirestore,
        geocodeMapsService: GeocodeMapsService,
        estateDao: EstateDao,
        storage: StorageReference
    ): EstateModel {
        return EstateRepository(db, geocodeMapsService, estateDao, storage)
    }


    @Provides
    @Singleton
    fun providesFirebaseUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    @Provides
    @Singleton
    fun providesDb(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun providesStorage(): FirebaseStorage {
        return Firebase.storage("gs://realestate-16d21.appspot.com/")
    }

    @Provides
    @Singleton
    fun providesStorageRef(firebaseStorage: FirebaseStorage): StorageReference {
        return firebaseStorage.reference
    }


}