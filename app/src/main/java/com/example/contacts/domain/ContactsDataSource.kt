package com.example.contacts.domain

import androidx.lifecycle.LiveData

interface ContactsDataSource{
    fun getData(): LiveData<ArrayList<ContactModel>>
    suspend fun insert(contactModel: ContactModel)
    suspend fun getByPhone(phone: String): ContactModel
    suspend fun update(contactModel: ContactModel)
    suspend fun delete(contactModel: ContactModel)
}