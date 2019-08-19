package com.example.contacts.domain

import androidx.lifecycle.LiveData

interface ContactsInteractor {
    fun getData(): LiveData<List<ContactModel>>
    fun getByPhone(phone: String): LiveData<ContactModel>
    fun getBySearch(searchQuery: String?): LiveData<List<ContactModel>>

    fun addContact(contact:ContactModel): LiveData<Result<String>>
    fun updateContact(contact: ContactModel): LiveData<Result<Unit>>
    fun deleteContact(contact: ContactModel): LiveData<Result<Unit>>
}