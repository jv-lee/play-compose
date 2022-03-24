package com.lee.playcompose.service

import androidx.fragment.app.FragmentActivity
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.entity.AccountViewState
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description
 */
interface AccountService {

    fun getAccountViewStates(activity: FragmentActivity): AccountViewState

    fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent>

    suspend fun requestAccountInfo(activity: FragmentActivity)

    suspend fun requestLogout(activity: FragmentActivity)
}