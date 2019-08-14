package com.example.contacts.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ContactRepository (private val contactDao: ContactDao){

    val allContacts: LiveData<List<Contact>> = contactDao.loadAllContacts()

    @WorkerThread
    suspend fun insert(contact: Contact){
        contactDao.insert(contact)
    }

    @WorkerThread
    suspend fun selectByPhone(phone: String): Contact{
        return contactDao.selectByPhone(phone)
    }

    @WorkerThread
    suspend fun update(contact: Contact){
        contactDao.update(contact)
    }

    @WorkerThread
    suspend fun delete(contact: Contact){
        contactDao.delete(contact)
    }
}