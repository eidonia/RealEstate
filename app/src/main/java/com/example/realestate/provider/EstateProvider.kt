package com.example.realestate.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.example.realestate.database.dao.EstateDao
import com.example.realestate.models.RealEstate
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class EstateProvider : ContentProvider() {

    val AUTHORITY = "com.example.realestate.provider"
    val TABLE_NAME = RealEstate::class.java.simpleName

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface EntryPointEstateProvider {
        fun estateDao(): EstateDao
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val appContext = context?.applicationContext ?: throw IllegalStateException()
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(appContext, EntryPointEstateProvider::class.java)
        val locDao = hiltEntryPoint.estateDao()
        var cursor = locDao.getEstateCursor()
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.estate/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw IllegalAccessException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw IllegalAccessException()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw IllegalAccessException()
    }
}