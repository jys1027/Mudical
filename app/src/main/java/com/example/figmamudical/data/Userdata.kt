package com.example.figmamudical.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Userdata (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "username")
    val userName : String,
    @ColumnInfo(name = "password")
    val userPassword : String,
    @ColumnInfo(name = "email")
    val userEmail : String,
    @ColumnInfo(name = "choice1")
    val userChoice1 : Int = 0,
    @ColumnInfo(name = "choice2")
    val userChoice2 : Int = 0,
    @ColumnInfo(name = "join_date")
    val joinDate : String,
    //@ColumnInfo(name = "program_segment_schedule")
    //val scheduleMade : Int,
    //@ColumnInfo(name = "if_done_segment_this_week")
    //val thisWeekDone : Boolean,
    @ColumnInfo(name = "number_of_segments_finished")
    val segmentsDone : Int = 0,
)