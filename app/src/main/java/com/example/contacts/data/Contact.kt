package com.example.contacts.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    val firstName: String,
    val secondName: String,
    @PrimaryKey
    val phone: String,
    val ringtone: String?,
    val note: String?,
    val image: String?
)