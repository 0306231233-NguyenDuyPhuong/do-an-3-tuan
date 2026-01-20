package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.view.avatarUrl2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class EditProfileViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val _updateUser = MutableLiveData<Boolean>()
    val updateUser: LiveData<Boolean> get() = _updateUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun updateUserFull(token: String, full_name: String, file: File?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var avatarUrl: String? = null
                if (file != null) {
                    avatarUrl = uploadOneImageSuspend(token, file)
                    if (avatarUrl == null) {
                        _error.postValue("UPLOAD_FAILED")
                        _updateUser.postValue(false)
                        return@launch
                    }
                    avatarUrl2 = avatarUrl
                }

                updateUserAPI(token, full_name, avatarUrl)
                _updateUser.postValue(true)

            } catch (e: Exception) {
                e.printStackTrace()
                _updateUser.postValue(false)
            }
        }
    }

    // 2. Hàm gọi API /user/update
    private fun updateUserAPI(token: String, full_name: String, avatar: String?) {
        Log.d("Test", "--- BẮT ĐẦU updateUserAPI ---")
        try {
            val jsonObject = JSONObject()
            jsonObject.put("full_name", full_name)
            jsonObject.put("phone", JSONObject.NULL)
            jsonObject.put("gender", JSONObject.NULL)
            if (avatar != null) {
                jsonObject.put("avatar", avatar)
            }
            val requestBodyString = jsonObject.toString()
            val mediaType = "application/json;charset=utf-8".toMediaType()
            val requestBody = requestBodyString.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/user/update")
                .addHeader("Authorization", "Bearer $token")
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) {
                    val bodyString = resp.body?.string().orEmpty()
                    Log.d("Test", "Update User Success: $bodyString")
                } else {
                    Log.e("Test", "Update User Failed: ${resp.code} - ${resp.message}")
                    if (resp.code == 403) {
                        _error.postValue("TOKEN_EXPIRED")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Test", "CRASH updateUserAPI: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun uploadOneImageSuspend(token: String, file: File): String? {
        var resultUrl: String? = null
        try {
            val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            multipartBuilder.addFormDataPart("images", file.name, requestFile)
            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/images/upload")
                .addHeader("Authorization", "Bearer $token")
                .post(multipartBuilder.build())
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val jsonResponse = JSONObject(bodyString ?: "")
                    if (jsonResponse.has("files")) {
                        val filesArray = jsonResponse.getJSONArray("files")
                        if (filesArray.length() > 0) {
                            resultUrl = filesArray.getString(0)
                        }
                    }
                } else {
                    Log.e("Test", "Upload Failed Code: ${response.code}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultUrl
    }
}