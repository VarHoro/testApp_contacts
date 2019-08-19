package com.example.contacts.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

class ContactsInteractorImpl(private val dataSource: ContactsDataSource) : ContactsInteractor {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun addContact(contact: ContactModel): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        scope.launch {
            try {
                dataSource.insert(contact)
                result.postValue(Result.success(contact.firstName.plus(' ').plus(contact.secondName)))
            } catch (e: IOException) {
                result.postValue(Result.failure(e))
            }
        }
        return result
    }

    override fun updateContact(contact: ContactModel): LiveData<Result<Unit>> {
        val result = MutableLiveData<Result<Unit>>()
        scope.launch {
            try {
                dataSource.update(contact)
                result.postValue(Result.success(Unit))
            } catch (e: IOException) {
                result.postValue(Result.failure(e))
            }
        }
        return result
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

    override fun deleteContact(contact: ContactModel): LiveData<Result<Unit>> {
        val result = MutableLiveData<Result<Unit>>()
        scope.launch {
            try {
                dataSource.delete(contact)
                result.postValue(Result.success(Unit))
            } catch (e: IOException) {
                result.postValue(Result.failure(e))
            }
        }
        return result
    }

    override fun getBySearch(searchQuery: String?): LiveData<List<ContactModel>> {
        return if (searchQuery == null) dataSource.getData() else dataSource.searchContacts(searchQuery)
    }
}