package com.apollyon.samproject.repositories

import android.app.Application
import android.content.ContentValues
import android.se.omapi.Session
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollyon.samproject.data.*
import com.apollyon.samproject.utilities.LevelUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class RunRepository(application: Application) {

    private var runsDao: RunDao
    private lateinit var allRuns : LiveData<List<RunningSession>>


    private var usersDao: UsersDao
    private lateinit var currentUser : LiveData<User>

    private var missionsDao: MissionsDao
    private lateinit var allMissions : LiveData<List<Mission>>

    private lateinit var totalMeters : LiveData<Int>
    private lateinit var totalTime : LiveData<Long>
    private lateinit var totalAvgSpeed : LiveData<Float>
    private lateinit var totalCalories : LiveData<Int>

    // Firebase

    private lateinit var userRealtimeReference : DatabaseReference
    private val _userFromRealtime = MutableLiveData<User?>()
    val userFromRealtime : LiveData<User?> get() = _userFromRealtime

    //job per coroutines operano su room
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // listener che ascolta gli update all'utente da firebase
    private val userListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI and the room database
            dataSnapshot.getValue<User>()?.let { updateUserLocal(it) }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    init {
        val database = RunningDatabase.getInstance(application)

        runsDao = database.runDao
        usersDao = database.userDao
        missionsDao = database.missionsDao
    }

    fun setUserLogged(uid: String){
        userRealtimeReference = FirebaseDatabase.getInstance().getReference("users").child(uid)
        // prendo user da realtime database
        userRealtimeReference.get().addOnSuccessListener {
            _userFromRealtime.value = it.getValue<User>()
            insertNewUserLocal(_userFromRealtime.value!!)
        }

        userRealtimeReference.addValueEventListener(userListener)

        // inizializzo i campi che mi servono usando i dao
        currentUser = usersDao.getUser(uid)
        allRuns = runsDao.getAllRunsByAvgSpeed(uid)
        allMissions = missionsDao.getAllMissions()

        totalMeters = runsDao.getTotalRunsDistance(uid)
        totalAvgSpeed = runsDao.getTotalRunsAvgSpeed(uid)
        totalCalories = runsDao.getRunsTotalCal(uid)
        totalTime = runsDao.getTotalTimeInMillis(uid)
    }

    fun updateRealtimeFromUser(){
        userRealtimeReference.setValue(_userFromRealtime.value!!)
    }

    //SESSIONS

    /*-------------------------------------ROOM-------------------------------------------------*/
    // TODO metti tuttp in un repository

    // comunicazione col database -> uso coroutines

    //SESSIONS

    fun clearSessions(){
        uiScope.launch {
            clearAllDao(userFromRealtime.value!!.uid)
        }
    }

    fun insertSession(session : RunningSession){
        session.user = userFromRealtime.value!!.uid
        uiScope.launch {
            insertSessionDao(session)
        }
    }

    //USERS

    fun insertNewUserLocal( user: User){
        uiScope.launch {
            insertUserDao(user)
        }
    }

    fun updateUserLocal( user: User){
        uiScope.launch {
            updateUserDao(user)
        }
    }

    // MISSIONS

    fun insertMission(mission: Mission){
        uiScope.launch {
            insertMissionDao(mission)
        }
    }

    fun insertMissions(missions: List<Mission>){
        uiScope.launch {
            insertMissionsDao(missions)
        }
    }

    fun updateMission(mission: Mission){
        uiScope.launch {
            updateMissionDao(mission)
        }
    }

    /**
     * insert session in the database
     * @param session session to add
     */
    private suspend  fun insertSessionDao(session: RunningSession){
        // insert missing data in the session like the user id
        withContext(Dispatchers.IO){
            runsDao.insertRun(session)
        }
    }

    private suspend fun clearAllDao(uid : String?){
        withContext(Dispatchers.IO){
            runsDao.deleteAllRunsOfUser(uid)
        }
    }

    // MISSIONS

    private suspend fun insertMissionDao(mission: Mission){
        withContext(Dispatchers.IO){
            missionsDao.insertMission(mission)
        }
    }

    private suspend fun insertMissionsDao(missions: List<Mission>){
        withContext(Dispatchers.IO){
            missionsDao.insertManyMissions(missions)
        }
    }

    private suspend fun updateMissionDao(mission: Mission){
        withContext(Dispatchers.IO){
            missionsDao.updateMission(mission)
        }
    }

    // USERS

    private suspend fun insertUserDao(user: User){
        withContext(Dispatchers.IO){
            usersDao.insert(user)
        }
    }

    suspend fun updateUserDao(user: User){
        withContext(Dispatchers.IO){
            usersDao.update(user)
        }
    }

    fun getAllUserSessions(): LiveData<List<RunningSession>> {
        return allRuns
    }

    fun getAllMissions(): LiveData<List<Mission>> {
        return allMissions
    }

    fun getTotalKm():LiveData<Int>{
        return totalMeters
    }

    fun getTotalTime(): LiveData<Long> {
        return totalTime
    }

    fun getTotalAvgSpeed(): LiveData<Float> {
        return  totalAvgSpeed
    }

    fun getTotalCalories(): LiveData<Int> {
        return totalCalories
    }
}