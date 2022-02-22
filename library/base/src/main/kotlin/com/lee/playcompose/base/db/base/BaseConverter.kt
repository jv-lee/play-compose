package com.lee.playcompose.base.db.base

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.lee.playcompose.base.net.HttpManager

/**
 * @author jv.lee
 * @date 2020/4/17
 * @description
 */
open class BaseConverter<T> {
    @TypeConverter
    fun stringJsonToList(value: String?): List<T>? {
        return HttpManager.getGson().fromJson(value, object : TypeToken<List<T>>() {}.type)
    }

    @TypeConverter
    fun stringListFormJson(list: List<T>?): String? {
        return HttpManager.getGson().toJson(list)
    }
}