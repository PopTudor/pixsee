package com.marked.vifo.model.contact;

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import com.marked.vifo.extra.UserConstants
import org.json.JSONObject
import java.util.*

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
class Contact(id: String, name: String, email: String, token: String) : Comparator<Contact>, Comparable<Contact>, Parcelable {
    constructor(id: String, name: String, token: String) : this(id, name, "", token)

    var id: String
    var name: String
    var email: String
    var token: String

    init {
        this.id = id
        this.name = name
        this.email = email
        this.token = token
    }

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR:Parcelable.Creator<Contact> = object : Parcelable.Creator<Contact> {
            override fun createFromParcel(parcelIn: Parcel) = Contact(parcelIn)
            override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
        };

        protected fun Contact(parcelIn: Parcel): Contact {
            var id = parcelIn.readString()
            var name = parcelIn.readString()
            var email = parcelIn.readString()
            var token = parcelIn.readString()
            return Contact(id, name, email, token)
        }
    }

    override fun describeContents(): Int {
        return 0;
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(token);
    }

    override fun compareTo(other: Contact): Int {
        return id.compareTo(other.id);
    }

    override fun compare(lhs: Contact, rhs: Contact): Int {
        return lhs.id.compareTo(rhs.id);
    }

    override fun equals(other: Any?) = if (other is Contact && id.equals(other.id)) true else false

    fun toJSON(): JSONObject {
        val jsonObject = JSONObject();
        jsonObject.put(UserConstants.ID, id);
        jsonObject.put(UserConstants.NAME, name);
        jsonObject.put(UserConstants.EMAIL, email);
        jsonObject.put(UserConstants.TOKEN, token);
        return jsonObject;
    }

    fun toContentValues(): ContentValues {
        val values = ContentValues();
        values.put(UserConstants.ID, id);
        values.put(UserConstants.NAME, name);
        values.put(UserConstants.EMAIL, email);
        values.put(UserConstants.TOKEN, token);
        return values;
    }

    fun fromJSON(json: JSONObject) {
        id = json.getString(UserConstants.ID);
        name = json.getString(UserConstants.NAME);
        email = json.getString(UserConstants.EMAIL);
        token = json.getString(UserConstants.TOKEN);
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result += 31 * result + name.hashCode()
        result += 31 * result + email.hashCode()
        result += 31 * result + token.hashCode()
        return result
    }
}
