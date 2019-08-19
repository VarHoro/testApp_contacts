package com.example.contacts.presentation.listofcontacts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.domain.ContactModel
import com.example.contacts.presentation.listofcontacts.AdapterModel.Companion.TYPE_CONTACT
import com.example.contacts.presentation.listofcontacts.AdapterModel.Companion.TYPE_LETTER
import com.example.contacts.presentation.onecontact.OneContactActivity

class ContactListAdapter internal constructor(context: Context, val adapterOnClick: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var contacts = ArrayList<AdapterModel>()

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactItemView: TextView = itemView.findViewById(R.id.contact_name_text_view)
        private val contactPhoneView: TextView = itemView.findViewById(R.id.phone_text_view)

        init {
            itemView.setOnClickListener {adapterOnClick(contactPhoneView.text.toString())}
        }

        fun bind(model: AdapterModel) {
            contactItemView.text = model.name
            contactPhoneView.text = model.phone
        }
    }

    inner class AlphabetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val charTextView: TextView = itemView.findViewById(R.id.header_text_view)

        fun bind(model: AdapterModel) {
            charTextView.text = model.letter.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LETTER -> {
                val v = inflater.inflate(R.layout.header_item, parent, false)
                AlphabetViewHolder(v)
            }
            else -> {
                val v = inflater.inflate(R.layout.contact_item, parent, false)
                ContactViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = contacts[position]
        if (current.isContact) {
            holder as ContactViewHolder
            holder.bind(current)
        } else {
            holder as AlphabetViewHolder
            holder.bind(current)
        }
    }

    private fun getFirstLetter(contact: ContactModel): Char {
        return if (contact.secondName != null && contact.secondName.isNotEmpty()) {
            contact.secondName[0]
        } else {
            '#'
        }
    }

    internal fun setContacts(contact: List<ContactModel>) {
        contacts.clear()
        contact.groupBy {
            getFirstLetter(it)
        }.forEach{ (key, list) ->
            contacts.add(
                AdapterModel(
                    isContact = false,
                    name = null,
                    phone = null,
                    letter = key
                )
            )
            contacts.addAll(
                list.map{
                    AdapterModel(
                        isContact = true,
                        name = it.firstName.plus(' ').plus(it.secondName),
                        phone = it.phone,
                        letter = null
                    )
                }
            )
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = contacts.size

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        when (contacts[position].getType()) {
            TYPE_LETTER -> viewType = TYPE_LETTER
            TYPE_CONTACT -> viewType = TYPE_CONTACT
        }
        return viewType
    }

}