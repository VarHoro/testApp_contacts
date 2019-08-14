package com.example.contacts.presentation.listofcontacts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.domain.ContactModel
import com.example.contacts.presentation.listofcontacts.AdapterModel.Companion.TYPE_CONTACT
import com.example.contacts.presentation.listofcontacts.AdapterModel.Companion.TYPE_LETTER
import com.example.contacts.presentation.onecontact.OneContactActivity

class ContactListAdapter internal constructor(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                contactsFiltered = (if (charString.isEmpty()) {
                    contacts
                } else {
                    val filtered = ArrayList<AdapterModel>()
                    for (row in contacts) {
                        if (row.isContact) {
                            if (row.name!!.toLowerCase().contains(charString.toLowerCase()) ||
                                row.phone!!.contains(charString)
                            ) {
                                filtered.add(row)
                            }
                        }
                    }
                    filtered
                })

                val results = FilterResults()
                results.values = contactsFiltered
                return results
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                if (p1 != null) {
                    contactsFiltered = p1.values as ArrayList<AdapterModel>
                    notifyDataSetChanged()
                }
            }
        }
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var contacts = ArrayList<AdapterModel>()
    private var contactsFiltered = ArrayList<AdapterModel>()

    companion object {
        const val EXTRA_PHONE = "com.example.contacts.presentation.listofcontacts.FNAME"
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactItemView: TextView = itemView.findViewById(R.id.contact_name_text_view)
        val contactPhoneView: TextView = itemView.findViewById(R.id.phone_text_view)

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, OneContactActivity::class.java)
                intent.putExtra(EXTRA_PHONE, contactPhoneView.text.toString())
                context.startActivity(intent)
            }
        }

    }

    inner class AlphabetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val charTextView: TextView = itemView.findViewById(R.id.header_text_view)
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
        val current = contactsFiltered[position]
        if (current.isContact) {
            holder as ContactViewHolder
            holder.contactItemView.text = current.name
            holder.contactPhoneView.text = current.phone
        } else {
            holder as AlphabetViewHolder
            holder.charTextView.text = current.letter.toString().toUpperCase()
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
        var currentLetter = getFirstLetter(contact[0])

        if (contact.size == 1) {
            contacts.add(
                AdapterModel(
                    isContact = false,
                    name = null,
                    phone = null,
                    letter = currentLetter
                )
            )
            contacts.add(
                AdapterModel(
                    isContact = true,
                    name = contact[0].firstName.plus(' ').plus(contact[0].secondName),
                    phone = contact[0].phone,
                    letter = null
                )
            )
        } else {

            for (i in 0 until contact.size - 1) {
                if (i == 0) {
                    contacts.add(
                        AdapterModel(
                            isContact = false,
                            name = null,
                            phone = null,
                            letter = currentLetter
                        )
                    )
                }
                if (currentLetter != getFirstLetter(contact[i + 1])) {
                    contacts.add(
                        AdapterModel(
                            isContact = true,
                            name = contact[i].firstName.plus(' ').plus(contact[i].secondName),
                            phone = contact[i].phone,
                            letter = null
                        )
                    )
                    currentLetter = getFirstLetter(contact[i + 1])
                    contacts.add(
                        AdapterModel(
                            isContact = false,
                            name = null,
                            phone = null,
                            letter = currentLetter
                        )
                    )
                } else {
                    contacts.add(
                        AdapterModel(
                            isContact = true,
                            name = contact[i].firstName.plus(' ').plus(contact[i].secondName),
                            phone = contact[i].phone,
                            letter = null
                        )
                    )
                }
            }
            contacts.add(
                AdapterModel(
                    isContact = true,
                    name = contact[contact.size - 1].firstName.plus(' ').plus(contact[contact.size - 1].secondName),
                    phone = contact[contact.size - 1].phone,
                    letter = null
                )
            )
        }
        contactsFiltered = contacts
        notifyDataSetChanged()
    }

    override fun getItemCount() = contactsFiltered.size

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        if (contactsFiltered[position].getType() == TYPE_LETTER) {
            viewType = TYPE_LETTER
        } else if (contactsFiltered[position].getType() == TYPE_CONTACT) {
            viewType = TYPE_CONTACT
        }
        return viewType
    }
}