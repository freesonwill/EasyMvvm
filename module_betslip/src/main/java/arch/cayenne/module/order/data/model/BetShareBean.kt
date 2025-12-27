package arch.cayenne.module.order.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author: wenxi
 * @date: 26/12/25 20:46
 * @description:
 */
data class BetShareBean(
    val userId: Long,
    val settleId: String?,
    val gameType: Int,
    val validBetScore: Int,
    val winScore: Int,
    val multi: String?,
    val roomName: String?,
    val ccy: String?,
    val settleTime: String?,
    val content: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(userId)
        dest.writeString(settleId)
        dest.writeInt(gameType)
        dest.writeInt(validBetScore)
        dest.writeInt(winScore)
        dest.writeString(multi)
        dest.writeString(roomName)
        dest.writeString(ccy)
        dest.writeString(settleTime)
        dest.writeString(content)
    }

    companion object CREATOR : Parcelable.Creator<BetShareBean> {
        override fun createFromParcel(parcel: Parcel): BetShareBean {
            return BetShareBean(parcel)
        }

        override fun newArray(size: Int): Array<BetShareBean?> {
            return arrayOfNulls(size)
        }
    }
}
