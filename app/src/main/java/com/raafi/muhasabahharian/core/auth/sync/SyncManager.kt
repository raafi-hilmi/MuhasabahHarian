package com.raafi.muhasabahharian.core.auth.sync

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raafi.muhasabahharian.core.auth.session.UserSessionManager
import com.raafi.muhasabahharian.data.local.dao.MuhasabahDao
import com.raafi.muhasabahharian.data.remote.RemoteMuhasabahDataSource
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncManager @Inject constructor(
    private val session: UserSessionManager,
    private val dao: MuhasabahDao,
    private val repo: MuhasabahRepository,
) : ViewModel() {

    init {
        session.uidFlow
            .filterNotNull()
            .onEach { repo.refreshFromRemote() }
            .launchIn(viewModelScope)
    }
}
