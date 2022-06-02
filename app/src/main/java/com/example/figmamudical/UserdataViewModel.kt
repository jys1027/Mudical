package com.example.figmamudical

import android.util.Log
import androidx.lifecycle.*
import com.example.figmamudical.data.Userdata
import com.example.figmamudical.data.UserdataDao
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UserdataViewModel(private val dataDao: UserdataDao) : ViewModel() {

    private lateinit var curUsername: String
    private var selectedSong: Int = 0
    private var chosenInstrument = mutableListOf(false,false,false,false,false,false,false,false,false)

    fun setCurUsername(name: String) {
        curUsername = name
    }
    fun getCurUsername(): String {
        return curUsername
    }

    private fun insertNewUser(data: Userdata){
        viewModelScope.launch {
            dataDao.insert(data)
        }
    }
    private fun getNewUserEntry(userName: String, userPassword: String, userEmail: String, userChoice1: Int, userChoice2: Int, joinDate: String, segmentsDone: Int): Userdata {
        return Userdata(
            userName = userName,
            userPassword = userPassword,
            userEmail = userEmail,
            userChoice1 = userChoice1,
            userChoice2 = userChoice2,
            joinDate = joinDate,
            segmentsDone = segmentsDone
        )
    }
    fun addUser(userName: String, userPassword: String, userEmail:String, userChoice1: Int, userChoice2: Int, joinDate: String, segmentsDone: Int) {
        val newUser = getNewUserEntry(userName, userPassword, userEmail, userChoice1, userChoice2, joinDate, segmentsDone)
        insertNewUser(newUser)
    }
    fun getUser(username: String) : LiveData<Userdata> {
        return dataDao.getUser(username).asLiveData()
    }


    private fun getUpdatedUserEntry(
        userId: Int,
        userName: String,
        userPass: String,
        userEmail: String,
        userChoice1: Int,
        userChoice2: Int,
        joinDate: String,
        segmentsDone: Int
    ): Userdata {
        return Userdata(
            id = userId,
            userName = userName,
            userPassword = userPass,
            userEmail = userEmail,
            userChoice1 = userChoice1,
            userChoice2 = userChoice2,
            joinDate = joinDate,
            segmentsDone = segmentsDone
        )
    }
    fun updateUser(
        userId: Int,
        userName: String,
        userPass: String,
        userEmail: String,
        userChoice1: Int,
        userChoice2: Int,
        joinDate: String,
        segmentsDone: Int
    ) {
        val updatedUser = getUpdatedUserEntry(userId, userName, userPass, userEmail, userChoice1, userChoice2, joinDate, segmentsDone)
        updateUser(updatedUser)
    }
    private fun updateUser(user: Userdata) {
        viewModelScope.launch {
            dataDao.update(user)
        }
    }

    fun setSong(song: Int) {
        selectedSong = song
    }
    fun getSong(): Int {
        return selectedSong
    }

    fun setInstrument(chosen: MutableList<Boolean>) {
        chosenInstrument = chosen
    }
    fun getChosenInstrument(idx: Int): Boolean {
        return chosenInstrument[idx]
    }

    fun done(
        userId: Int,
        userName: String,
        userPass: String,
        userEmail: String,
        userChoice1: Int,
        userChoice2: Int,
        joinDate: String,
        segmentsDone: Int
    )
    {
        val updatedUser = getUpdatedUserEntry(
            userId,
            userName,
            userPass,
            userEmail,
            userChoice1,
            userChoice2,
            joinDate,
            segmentsDone + 1
        )
        updateUser(updatedUser)
    }

}

class UserdataViewModelFactory(private val dataDao: UserdataDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(UserdataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserdataViewModel(dataDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun getWeekString(joinDate: String): Pair<MutableList<String>, Int> {
    Log.d("CHECKER", "getting week string with $joinDate")
    val weekString = mutableListOf<String>()

    val weekStart = mutableListOf<String>()
    val weekEnd = mutableListOf<String>()
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    val calendar: Calendar = Calendar.getInstance()
    val timeParsed = formatter.parse(joinDate)
    if(timeParsed == null)
        calendar.time = formatter.parse("2021.01.01")!!
    else
        calendar.time = timeParsed

    val nowDate: Calendar = Calendar.getInstance()

    var nowWeek = 0
    var week = 0
    repeat(5) {
        weekStart.add(formatter.format(calendar.time))
        if(nowDate == calendar) nowWeek = week
        repeat(6) {
            calendar.add(Calendar.DATE, 1)
            if(nowDate == calendar) nowWeek = week
        }
        weekEnd.add(formatter.format(calendar.time))
        calendar.add(Calendar.DATE, 1)
        weekString.add(weekStart[week] + " ~ " + weekEnd[week])
        week ++
    }
    return Pair(weekString, nowWeek)
}

fun getSegmentString(choice1: Int, choice2: Int): MutableList<String> {
    val scheduleString = mutableListOf<String>()
    val schedule = when(choice1) {
        2,3-> 1
        else-> if(choice2!=-1) 2 else 1
    }
    when(schedule) {
        1-> {
            scheduleString.add("1주차_송라이팅1")
            scheduleString.add("2주차_송라이팅2")
            scheduleString.add("3주차_송라이팅3")
            scheduleString.add("4주차_즉흥악기연주1")
            scheduleString.add("5주차_즉흥악기연주2")
        }
        2-> {
            scheduleString.add("1주차_즉흥악기연주1")
            scheduleString.add("2주차_즉흥악기연주2")
            scheduleString.add("3주차_즉흥악기연주3")
            scheduleString.add("4주차_송라이팅1")
            scheduleString.add("5주차_송라이팅2")
        }
    }
    return scheduleString
}