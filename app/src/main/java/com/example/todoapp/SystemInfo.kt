package com.example.todoapp

import android.os.Parcel
import android.os.Parcelable

data class SystemInfo(
    val deviceName: String,
    val androidVersion: String,
    val apiLevel: Int,
    val manufacturer: String,
    val model: String,
    val batteryLevel: Int,
    val availableMemory: Long,
    val totalMemory: Long,
    val isCharging: Boolean,
    val timestamp: Long,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(deviceName)
        parcel.writeString(androidVersion)
        parcel.writeInt(apiLevel)
        parcel.writeString(manufacturer)
        parcel.writeString(model)
        parcel.writeInt(batteryLevel)
        parcel.writeLong(availableMemory)
        parcel.writeLong(totalMemory)
        parcel.writeByte(if (isCharging) 1 else 0)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SystemInfo> {
        override fun createFromParcel(parcel: Parcel): SystemInfo = SystemInfo(parcel)

        override fun newArray(size: Int): Array<SystemInfo?> = arrayOfNulls(size)
    }
}
