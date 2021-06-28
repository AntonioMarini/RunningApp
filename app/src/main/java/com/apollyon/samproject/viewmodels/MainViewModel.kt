package com.apollyon.samproject.viewmodels

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollyon.samproject.data.*
import com.apollyon.samproject.repositories.RunRepository
import com.apollyon.samproject.utilities.LevelUtil
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainViewModel(application: Application) : ViewModel(){

    var authUser : FirebaseUser? = null
    private  val runRepository = RunRepository(application)

    var userFromRealtime : LiveData<User?> = runRepository.userFromRealtime
    lateinit var allUserSessions : LiveData<List<RunningSession>>
    lateinit var allMissions : LiveData<List<Mission>>

    lateinit var totalKm : LiveData<Int>
    lateinit var totalTime : LiveData<Long>
    lateinit var totalAvgSpeed : LiveData<Float>
    lateinit var totalCalories : LiveData<Int>

    //to hide/show the topbar and navbar
    private val _shouldHideBars = MutableLiveData<Boolean>(false)
    val shouldHideBars :LiveData<Boolean> get() = _shouldHideBars

    //per fare download/upload dell immagine del profilo
    private val _profileImageDownloaded = MutableLiveData<Uri>()
    val profileImageDownloaded : LiveData<Uri> get() = _profileImageDownloaded
    private val storageReference = FirebaseStorage.getInstance().reference
    var pendingSnackbarInfo = MutableLiveData<String>("")

    fun onUserLogged(uid:String){
        runRepository.setUserLogged(uid)
        authUser = Firebase.auth.currentUser
        userFromRealtime = runRepository.userFromRealtime
        allUserSessions = runRepository.getAllUserSessions()
        allMissions  = runRepository.getAllMissions()

        totalKm= runRepository.getTotalKm()
        totalTime = runRepository.getTotalTime()
        totalAvgSpeed = runRepository.getTotalAvgSpeed()
        totalCalories  = runRepository.getTotalCalories()
    }

    fun uploadImage(uri: Uri){
        val profileImageRef: StorageReference = storageReference.child("images/" + userFromRealtime.value?.uid + "/profile/profile.jpg")
        val uploadTask = profileImageRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            _profileImageDownloaded.value = uri
            getDownloadUrl(profileImageRef)
        }
    }

    // update immgine profilo
    private fun getDownloadUrl(reference : StorageReference){
        reference.downloadUrl.addOnSuccessListener {uri ->
            Log.i("DOWNLOAD", "download successful")
            updateProfileImage(uri)
        }
    }
    private fun updateProfileImage(uri : Uri){
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }
        authUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "User profile updated.")
            }
        }
    }

    fun addXp(xpGained: Long){

        var initialXpToNextLevel = runRepository.userFromRealtime.value!!.xpToNextLevel!!
        var xpRemaining = initialXpToNextLevel - xpGained; // puo essere negativo

        while(xpRemaining<0){
            levelUp()
            initialXpToNextLevel = runRepository.userFromRealtime.value!!.xpToNextLevel!!
            xpRemaining += initialXpToNextLevel
        }
        runRepository.userFromRealtime.value!!.xpToNextLevel =  xpRemaining;

        runRepository.updateRealtimeFromUser()
    }

    private fun levelUp(){
        if(runRepository.userFromRealtime.value!!.level != null) {
            runRepository.userFromRealtime.value!!.level = runRepository.userFromRealtime.value!!.level!! + 1
            val newLevel = runRepository.userFromRealtime.value!!.level!!
            runRepository.userFromRealtime.value!!.xpToNextLevel = LevelUtil.xpForNextLevel(newLevel)
        }
    }

    fun onRunFinished(totalkm : Int){
        if (allMissions.value != null) {
            for(m in allMissions.value!!){
                if(!m.complete!! && parseMissionCondition(m.condition!!, totalkm))
                    onMissionCompleted(m)
            }
        }
    }

    fun onMissionCompleted(m:Mission){
        m.complete = true
        runRepository.updateMission(m)
        addXp(m.xpReward!!)
        pendingSnackbarInfo.postValue(m.name!! + " has been completed! Gained: ${m.xpReward}xp")
    }

    /**
     * Called when the run fragment is opened
     * its used to hide the useless ui of the main activity
     */
    fun onHideBars(){
        _shouldHideBars.value = true
    }

    fun onShowBars(){
        _shouldHideBars.value = false
    }
    /*-------------------------------------Missions Conditions-------------------------------------------------*/

    fun parseMissionCondition(condition: String, totalkm : Int) : Boolean{
        val tokens = condition.split(" ")
        return when(tokens[0]){
            "run" -> missionRunCondition(tokens.subList(1,tokens.size), totalkm)
            // altri eventuali tipi di condizione
            else -> false
        }
    }

    private fun missionRunCondition(tokens : List<String>, totalkm : Int) : Boolean{
        return when(tokens[0]){
            "distance" -> missionRunDistanceCondition(tokens.subList(1,tokens.size), totalkm)
            // altri eventuali parametri sulla condizione
            else -> false
        }
    }

    private fun missionRunDistanceCondition(tokens : List<String>, totalkm : Int) : Boolean{
        val operator = tokens[0]
        val value = tokens[1]

        return when(operator){
            ">" -> totalkm >=  value.toInt()
            //altri eventuali operatori
            else -> false
        }
    }

    fun clearSessions() {
        runRepository.clearSessions()
    }

    fun insertNewUserLocal(user: User) {
        runRepository.insertNewUserLocal(user)
    }

    fun insertSession(session: RunningSession) {
        runRepository.insertSession(session)
    }
    //altre funzioni per altri tipi di missioni che hanno condizioni su utenti per esempio...
}