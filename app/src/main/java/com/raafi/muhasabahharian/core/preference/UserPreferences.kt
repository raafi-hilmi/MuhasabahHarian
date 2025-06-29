package com.raafi.muhasabahharian.core.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.raafi.muhasabahharian.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

object UserPreferences {
    private val UID_KEY       = stringPreferencesKey("uid")
    private val NAME_KEY      = stringPreferencesKey("name")
    private val EMAIL_KEY     = stringPreferencesKey("email")
    private val ANON_KEY      = stringPreferencesKey("anon")

    suspend fun save(context: Context, user: User) {
        context.dataStore.edit { prefs ->
            prefs[UID_KEY]   = user.uid
            prefs[NAME_KEY]  = user.name
            prefs[EMAIL_KEY] = user.email ?: ""
            prefs[ANON_KEY]  = user.isAnonymous.toString()
        }
    }

    fun observe(context: Context): Flow<User?> =
        context.dataStore.data.map { prefs ->
            val uid  = prefs[UID_KEY] ?: return@map null
            val name = prefs[NAME_KEY] ?: "Tamu"
            val email= prefs[EMAIL_KEY].takeIf { !it.isNullOrBlank() }
            val anon = prefs[ANON_KEY]?.toBoolean() ?: true
            User(uid, name, email, anon)
        }

    suspend fun clear(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}
