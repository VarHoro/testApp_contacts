package com.example.contacts.presentation.listofcontacts

import androidx.lifecycle.*
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.Interactor

class ContactsViewModel(private val interactor: Interactor) : ViewModel() {

    var allContacts: LiveData<List<ContactModel>> = interactor.getData()

    fun getBySearch(searchQuery: String?): LiveData<List<ContactModel>> {
        return interactor.getBySearch(searchQuery)
    }
}