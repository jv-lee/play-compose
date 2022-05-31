package com.lee.playcompose.service

import androidx.fragment.app.FragmentActivity
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.entity.AccountViewState
import kotlinx.coroutines.flow.Flow

/**
 * 账户模块对外提供功能服务类
 * @author jv.lee
 * @date 2022/3/23
 */
interface AccountService {

    fun getAccountViewStates(activity: FragmentActivity): AccountViewState

    fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent>

    suspend fun requestAccountInfo(activity: FragmentActivity)

    suspend fun requestLogout(activity: FragmentActivity)

    fun getAccountInfo(): AccountData?

    fun getUserId(): Long

    fun isLogin(): Boolean
}