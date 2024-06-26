package org.piramalswasthya.cho.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.piramalswasthya.cho.database.converters.MasterDataListConverter
import org.piramalswasthya.cho.database.room.dao.HealthCenterDao
import org.piramalswasthya.cho.database.room.dao.UserDao
import org.piramalswasthya.cho.model.CounsellingProvided
import org.piramalswasthya.cho.model.DrugFormMaster
import org.piramalswasthya.cho.model.DrugFrequencyMaster
import org.piramalswasthya.cho.model.HigherHealthCenter
import org.piramalswasthya.cho.model.ItemMasterList
import org.piramalswasthya.cho.network.AmritApiService
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject



class DoctorMasterDataMaleRepo @Inject constructor(
    private val userDao: UserDao,
    private val healthCenterDao: HealthCenterDao,
    private val amritApiService: AmritApiService

){
//    private var visitCategoryID: Int = 6
//    private var providerServiceMapID: Int = -1
//    var gender : String = "Male"
//    private var vanID : Int =-1
//    private var facilityID : Int =-1
    var apiKey : String = "f5e3e002-8ef8-44cd-9064-45fbc8cad6d5"
    init {
        // Observe the LiveData to get the VAN ID and Facility ID
//        userDao.getLoggedInUserVanID().observeForever{ vanId ->
//            vanID = vanId ?: -1
//        }
//        userDao.getLoggedInUserProviderServiceMapId().observeForever{ vanId ->
//            providerServiceMapID = vanId ?: -1
//        }
//        userDao.getLoggedInUserFacilityID().observeForever { facilityId ->
//            facilityID = facilityId ?: -1
//        }
    }
    suspend fun getDoctorMasterMaleData() {
        try {
            val vanID = userDao.getLoggedInUserVanID()
            val facilityID = userDao.getLoggedInUserFacilityID()
            val providerServiceMapID = userDao.getLoggedInUserProviderServiceMapId()
            val visitCategoryID = 6
            val gender = "Male"

            val response = amritApiService.getDoctorMasterData(
                visitCategoryID, providerServiceMapID,
                gender,facilityID,vanID, apiKey
            )

            if (response.code() == 200) {
                val responseString = response.body()?.string()
                val responseJson = responseString?.let { JSONObject(it) }
                val jsonObject = responseJson?.getJSONObject("data")!!

                val higherHealthCenterListString = jsonObject.getJSONArray("higherHealthCare")
                val higherHealthCenterList = MasterDataListConverter.toHealthCenterList(higherHealthCenterListString.toString())
                saveHigherHealthCenterToCache(higherHealthCenterList)

                val drugFormMasterListString = jsonObject.getJSONArray("drugFormMaster")
                val drugFormMasterList = MasterDataListConverter.toDrugFormMasterList(drugFormMasterListString.toString())
                saveDrugFormMasterListToCache(drugFormMasterList)

                val itemMasterListString = jsonObject.getJSONArray("itemMaster")
                val itemMasterList = MasterDataListConverter.toItemMasterList(itemMasterListString.toString())
                itemMasterList.forEach {
                    val itemFormName = healthCenterDao.getItemFormNameByID(it.itemFormID)
                    it.dropdownForMed = "${if(itemFormName==null) "" else "$itemFormName "}${it.itemName} ${it.strength?:""}${it.unitOfMeasurement?:" "}"
                }
                saveItemMasterListToCache(itemMasterList)

                val drugFrequencyMasterListString = jsonObject.getJSONArray("drugFrequencyMaster")
                val drugFrequencyMasterList = MasterDataListConverter.toDrugFrequencyMasterList(drugFrequencyMasterListString.toString())
                saveDrugFrequencyMasterListToCache(drugFrequencyMasterList)

                val counsellingTypesListString = jsonObject.getJSONArray("counsellingProvided")
                val counsellingTypesMasterList = MasterDataListConverter.toCounsellingTypeMasterList(counsellingTypesListString.toString())
                saveCounsellingTypesMasterListToCache(counsellingTypesMasterList)

            }
        } catch (e: Exception) {
            Log.i("Error in Fetching Doctor Master Data", "$e")
        }
    }


    private suspend fun saveDrugFormMasterListToCache(drugFormMaster: List<DrugFormMaster>){
        try{
            drugFormMaster.forEach { drug:DrugFormMaster ->
                healthCenterDao.insertDrugFormMasterList(drug)
            }
        } catch (e: Exception){
            Timber.d("Error in saving Drug Form Master List data $e")
        }
    }

    private suspend fun saveCounsellingTypesMasterListToCache(counsellingTypes: List<CounsellingProvided>){
        try{
            counsellingTypes.forEach { counselling:CounsellingProvided ->
                healthCenterDao.insertCounsellingTypeMasterList(counselling)
            }
        } catch (e: Exception){
            Timber.d("Error in saving Counselling Types Master List data $e")
        }
    }

    private suspend fun saveDrugFrequencyMasterListToCache(drugFrequencyMaster: List<DrugFrequencyMaster>){
        try{
            drugFrequencyMaster.forEach { drugItem:DrugFrequencyMaster ->
                healthCenterDao.insertDrugFrequencyMasterList(drugItem)
            }
        } catch (e: Exception){
            Timber.d("Error in saving Drug Frequency Master List data $e")
        }
    }

    private suspend fun saveItemMasterListToCache(itemMasterList: List<ItemMasterList>){
        try{
            itemMasterList.forEach { itemM:ItemMasterList ->
                healthCenterDao.insertItemMasterList(itemM)
            }
        } catch (e: Exception){
            Timber.d("Error in saving Item Master List data $e")
        }
    }

    private suspend fun saveHigherHealthCenterToCache(higherHealthCenter:List<HigherHealthCenter>){
        try{
            higherHealthCenter.forEach { healthCenter:HigherHealthCenter ->
                healthCenterDao.insertHealthCenter(healthCenter)
            }
        } catch (e: Exception){
            Timber.d("Error in saving Higher health center data $e")
        }
    }

     fun getAllItemMasterList(): LiveData<List<ItemMasterList>> {
        return healthCenterDao.getAllItemMasterList()
    }

    fun getAllCounsellingList(): LiveData<List<CounsellingProvided>> {
        return healthCenterDao.getAllCounsellingProvided()
    }

    fun getHigherHealthCenter(): LiveData<List<HigherHealthCenter>> {
                return healthCenterDao.getHealthCenter()
    }

    fun getItemMasterListById(id:Int): ItemMasterList {
        return healthCenterDao.getItemMasterListById(id)
    }

    suspend fun getHigherHealthTypeByNameMap():Map<Int,String>{
        return healthCenterDao.getHigherHealthMap().associate {
            it.institutionID to it.institutionName
        }
    }
}
