package jp.gcreate.product.filteredhatebu.data.entities.debug

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "debug_work_log")
data class WorkLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val runAt: ZonedDateTime,
    val result: String,
    val runAtStr: String = runAt.toString()
)
