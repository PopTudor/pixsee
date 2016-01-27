package com.marked.vifo.model;

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import com.marked.vifo.extra.UserConstants
import org.json.JSONObject
import java.util.*

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public data class Contact(var id: String, var firstName: String, var lastName: String, var name: String = "$firstName $lastName".trim(), var token: String) : Comparator<Contact>, Comparable<Contact>, Parcelable {
    companion object {
        val CREATOR = object : Parcelable.Creator<Contact> {
            override public fun createFromParcel(parcelIn: Parcel) = Contact.Contact(parcelIn)
            override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
        };

        private fun Contact(parcelIn: Parcel): Contact {
            var id = parcelIn.readString()
            var firstName = parcelIn.readString()
            var lastName = parcelIn.readString()
            var name = parcelIn.readString()
            var token = parcelIn.readString()
            return Contact(id, firstName, lastName, name, token)
        }
    }

    override public fun compareTo(other: Contact): Int {
        return id.compareTo(other.id);
    }

    override public fun compare(lhs: Contact, rhs: Contact): Int {
        return lhs.id.compareTo(rhs.id);
    }

    override fun equals(other: Any?) = if (other is Contact && id.equals(other.id)) true else false

    public fun toJSON(): JSONObject {
        val jsonObject = JSONObject();
	    jsonObject.put(UserConstants.ID, id);
	    jsonObject.put(UserConstants.FIRST_NAME, firstName);
	    jsonObject.put(UserConstants.LAST_NAME, lastName);
	    jsonObject.put(UserConstants.TOKEN, token);
        return jsonObject;
    }

	public fun toContentValues(): ContentValues {
		val values = ContentValues();
		values.put(UserConstants.ID, id);
		values.put(UserConstants.FIRST_NAME, firstName);
		values.put(UserConstants.LAST_NAME, lastName);
		values.put(UserConstants.TOKEN, token);
		return values;
	}

    public fun fromJSON(json: JSONObject) {
	    id = json.getString(UserConstants.ID);
	    firstName = json.getString(UserConstants.FIRST_NAME);
	    lastName = json.getString(UserConstants.LAST_NAME);
        name = "$firstName $lastName"
	    token = json.getString(UserConstants.TOKEN);
    }

    override public fun describeContents(): Int {
        return 0;
    }

    override public fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(name);
        dest.writeString(token);
    }
}
