package com.example.contacts.app

import androidx.room.Room
import com.example.contacts.data.ContactsDataSourceImpl
import com.example.contacts.data.ContactsRoomDatabase
import com.example.contacts.domain.ContactsDataSource
import com.example.contacts.domain.ContactsInteractor
import com.example.contacts.domain.ContactsInteractorImpl
import com.example.contacts.presentation.listofcontacts.ContactsViewModel
import com.example.contacts.presentation.onecontact.OneContactViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val contactsModule = module {
    single { Room.databaseBuilder(get(), ContactsRoomDatabase::class.java, "Contacts").build()}
    single {get<ContactsRoomDatabase>().contactDao()}
    single<ContactsDataSource> { ContactsDataSourceImpl(get()) }
    single<ContactsInteractor> { ContactsInteractorImpl(get()) }

    viewModel { ContactsViewModel(get()) }
    viewModel { OneContactViewModel(get())}
}