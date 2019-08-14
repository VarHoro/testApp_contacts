package com.example.contacts.presentation.listofcontacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.Interactor
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class ContactsViewModel(application: Application) : AndroidViewModel(application),
    KoinComponent {

    private val interactor: Interactor by inject { parametersOf(application) }
    var allContacts: LiveData<ArrayList<ContactModel>>

    init {
        allContacts = interactor.getData()
    }

    fun insert(contact: ContactModel) {
        interactor.insert(contact)
    }
}