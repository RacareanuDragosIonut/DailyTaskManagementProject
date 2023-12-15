import android.os.Parcel
import android.os.Parcelable

data class Task(
    val taskId: String? = null,
    val name: String? = "",
    val priority: String? = "",
    val description: String? = "",
    val owner: String? = "",
    val status: String? = "",
    val type: String? = "",
    val dueDate: String? = "",
    var sharedUsers: List<String> ? = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(taskId)
        parcel.writeString(name)
        parcel.writeString(priority)
        parcel.writeString(description)
        parcel.writeString(owner)
        parcel.writeString(status)
        parcel.writeString(type)
        parcel.writeString(dueDate)
        parcel.writeStringList(sharedUsers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
