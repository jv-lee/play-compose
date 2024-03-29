package com.lee.playcompose.service.helper

import java.util.*

/**
 * AutoService工具类 项目中通过ModuleService来获取其他模块提供服务
 * @author jv.lee
 * @date 2021/9/9
 */
object ModuleService {
    inline fun <reified T> find(): T {
        return ServiceLoader.load(T::class.java).iterator().next()
            ?: throw RuntimeException("${T::class.java.name} not module implements")
    }
}