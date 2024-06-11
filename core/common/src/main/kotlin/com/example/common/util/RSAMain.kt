package com.example.common.util

import android.os.Build
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.util.Base64

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

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    val keyPair = generateRSAKeyPair()
    val publicKey: PublicKey = keyPair.public
    val privateKey: PrivateKey = keyPair.private

    println("Public Key: ${Base64.getEncoder().encodeToString(publicKey.encoded)}")
    println("Private Key (PKCS#8): ${convertKeyToPKCS8Format(privateKey)}")
}