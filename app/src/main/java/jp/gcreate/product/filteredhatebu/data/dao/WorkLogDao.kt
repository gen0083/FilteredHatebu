package jp.gcreate.product.filteredhatebu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.debug.WorkLog

@Dao
interface WorkLogDao {
    
    @Insert fun insert(log: WorkLog)
    
    @Query("select * from debug_work_log")
    fun getAllLog(): List<WorkLog>
}
