package com.lee.playcompose.common.entity

import androidx.annotation.Keep

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
@Keep
data class AccountData(val coinInfo: CoinInfo, val userInfo: UserInfo)

@Keep
data class CoinInfo(
    val coinCount: Int,
    val level: Int,
    val nickname: String,
    val rank: String,
    val userId: Long,
    val username: String
)

@Keep
data class UserInfo(
    val id: Long,
    val username: String,
    val nickname: String,
    val password: String,
    val publicName: String,
    val email: String,
    val icon: String,
    val type: Int,
    val token: String,
    val admin: Boolean,
    val chapterTops: List<String>,
    val coinCount: Int,
    val collectIds: List<String>,
)

data class AccountViewState(
    val accountData: AccountData? = null,
    val isLogin: Boolean = false,
    val isLoading: Boolean = false
)

sealed class AccountViewEvent {
    data class LogoutSuccess(val message: String? = "") : AccountViewEvent()
    data class LogoutFailed(val message: String? = "") : AccountViewEvent()
}


sealed class AccountViewAction {
    object RequestAccountData : AccountViewAction()
    object RequestLogout : AccountViewAction()
    data class UpdateAccountStatus(val accountData: AccountData, val isLogin: Boolean) :
        AccountViewAction()
}