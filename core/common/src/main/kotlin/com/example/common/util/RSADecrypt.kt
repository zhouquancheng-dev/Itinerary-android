package com.example.common.util

import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

object RSADecrypt {

    fun decrypt(cryptograph: String, privateKey: String): String {
        return try {
            val keySpec = PKCS8EncodedKeySpec(decodeBase64(privateKey))
            val priKey: PrivateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec)

            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, priKey)

            val b = decodeBase64(cryptograph)
            String(cipher.doFinal(b))
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
            "Decryption failed: Invalid key specification - ${e.message}"
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            "Decryption failed: No such algorithm - ${e.message}"
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
            "Decryption failed: No such padding - ${e.message}"
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
            "Decryption failed: Invalid key - ${e.message}"
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
            "Decryption failed: Illegal block size - ${e.message}"
        } catch (e: BadPaddingException) {
            e.printStackTrace()
            "Decryption failed: Bad padding - ${e.message}"
        } catch (e: Exception) {
            e.printStackTrace()
            "Decryption failed: ${e.message}"
        }
    }

    private fun decodeBase64(input: String): ByteArray {
        return Base64.getDecoder().decode(input)
    }
}
