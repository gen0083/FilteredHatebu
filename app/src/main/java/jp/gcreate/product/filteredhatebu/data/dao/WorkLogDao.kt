package jp.gcreate.product.filteredhatebu.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.debug.WorkLog

@Dao
interface WorkLogDao {
    
    @Insert fun insert(log: WorkLog)
    
    @Query("select * from debug_work_log")
    fun getAllLog(): List<WorkLog>
}
