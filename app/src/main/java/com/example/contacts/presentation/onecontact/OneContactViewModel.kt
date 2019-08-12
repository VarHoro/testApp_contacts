package com.example.contacts.presentation.onecontact

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.contacts.data.Contact
import com.example.contacts.data.ContactRepository
import com.example.contacts.data.ContactsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class OneContactViewModel(application: Application): AndroidViewModel(application){
    private val repository: ContactRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    val firstName = ObservableField<String>("")
    val secondName = ObservableField<String>("")
    val phone = ObservableField<String>("")
    val note = ObservableField<String>("")
    val ringtone = ObservableField<String>("Default")

    var contact =  Contact("", "", null, null)

    init {
        val contactDao = ContactsRoomDatabase.getDatabase(application, scope).contactDao()
        repository = ContactRepository(contactDao)
    }

    fun getByName(name: String): Contact {
        scope.launch {
            contact = repository.selectByName(name)
            firstName.set(contact.name)
            secondName.set(contact.name)
            phone.set(contact.phone)
            note.set(contact.note)
            ringtone.set(contact.ringtone)
        }
        return contact
    }

}