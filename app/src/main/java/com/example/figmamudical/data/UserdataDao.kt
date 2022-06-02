package com.example.figmamudical.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserdataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: Userdata)

    @Update
    suspend fun update(data: Userdata)

    @Delete
    suspend fun delete(data: Userdata)

    @Query("SELECT * from Userdata WHERE username = :username")
    fun getUser(username: String): Flow<Userdata>


}