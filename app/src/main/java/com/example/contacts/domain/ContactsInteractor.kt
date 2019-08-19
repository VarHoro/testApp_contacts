package com.example.contacts.domain

import androidx.lifecycle.LiveData

interface ContactsInteractor {
    fun getData(): LiveData<List<ContactModel>>
    fun addContact(contact:ContactModel)
    fun getByPhone(phone: String): LiveData<ContactModel>
    fun updateContact(contact: ContactModel)
    fun deleteContact(contact: ContactModel)
    fun getBySearch(searchQuery: String?): LiveData<List<ContactModel>>
}