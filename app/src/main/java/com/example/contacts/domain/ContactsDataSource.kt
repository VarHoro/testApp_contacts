package com.example.contacts.domain

import androidx.lifecycle.LiveData

interface ContactsDataSource{
    fun getData(): LiveData<List<ContactModel>>
    suspend fun insert(contactModel: ContactModel)
    fun getByPhone(phone: String): ContactModel
    suspend fun update(contactModel: ContactModel)
    suspend fun delete(contactModel: ContactModel)
}