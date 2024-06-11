package com.zqc.itinerary

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.common.util.RSAKeyHelper
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyPair
import java.util.Base64

@RunWith(AndroidJUnit4::class)
class RSAKeyHelperTest {

    @Test
    fun testGenerateRSAKeyPair() {
        val keyPair: KeyPair = RSAKeyHelper.generateRSAKeyPair()
        assertNotNull("Key pair should not be null", keyPair)
        assertNotNull("Public key should not be null", keyPair.public)
        assertNotNull("Private key should not be null", keyPair.private)

        // 打印公钥和私钥
        val publicKeyString = Base64.getEncoder().encodeToString(keyPair.public.encoded)
        val privateKeyString = Base64.getEncoder().encodeToString(keyPair.private.encoded)
        println("Public Key: $publicKeyString")
        println("Private Key: $privateKeyString")
    }

    @Test
    fun testConvertKeyToPKCS8Format() {
        val keyPair: KeyPair = RSAKeyHelper.generateRSAKeyPair()
        val privateKeyPKCS8: String = RSAKeyHelper.convertKeyToPKCS8Format(keyPair.private)
        assertNotNull("PKCS#8 encoded private key should not be null", privateKeyPKCS8)

        // 打印PKCS#8格式的私钥
        println("Private Key (PKCS#8): $privateKeyPKCS8")
    }
}