package com.example.login.obj

import com.example.common.util.DataStoreUtils

object LoginState {

    private var dataStore = DataStoreUtils
    private const val IS_LOGIN = "is_login"

    /**
     * 初始化DataStoreUtils
     */
    fun initialize(dataStoreUtils: DataStoreUtils) {
        dataStore = dataStoreUtils
    }

    /**
     * 判断是否已登录
     * @return 已登录返回true，未登录返回false
     */
    var login: Boolean
        get() {
            return dataStore.getBooleanSync(IS_LOGIN)
        }
        set(value) {
            dataStore.putBooleanSync(IS_LOGIN, value)
        }

    /**
     * 注销登录并删除token
     */
    fun logout() {
        login = false
    }

}