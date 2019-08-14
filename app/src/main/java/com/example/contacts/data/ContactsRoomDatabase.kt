package com.example.contacts.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Contact::class], version = 1)
abstract class ContactsRoomDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactsRoomDatabase? = null

        private const val name = "Contacts"
        private const val fName = "Emergency"
        private const val sName = ""
        private const val phone = "020"
        private const val ringtone = "Default"
        private const val note = "Only for extreme situations"
        private const val image = ""

        fun getDatabase(context: Context, scope: CoroutineScope): ContactsRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        ContactsRoomDatabase::class.java,
                        name
                    ).addCallback(ContactDatabaseCallBack(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class ContactDatabaseCallBack(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.contactDao())
                }
            }
        }

        suspend fun populateDatabase(contactDao: ContactDao) {
            val contact = Contact(fName, sName, phone, ringtone, note, image)
            contactDao.insert(contact)
        }
    }
}