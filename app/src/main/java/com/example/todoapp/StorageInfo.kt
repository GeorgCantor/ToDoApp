package com.example.todoapp

import android.os.Parcel
import android.os.Parcelable

data class StorageInfo(
    val totalInternalStorage: Long,
    val availableInternalStorage: Long,
    val totalExternalStorage: Long,
    val availableExternalStorage: Long,
    val isExternalStorageAvailable: Boolean,
    val storageUsagePercentage: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeLong(totalInternalStorage)
        parcel.writeLong(availableInternalStorage)
        parcel.writeLong(totalExternalStorage)
        parcel.writeLong(availableExternalStorage)
        parcel.writeByte(if (isExternalStorageAvailable) 1 else 0)
        parcel.writeInt(storageUsagePercentage)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<StorageInfo> {
        override fun createFromParcel(parcel: Parcel): StorageInfo = StorageInfo(parcel)

        override fun newArray(size: Int): Array<StorageInfo?> = arrayOfNulls(size)
    }
}
