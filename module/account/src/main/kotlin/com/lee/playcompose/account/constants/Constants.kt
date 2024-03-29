package com.lee.playcompose.account.constants

/**
 * 账户信息存储常量
 * @author jv.lee
 * @date 2021/11/25
 */
object Constants {

    /** 账户信息缓存key */
    const val CACHE_KEY_ACCOUNT_DATA = "cacheKey:account-data"

    /** 登陆状态sp存储key */
    const val SP_KEY_IS_LOGIN = "spKey:is-login"

    /** 登陆用户名sp存储key *（用于下次登陆自动输入） */
    const val SP_KEY_SAVE_INPUT_USERNAME = "spKey:save-input-username"

}