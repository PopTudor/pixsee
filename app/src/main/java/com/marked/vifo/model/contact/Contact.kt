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
class Contact(id: String, name: String, token: String) : Comparator<Contact>, Comparable<Contact>, Parcelable {
    var id: String
    var name: String
    var token: String

    init {
        this.id = id
        this.name = name
        this.token = token
    }

    companion object {
        val CREATOR = object : Parcelable.Creator<Contact> {
            override fun createFromParcel(parcelIn: Parcel) = Contact(parcelIn)
            override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
        };

        private fun Contact(parcelIn: Parcel): Contact {
            var id = parcelIn.readString()
            var name = parcelIn.readString()
            var token = parcelIn.readString()
            return Contact(id, name, token)
        }
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
        jsonObject.put(UserConstants.TOKEN, token);
        return jsonObject;
    }

    fun toContentValues(): ContentValues {
        val values = ContentValues();
        values.put(UserConstants.ID, id);
        values.put(UserConstants.TOKEN, token);
        return values;
    }

    fun fromJSON(json: JSONObject) {
        id = json.getString(UserConstants.ID);
        name = json.getString(UserConstants.NAME);
        token = json.getString(UserConstants.TOKEN);
    }

    override fun describeContents(): Int {
        return 0;
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(token);
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result += 31 * result + name.hashCode()
        result += 31 * result + token.hashCode()
        return result
    }
}
