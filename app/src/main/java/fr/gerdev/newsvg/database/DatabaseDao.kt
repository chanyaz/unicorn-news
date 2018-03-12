package fr.gerdev.newsvg.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

// ger 04/03/18
@Dao
interface DatabaseDao {
    @Query("DELETE FROM Article")
    fun nukeTable()
}