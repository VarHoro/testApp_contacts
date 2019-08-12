package com.example.contacts.presentation.listofcontacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.contacts.data.Contact
import com.example.contacts.data.ContactRepository
import com.example.contacts.data.ContactsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application){

    private val repository: ContactRepository
    val allContacts: LiveData<List<Contact>>

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        val contactDao = ContactsRoomDatabase.getDatabase(application, scope).contactDao()
        repository = ContactRepository(contactDao)
        allContacts = repository.allContacts
    }

    fun insert(contact: Contact){
        scope.launch {
            repository.insert(contact)
        }
    }
}