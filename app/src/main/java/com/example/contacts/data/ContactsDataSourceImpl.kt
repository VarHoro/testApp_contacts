package com.example.contacts.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.ContactsDataSource
import kotlinx.coroutines.CoroutineScope

class ContactsDataSourceImpl(application: Application, scope: CoroutineScope) : ContactsDataSource {
    private val repository: ContactRepository

    init {
        val contactDao = ContactsRoomDatabase.getDatabase(application, scope).contactDao()
        repository = ContactRepository(contactDao)
    }

    override suspend fun update(contactModel: ContactModel) {
        val contact = Contact(
            firstName = contactModel.firstName ?: "",
            secondName = contactModel.secondName ?: "",
            phone = contactModel.phone,
            note = contactModel.note,
            ringtone = contactModel.ringtone,
            image = contactModel.image
        )
        repository.update(contact)
    }

    override suspend fun insert(contactModel: ContactModel) {
        val contact = Contact(
            firstName = contactModel.firstName ?: "",
            secondName = contactModel.secondName ?: "",
            phone = contactModel.phone,
            note = contactModel.note,
            ringtone = contactModel.ringtone,
            image = contactModel.image
        )
        repository.insert(contact)
    }

    override fun getData(): LiveData<ArrayList<ContactModel>> {
        val liveData = repository.allContacts
        return Transformations.map(liveData) { contacts -> listToArrayList(contacts) }
    }

    private fun listToArrayList(contacts: List<Contact>): ArrayList<ContactModel>{
        val model = ArrayList<ContactModel>()
        contacts.forEach {
            val contact = ContactModel(
                firstName = it.firstName,
                secondName = it.secondName,
                phone = it.phone,
                note = it.note,
                ringtone = it.ringtone,
                image = it.image
            )
            model.add(contact)
        }
        return model
    }

    override suspend fun getByPhone(phone: String): ContactModel {
        val contact = repository.selectByPhone(phone)
        return ContactModel(
            firstName = contact.firstName,
            secondName = contact.secondName,
            phone = contact.phone,
            note = contact.note,
            ringtone = contact.ringtone,
            image = contact.image
        )
    }

    override suspend fun delete(contactModel: ContactModel) {
        val contact = Contact(
            firstName = contactModel.firstName ?: "",
            secondName = contactModel.secondName ?: "",
            phone = contactModel.phone,
            note = contactModel.note,
            ringtone = contactModel.ringtone,
            image = contactModel.image
        )
        repository.delete(contact)
    }
}
