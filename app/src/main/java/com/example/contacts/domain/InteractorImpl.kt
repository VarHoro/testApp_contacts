package com.example.contacts.domain

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class InteractorImpl(private val application: Application) : Interactor, KoinComponent {

    private val dataSource: ContactsDataSource by inject { parametersOf(this.application, scope) }

    private val job = SupervisorJob()

    private val scope = CoroutineScope(Dispatchers.IO + job)
    override fun insert(contact: ContactModel) {
        scope.launch {
            dataSource.insert(contact)
        }
    }

    override fun update(contact: ContactModel) {
        scope.launch {
            dataSource.update(contact)
        }
    }

    override fun getData(): LiveData<ArrayList<ContactModel>> {
        return dataSource.getData()
    }

    override fun getByPhone(phone: String): LiveData<ContactModel> {
        val contact = MutableLiveData<ContactModel>()
        scope.launch {
            contact.postValue(dataSource.getByPhone(phone))
        }
        return contact
    }

    override fun delete(contact: ContactModel) {
        scope.launch {
            dataSource.delete(contact)
        }
    }
}