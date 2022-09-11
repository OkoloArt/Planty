package com.example.waterme.model

import com.google.firebase.database.Exclude


data class Plants(
    @get: Exclude
    var plantId: String? = null,
    var plantImage: String? = null,
    var plantTitle: String? = null,
    var plantDescription: String? = null,
    var plantReminderHour: Int? = null,
    var plantReminderMinute: Int? = null,
    var plantAlarmChoice: Boolean? = null,
    var plantReminderDays: MutableList<String>? = null,
    val plantAction: MutableList<String>? = null,
    @get: Exclude
    var isDeleted: Boolean = false
    ){
    override fun equals(other: Any?): Boolean {
        return if (other is Plants){
            other.plantId == plantId
        }else false
    }

    override fun hashCode(): Int {
        var result = plantId?.hashCode() ?: 0
        result = 31 * result + (plantImage?.hashCode() ?: 0)
        result = 31 * result + (plantTitle?.hashCode() ?: 0)
        result = 31 * result + (plantDescription?.hashCode() ?: 0)
        result = 31 * result + (plantReminderHour ?: 0)
        result = 31 * result + (plantReminderMinute ?: 0)
        result = 31 * result + (plantAlarmChoice?.hashCode() ?: 0)
        result = 31 * result + (plantReminderDays?.hashCode() ?: 0)
        result = 31 * result + (plantAction?.hashCode() ?: 0)
        result = 31 * result + isDeleted.hashCode()
        return result
    }
}