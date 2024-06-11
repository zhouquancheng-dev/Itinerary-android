package com.example.common.util

import android.os.Build
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.Security
import java.util.Base64

object RSAKeyHelper {

    fun generateRSAKeyPair(): KeyPair {
        Security.addProvider(BouncyCastleProvider())
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
        keyPairGenerator.initialize(1024) // 设置密钥长度为1024位
        return keyPairGenerator.generateKeyPair()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertKeyToPKCS8Format(privateKey: PrivateKey): String {
        val pkcs8EncodedKey = privateKey.encoded
        return Base64.getEncoder().encodeToString(pkcs8EncodedKey)
    }
}