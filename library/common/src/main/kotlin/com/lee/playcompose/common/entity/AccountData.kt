/*
 * 账户类实体类 及账户viewModel ViewState、ViewAction、ViewEvent
 * @author jv.lee
 * @date 2021/11/25
 */
package com.lee.playcompose.common.entity

import androidx.annotation.Keep
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState

/**
 * 账户信息
 * @param coinInfo 积分信息
 * @param userInfo 用户信息
 */
@Keep
data class AccountData(val coinInfo: CoinInfo, val userInfo: UserInfo)

/**
 * 积分信息
 * @param coinCount 积分数
 * @param level 积分等级
 * @param nickname 用户昵称
 * @param userId 用户id
 * @param username 账号
 */
@Keep
data class CoinInfo(
    val coinCount: Int,
    val level: Int,
    val nickname: String,
    val rank: String,
    val userId: Long,
    val username: String
)

/**
 * 用户信息
 * @param id
 * @param username 账号
 * @param nickname 用户昵称
 * @param password 密码
 * @param publicName 默认账户名称
 * @param email 账户邮箱
 * @param icon 头像地址
 * @param type
 * @param token 账户登陆token
 * @param admin 是否管理员
 * @param chapterTops
 * @param coinCount 积分数量
 * @param collectIds
 */
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
    val collectIds: List<String>
)

data class AccountViewState(
    val accountData: AccountData? = null,
    val isLogin: Boolean = false,
    val isLoading: Boolean = false
) : IViewState

sealed class AccountViewEvent : IViewEvent {
    data class LogoutSuccess(val message: String? = "") : AccountViewEvent()
    data class LogoutFailed(val message: String? = "") : AccountViewEvent()
}

sealed class AccountViewIntent : IViewIntent {
    object ClearLoginState : AccountViewIntent()
    object RequestAccountData : AccountViewIntent()
    object RequestLogout : AccountViewIntent()
    data class UpdateAccountStatus(val accountData: AccountData, val isLogin: Boolean) :
        AccountViewIntent()
}