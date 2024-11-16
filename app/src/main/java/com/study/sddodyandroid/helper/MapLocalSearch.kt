package com.study.sddodyandroid.helper

import android.content.Context
import com.study.sddodyandroid.dto.LocationDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject


//경남 밀양시
suspend fun getSearchResult(
    context : Context,
    inputText: String,
    searchResultList: MutableList<LocationDto>
): Unit {
    val apiBaseUrl = "https://places.googleapis.com/v1/places:searchText"

//    val apiKey = context.resources.getString(R.string.MAPS_API_KEY)
    val apiKey = "AIzaSyC9DkPCNItmBLga7QJ0trnWXRs9hbCVIfY"


    val client = OkHttpClient()

//    }
//    "maxResultCount": 10,
//    "textQuery": ${JSONObject.quote(inputText)},

//    picy Vegetarian Food in Sydney, Australia
    val jsonBody = """
        {
          "textQuery": "$inputText",
          "languageCode": "ko",
          "maxResultCount": 10,
        }
        """.trimIndent()


    val request = Request.Builder()
        .url(apiBaseUrl)
        .post(jsonBody.toRequestBody("application/json".toMediaTypeOrNull()))
        .header("X-Goog-Api-Key", "$apiKey")
        .header("Content-Type", "application/json")
        .header("X-Goog-FieldMask","places.displayName,places.formattedAddress,places.location")
        .build()


    try {
        val response: Response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }

        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            val result = JSONObject(responseBody)
            val placesArray = result.getJSONArray("places")
            val tmpList = mutableListOf<LocationDto>()

            for (i in 0 until placesArray.length()) {
                val place = placesArray.getJSONObject(i)

                val formattedAddress = place.getString("formattedAddress")
                val displayName = place.getJSONObject("displayName").getString("text")
                val location = place.getJSONObject("location")
                val latitude = location.getDouble("latitude")
                val longitude = location.getDouble("longitude")

                val locationDto = LocationDto(displayName, formattedAddress, latitude, longitude)
                tmpList.add(locationDto)
            }

            withContext(Dispatchers.Main) {
                // Main 스레드에서 실행하여 UI 갱신
                searchResultList.clear()
                searchResultList.addAll(tmpList)
            }

        } else {
            println("Error: ${response.code}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Exception: ${e.message}")
        throw e // 예외를 다시 던지면 호출한 곳에서 처리 가능
    }
}
