package net.runelite.client.plugins.ChinBreakHandler.util;

import net.runelite.client.config.ConfigManager;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class ProfilesData
{
    private static final int iterations = 100000;

    private static String decryptText(byte[] enc, SecretKey aesKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if (enc == null || enc.length == 0) {
            return "";  // Return an empty string for empty byte array
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec newKey = new SecretKeySpec(aesKey.getEncoded(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, newKey);
        return new String(cipher.doFinal(enc));
    }

    public String getProfileData(ConfigManager configManager, char[] password) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String tmp = configManager.getConfiguration("piggyProfiles", "profilesData");
        if (tmp.startsWith("¬")) {
            tmp = tmp.substring(1);
            byte[] decoded = base64Decode(tmp);

            if (decoded.length % 16 != 0) {
                throw new IllegalStateException("Encrypted data length is not a multiple of 16 bytes");
            }

            return decryptText(decoded, getAesKey(configManager, password));
        }
        return tmp;
    }

    private static byte[] getSalt(ConfigManager configManager)
    {
        String salt = configManager.getConfiguration("piggyProfiles", "salt");
        if (salt.isEmpty())
        {
            return new byte[0];
        }
        return base64Decode(salt);
    }

    private static SecretKey getAesKey(ConfigManager configManager, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        if (getSalt(configManager).length == 0)
        {
            byte[] b = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(b);
            setSalt(b, configManager);
        }
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, getSalt(configManager), iterations, 128);
        return factory.generateSecret(spec);
    }

    private static void setSalt(byte[] bytes, ConfigManager configManager)
    {
        configManager.setConfiguration("piggyProfiles", "salt", base64Encode(bytes));
    }

    private static byte[] base64Decode(String data)
    {
        return Base64.getDecoder().decode(data);
    }

    private static String base64Encode(byte[] data)
    {
        return Base64.getEncoder().encodeToString(data);
    }
}