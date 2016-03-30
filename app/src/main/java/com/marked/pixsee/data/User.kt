package com.marked.pixsee.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
data class User(
        @SerializedName(value = "userID", alternate = arrayOf("_id"))
        val userID: String,
        val name: String,
        val email: String,
        val token: String
) : Parcelable {
    var password: String? = null

    constructor(userID: String, name: String, email: String,token: String,password: String? = null)
    : this(userID, name, email, token) {
        this.password = password
    }

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcelIn: Parcel) = Contact(parcelIn)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        };

        private fun Contact(parcelIn: Parcel): User {
            var id = parcelIn.readString()
            var name = parcelIn.readString()
            var email = parcelIn.readString()
            var token = parcelIn.readString()
            var password = parcelIn.readString()
            return User(id, name, email, token, password)
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
        dest.writeString(password);
    }
}
