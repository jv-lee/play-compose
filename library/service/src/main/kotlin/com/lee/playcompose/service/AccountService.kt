package com.lee.playcompose.service

import androidx.fragment.app.FragmentActivity
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.service.core.IModuleService
import kotlinx.coroutines.flow.Flow

/**
 * 账户模块对外提供功能服务类
 * @author jv.lee
 * @date 2022/3/23
 */
interface AccountService : IModuleService {

    /**
     * 获取账户viewState，其他模块渲染账户相关ui时根据该state控制
     * @param activity
     */
    fun getAccountViewStates(activity: FragmentActivity): AccountViewState

    /**
     * 获取账户viewEvent，其他模块监听该事处理制账户事件
     */
    fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent>

    /**
     * 请求全局AccountViewModel 请求账户信息
     * @param activity
     */
    fun requestAccountInfo(activity: FragmentActivity)

    /**
     * 请求全局AccountViewModel 请求登出
     * @param activity
     */
    fun requestLogout(activity: FragmentActivity)

    /**
     * 清除本地登陆状态
     * @param activity
     */
    fun clearLoginState(activity: FragmentActivity)

    /**
     * 获取当前登陆的账户信息
     */
    fun getAccountInfo(): AccountData?

    /**
     * 获取当前登陆用户id
     */
    fun getUserId(): Long

    /**
     * 获取当前是否已登陆
     */
    fun isLogin(): Boolean
}