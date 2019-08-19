package com.example.contacts.domain

import androidx.lifecycle.LiveData

interface ContactsDataSource{
    fun getData(): LiveData<List<ContactModel>>
    fun insert(contactModel: ContactModel)
    fun getByPhone(phone: String): ContactModel
    fun update(contactModel: ContactModel)
    fun delete(contactModel: ContactModel)
}