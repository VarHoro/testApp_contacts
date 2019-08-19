package com.example.contacts.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ContactsInteractorImpl(private val dataSource: ContactsDataSource) : ContactsInteractor {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var observer: Observer<List<ContactModel>>

    override fun addContact(contact: ContactModel) {
        scope.launch {
            dataSource.insert(contact)
        }
    }

    override fun updateContact(contact: ContactModel) {
        scope.launch {
            dataSource.update(contact)
        }
    }

    override fun getData(): LiveData<List<ContactModel>> {
        return dataSource.getData()
    }

    override fun getByPhone(phone: String): LiveData<ContactModel> {
        val contact = MutableLiveData<ContactModel>()
        scope.launch {
            contact.postValue(dataSource.getByPhone(phone))
        }
        return contact
    }

    override fun deleteContact(contact: ContactModel) {
        scope.launch {
            dataSource.delete(contact)
        }
    }

    override fun getBySearch(searchQuery: String?): LiveData<List<ContactModel>> {
        return dataSource.searchContacts(searchQuery.toString())
    }
}