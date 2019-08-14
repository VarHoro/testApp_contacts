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

        fun getDatabase(context: Context, scope: CoroutineScope): ContactsRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        ContactsRoomDatabase::class.java,
                        "Contacts"
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
            var contact = Contact("Emergency", "","020", "Default", "Only for extreme situations", "")
            contactDao.insert(contact)
        }
    }
}