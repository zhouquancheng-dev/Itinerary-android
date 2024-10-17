package com.example.login.vm

import android.content.Context
import com.example.common.data.DatastoreKey.TIM_USER_ID
import com.example.common.data.DatastoreKey.TIM_USER_SIG
import com.example.common.di.AppDispatchers.*
import com.example.common.di.Dispatcher
import com.example.common.util.sp.DataStoreUtils.getStringSync
import com.example.common.util.sp.DataStoreUtils.putStringSync
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher
) {

    fun getCachedUserSig() = getStringSync(TIM_USER_SIG)

    fun cacheUserSig(userSig: String?) {
        if (userSig != null) {
            putStringSync(TIM_USER_SIG, userSig)
        }
    }

    fun putIMUserId(userId: String) = putStringSync(TIM_USER_ID, userId)

    suspend fun readAssetFile(context: Context, fileName: String): String {
        return withContext(ioDispatcher) {
            context.assets.open(fileName)
                .bufferedReader()
                .use { bfr ->
                    bfr.readText()
                }
        }
    }

}