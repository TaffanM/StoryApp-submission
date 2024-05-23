package com.taffan.storyapp.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.taffan.storyapp.data.response.LoginResult
import kotlinx.coroutines.flow.map
import java.util.concurrent.Flow


val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user")
class UserPreferences private constructor(
    private val dataStore: DataStore<Preferences>
){
    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
        val NAME = stringPreferencesKey("name")
        val USER_ID = stringPreferencesKey("userId")
    }

    fun getUser(): kotlinx.coroutines.flow.Flow<LoginResult?> {
        return dataStore.data.map { preferences ->
            val token = preferences[PreferencesKeys.TOKEN]
            val name = preferences[PreferencesKeys.NAME]
            val userId = preferences[PreferencesKeys.USER_ID]

            if (token != null && name != null && userId != null) {
                LoginResult(name = name, userId = userId, token = token)
            } else {
                null
            }
        }
    }

    suspend fun saveUser(loginResult: LoginResult) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = loginResult.token
            preferences[PreferencesKeys.NAME] = loginResult.name
            preferences[PreferencesKeys.USER_ID] = loginResult.userId
        }
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}