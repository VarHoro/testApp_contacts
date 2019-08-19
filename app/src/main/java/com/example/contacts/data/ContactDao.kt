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
    fun insert(contact: Contact)

    @Query("DELETE FROM contacts WHERE phone = :phone")
    fun deleteContact(phone: String)

    @Update
    fun update(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY secondName ASC")
    fun loadAllContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE firstName LIKE '%' || :query || '%' OR secondName LIKE '%' || :query || '%' ORDER BY secondName ASC")
    fun searchContacts(query: String): LiveData<List<Contact>>
}