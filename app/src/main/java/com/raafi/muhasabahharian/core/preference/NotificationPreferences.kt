package com.raafi.muhasabahharian.core.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("notification_prefs")

object NotificationPreferences {
    private val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")

    fun getNotifEnabled(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[NOTIF_ENABLED] ?: false
        }
    }

    suspend fun isNotifEnabled(context: Context): Boolean {
        return context.dataStore.data.first()[NOTIF_ENABLED] ?: false
    }

    suspend fun setNotifEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIF_ENABLED] = enabled
        }
    }
}
