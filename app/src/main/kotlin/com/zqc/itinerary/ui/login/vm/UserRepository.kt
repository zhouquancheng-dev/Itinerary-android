package com.zqc.itinerary.ui.login.vm

import android.util.Log
import com.example.common.util.DataStoreUtils
import com.example.network.ItineraryNetwork
import com.example.model.UserResponse
import com.example.model.SmsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val network: ItineraryNetwork
) {

    private val userRepositoryTag = UserRepository::class.java.simpleName

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return Flow<UserResponse<String?>>
     */
    fun login(username: String, password: String): Flow<UserResponse<String?>> = flow {
        val token = DataStoreUtils.getStringSync("")
        val userResponse = network.login(token, username, password)
        emit(userResponse)
    }.flowOn(Dispatchers.IO).catch { e ->
        Log.e(userRepositoryTag, "登录请求发生错误: ${e.message}", e)
    }

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @return Flow<UserResponse<Any?>>
     */
    fun register(username: String, password: String): Flow<UserResponse<Any?>> = flow {
        val userResponse = network.register(username, password)
        emit(userResponse)
    }.flowOn(Dispatchers.IO).catch { e ->
        Log.e(userRepositoryTag, "注册请求发生错误: ${e.message}", e)
    }

    /**
     * 用户退出登录
     */
    fun logout(): Flow<UserResponse<Any?>> = flow {
        val token = DataStoreUtils.getStringSync("")
        emit(network.logout(token))
    }.flowOn(Dispatchers.IO).catch { e ->
        Log.e(userRepositoryTag, "退出登录请求发生错误: ${e.message}", e)
    }

    /**
     * 自动登录
     */
    fun autoLogin(): Flow<UserResponse<String?>> = flow {
        val token = DataStoreUtils.getStringSync("")
        emit(network.autoLogin(token))
    }.flowOn(Dispatchers.IO).catch { e ->
        Log.e(userRepositoryTag, "自动登录请求时发生错误: ${e.message}", e)
    }

    /**
     * 短信验证码校验
     * @param phoneNumber 手机号码
     * @param smsCode 验证码
     * @param bizId 发送回执id
     * @return BaseResponse<SmsResponse>
     */
    fun verifySmsCode(
        phoneNumber: String, smsCode: String, bizId: String
    ): Flow<UserResponse<SmsResponse?>> = flow {
        val userResponse = network.checkSms(phoneNumber, smsCode, bizId)
        emit(userResponse)
    }.flowOn(Dispatchers.IO).catch { e ->
        Log.e(userRepositoryTag, "短信验证码校验发生错误: ${e.message}", e)
    }

    /**
     * 极验验证码二次校验
     * @param captchaId 验证 id
     * @param lotNumber 验证流水号
     * @param captchaOutput 验证输出信息
     * @param passToken 验证通过标识
     * @param genTime 验证通过时间戳
     * @param action 客户端验证行为，如: login
     * @return Boolean 校验成功 true 否则 false
     */
    fun verifyCaptcha(
        captchaId: String,
        lotNumber: String,
        captchaOutput: String,
        passToken: String,
        genTime: String,
        action: String
    ): Flow<Boolean> = flow {
        val captchaResponse =
            network.verifyCaptcha(captchaId, lotNumber, captchaOutput, passToken, genTime, action)
        emit(captchaResponse)
    }.flowOn(Dispatchers.IO).catch { e ->
        Log.e(userRepositoryTag, "极验验证码二次校验请求错误: ${e.message}", e)
    }
}