package com.engmohammeda.apndoctor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApnDao {
    @Query("""
        SELECT * FROM apn_profiles
        WHERE mcc = :mcc
        AND mnc = :mnc
    """)
    suspend fun findByMccMnc(
        mcc: String,
        mnc: String
    ): List<ApnEntity>

    @Query("""
        SELECT * FROM apn_profiles
        WHERE operatorName LIKE '%' || :query || '%'
    """)
    fun search(
        query: String
    ): Flow<List<ApnEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(
        items: List<ApnEntity>
    )

    @Query("SELECT COUNT(*) FROM apn_profiles")
    suspend fun getCount(): Int
}
