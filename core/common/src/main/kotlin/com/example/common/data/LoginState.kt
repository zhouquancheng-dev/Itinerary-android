package com.example.common.data

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.common.data.DatastoreKey.IS_LOGIN_STATUS
import com.example.common.data.DatastoreKey.TIM_USER_SIG
import com.example.common.util.sp.DataStoreUtils.getBooleanSync
import com.example.common.util.sp.DataStoreUtils.putBooleanSync
import com.example.common.util.sp.DataStoreUtils.removeSync

object LoginState {

    var isLoggedIn: Boolean
        get() = getBooleanSync(IS_LOGIN_STATUS)
        set(value) {
            putBooleanSync(IS_LOGIN_STATUS, value)
            if (!value) {
                removeSync(stringPreferencesKey(TIM_USER_SIG))
            }
        }

}