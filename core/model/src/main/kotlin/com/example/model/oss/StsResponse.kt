package com.example.model.oss

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StsResponse(
    @SerialName("RequestId") val requestId: String,
    @SerialName("AssumedRoleUser") val assumedRoleUser: AssumedRoleUser,
    @SerialName("Credentials") val credentials: Credentials
) {
    @Serializable
    data class AssumedRoleUser(
        @SerialName("Arn") val arn: String,
        @SerialName("AssumedRoleId") val assumedRoleId: String
    )

    @Serializable
    data class Credentials(
        @SerialName("SecurityToken") val securityToken: String,
        @SerialName("Expiration") val expiration: String,
        @SerialName("AccessKeySecret") val accessKeySecret: String,
        @SerialName("AccessKeyId") val accessKeyId: String
    )
}