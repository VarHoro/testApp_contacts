package com.example.contacts.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.ContactsDataSource

class ContactsDataSourceImpl(private val contactDao: ContactDao) :
    ContactsDataSource {

    override fun update(contactModel: ContactModel) {
        val contact = Contact(
            firstName = contactModel.firstName ?: "",
            secondName = contactModel.secondName ?: "",
            phone = contactModel.phone,
            note = contactModel.note,
            ringtone = contactModel.ringtone,
            image = contactModel.image
        )
        contactDao.update(contact)
    }

    override fun insert(contactModel: ContactModel) {
        val contact = Contact(
            firstName = contactModel.firstName ?: "",
            secondName = contactModel.secondName ?: "",
            phone = contactModel.phone,
            note = contactModel.note,
            ringtone = contactModel.ringtone,
            image = contactModel.image
        )
        contactDao.insert(contact)
    }

    override fun getData(): LiveData<List<ContactModel>> {
        val liveData = contactDao.loadAllContacts()
        return Transformations.map(liveData) { contacts ->
            contacts.map {
                ContactModel(
                    firstName = it.firstName,
                    secondName = it.secondName,
                    phone = it.phone,
                    note = it.note,
                    ringtone = it.ringtone,
                    image = it.image
                )
            }
        }
    }

    override fun getByPhone(phone: String): ContactModel {
        val contact = contactDao.selectByPhone(phone)
        return ContactModel(
            firstName = contact.firstName,
            secondName = contact.secondName,
            phone = contact.phone,
            note = contact.note,
            ringtone = contact.ringtone,
            image = contact.image
        )
    }

    override fun delete(contactModel: ContactModel) {
        contactDao.deleteContact(contactModel.phone)
    }
}
