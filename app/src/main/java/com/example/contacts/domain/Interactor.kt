package com.example.contacts.domain

import androidx.lifecycle.LiveData

interface Interactor {
    fun getData(): LiveData<ArrayList<ContactModel>>
    fun insert(contact:ContactModel)
    fun getByPhone(phone: String): LiveData<ContactModel>
    fun update(contact: ContactModel)
    fun delete(contact: ContactModel)
}