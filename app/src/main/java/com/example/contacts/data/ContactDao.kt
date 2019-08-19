package com.example.contacts.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDao {
    @Query("DELETE FROM contacts")
    fun deleteAll()

    @Query("SELECT * FROM contacts WHERE phone = :phone")
    fun selectByPhone(phone: String): Contact

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Query("DELETE FROM contacts WHERE phone = :phone")
    suspend fun deleteContact(phone: String)

    @Update
    suspend fun update(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY secondName ASC")
    fun loadAllContacts(): LiveData<List<Contact>>
}