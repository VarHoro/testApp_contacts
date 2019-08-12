package com.example.contacts.presentation.listofcontacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.data.Contact

class ContactListAdapter internal constructor(context: Context): RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var contacts = emptyList<Contact>()

    inner class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val contactItemView: TextView = itemView.findViewById(R.id.contact_name_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = inflater.inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val current = contacts[position]
        holder.contactItemView.text = current.name
    }

    internal fun setContacts(contact: List<Contact>){
        this.contacts = contact
        notifyDataSetChanged()
    }

    override fun getItemCount() = contacts.size
}