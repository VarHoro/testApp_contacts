package com.example.contacts.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 1)
abstract class ContactsRoomDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
}