package com.example.contacts.presentation.onecontact

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.ContactsInteractor

class OneContactViewModel(private val interactor: ContactsInteractor) : ViewModel() {

    val isExistingContact = ObservableBoolean(false)
    val imageText = ObservableField("")
    val firstNameText = ObservableField("")
    val secondNameText = ObservableField("")
    val phoneText = ObservableField("")
    val noteText = ObservableField("")
    val ringtoneText = ObservableField("Default")

    private lateinit var contactModel: LiveData<ContactModel>

    fun getByPhone(getPhone: String): LiveData<Boolean> {
        contactModel = interactor.getByPhone(getPhone)
        return Transformations.map(contactModel) { contactModel ->
            imageText.set(contactModel.image)
            firstNameText.set(contactModel.firstName)
            secondNameText.set(contactModel.secondName)
            phoneText.set(contactModel.phone)
            noteText.set(contactModel.note)
            ringtoneText.set(contactModel.ringtone)
            isExistingContact.set(true)
            true
        }
    }

    fun addContact(): LiveData<String> {
        return Transformations.map(interactor.addContact(setContactModel())) {
            it.fold(
                { value -> String.format(SAVED, value) },
                { exception -> "Error: ${exception.message}" }
            )
        }
    }

    fun updateContact(): LiveData<String> {
        val contact = setContactModel()
        return if (contactModel.value != contact) {
            Transformations.map(interactor.updateContact(contact)) {
                it.fold(
                    { UPDATED },
                    { exception -> "Error: ${exception.message}" }
                )
            }
        } else {
            val str = MutableLiveData<String>()
            str.postValue(UNCHANGED)
            str
        }
    }

    fun deleteContact(): LiveData<String> {
        val model: ContactModel? = contactModel.value
        return if (model != null) {
            Transformations.map(interactor.deleteContact(model)) {
                it.fold(
                    { DELETED },
                    { exception -> "Error: ${exception.message}" }
                )
            }
        } else {
            val str = MutableLiveData<String>()
            str.postValue(PROBLEM)
            str
        }
    }

    private fun setContactModel(): ContactModel {
        return ContactModel(
            image = imageText.get(),
            firstName = firstNameText.get(),
            secondName = secondNameText.get(),
            phone = phoneText.get() ?: "",
            note = noteText.get(),
            ringtone = ringtoneText.get()
        )
    }

    companion object {
        const val SAVED = "Contact saved: %s!"
        const val UPDATED = "Changes are saved!"
        const val UNCHANGED = "There were no changes"
        const val DELETED = "Contact was deleted!"
        const val PROBLEM = "Contact not found"
    }
}