package com.marked.vifo.model.contact;

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.marked.vifo.extra.UserConstants

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
data class Contact(
        @SerializedName(value = "userID",alternate=arrayOf("_id"))
        var userID: String?,
        var name: String?,
        var email: String?,
        var token: String?
) : Parcelable {
    constructor() : this(null, null, null, null)

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR: Parcelable.Creator<Contact> = object : Parcelable.Creator<Contact> {
            override fun createFromParcel(parcelIn: Parcel) = Contact(parcelIn)
            override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
        };

        private fun Contact(parcelIn: Parcel): Contact {
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
        dest.writeString(userID);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(token);
    }

    fun toContentValues(): ContentValues {
        val values = ContentValues();
        values.put(UserConstants.ID, userID);
        values.put(UserConstants.NAME, name);
        values.put(UserConstants.EMAIL, email);
        values.put(UserConstants.TOKEN, token);
        return values;
    }
}
