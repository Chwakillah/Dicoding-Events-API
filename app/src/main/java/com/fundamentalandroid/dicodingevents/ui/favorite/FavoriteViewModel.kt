package com.fundamentalandroid.dicodingevents.ui.favorite

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity
import com.fundamentalandroid.dicodingevents.data.repository.FavoriteRepository

@SuppressLint("StaticFieldLeak")
class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val context: Context = application.applicationContext

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> {
        _loading.value = true
        if (isNetworkAvailable()) {
            return mFavoriteRepository.getAllFavorites().also {
                _loading.value = false
            }
        } else {
            _loading.value = false
            _errorMessage.value = "Tidak ada koneksi internet."
            return MutableLiveData(emptyList())
        }
    }
}

