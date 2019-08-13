package com.example.contacts.presentation.onecontact

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.Interactor
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class OneContactViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val interactor: Interactor by inject { parametersOf(application) }

    val isExistingContact = ObservableBoolean(false)
    val firstNameText = ObservableField<String>("")
    val secondNameText = ObservableField<String>("")
    val phoneText = ObservableField<String>("")
    val noteText = ObservableField<String>("")
    val ringtoneText = ObservableField<String>("Default")

    private lateinit var contactModel: ContactModel

    fun getByPhone(getPhone: String): ContactModel {
        contactModel = interactor.getByPhone(getPhone)
        firstNameText.set(contactModel.firstName)
        secondNameText.set(contactModel.secondName)
        phoneText.set(contactModel.phone)
        noteText.set(contactModel.note)
        ringtoneText.set(contactModel.ringtone)
        isExistingContact.set(true)
        return contactModel
    }

    fun update() {
        val contact = ContactModel(
            firstName = firstNameText.get(),
            secondName = secondNameText.get(),
            phone = phoneText.get() ?: "",
            note = noteText.get(),
            ringtone = ringtoneText.get()
        )
        if (contactModel != contact) {
            interactor.update(contact)
        }
    }

}