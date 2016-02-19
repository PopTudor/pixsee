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
        @SerializedName("id")
        var id: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("token")
        var token: String?,
        @SerializedName("password")
        var password: String? = null
) : Parcelable {
    constructor(id: String?, name: String?, token: String?) : this(id, name, null, token)

    constructor(id: String?, name: String?, email: String?, token: String?) : this(id, name, email, token, null)

    constructor() : this("", "", "")

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
            var password = parcelIn.readString()
            return Contact(id, name, email, token,password)
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
        dest.writeString(password);
    }

    fun toContentValues(): ContentValues {
        val values = ContentValues();
        values.put(UserConstants.ID, id);
        values.put(UserConstants.NAME, name);
        values.put(UserConstants.EMAIL, email);
        values.put(UserConstants.TOKEN, token);
        values.put(UserConstants.PASSWORD, password);
        return values;
    }
}
