package com.example.contacts.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class InteractorImpl(private val dataSource: ContactsDataSource) : Interactor {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var observer : Observer<List<ContactModel>>

    override fun addContact(contact: ContactModel) {
        dataSource.insert(contact)
    }

    override fun updateContact(contact: ContactModel) {
        dataSource.update(contact)
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
        dataSource.delete(contact)
    }

    override fun getBySearch(searchQuery: String?): LiveData<List<ContactModel>> {
        val contacts = dataSource.getData()
        val result = MutableLiveData<List<ContactModel>>()
        var list = ArrayList<ContactModel>()
        observer = Observer {
            if (searchQuery != null) {
                it.forEach { contact ->
                    if (contact.firstName?.toLowerCase()?.contains(searchQuery.toLowerCase()) == true ||
                        contact.secondName?.toLowerCase()?.contains(searchQuery.toLowerCase()) == true || contact.phone.contains(
                            searchQuery.toString()
                        )
                    ) {
                        list.add(contact)
                    }
                }
            } else {
                list = it as ArrayList<ContactModel>
            }
            result.postValue(list)
            contacts.removeObserver(observer)
        }
        contacts.observeForever(observer)
        return result
    }
}