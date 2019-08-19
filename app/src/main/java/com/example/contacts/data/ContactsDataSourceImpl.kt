package com.example.contacts.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.ContactsDataSource

class ContactsDataSourceImpl(private val contactDao: ContactDao) :
    ContactsDataSource {

    private fun contactToContactModel(contact: Contact): ContactModel {
        return ContactModel(
            firstName = contact.firstName,
            secondName = contact.secondName,
            phone = contact.phone,
            note = contact.note,
            ringtone = contact.ringtone,
            image = contact.image
        )
    }

    private fun contactModelToContact(contact: ContactModel): Contact {
        return Contact(
            firstName = contact.firstName ?: "",
            secondName = contact.secondName ?: "",
            phone = contact.phone,
            note = contact.note,
            ringtone = contact.ringtone,
            image = contact.image
        )
    }

    override fun update(contactModel: ContactModel) {
        contactDao.update(contactModelToContact(contactModel))
    }

    override fun insert(contactModel: ContactModel) {
        contactDao.insert(contactModelToContact(contactModel))
    }

    override fun getData(): LiveData<List<ContactModel>> {
        val liveData = contactDao.loadAllContacts()
        return Transformations.map(liveData) { contacts ->
            contacts.map {
                contactToContactModel(it)
            }
        }
    }

    override fun getByPhone(phone: String): ContactModel {
        return contactToContactModel(contactDao.selectByPhone(phone))
    }

    override fun delete(contactModel: ContactModel) {
        contactDao.deleteContact(contactModel.phone)
    }

    override fun searchContacts(query: String): LiveData<List<ContactModel>> {
        val liveData = contactDao.searchContacts(query)
        return Transformations.map(liveData) { contacts ->
            contacts.map {
                contactToContactModel(it)
            }
        }
    }
}
