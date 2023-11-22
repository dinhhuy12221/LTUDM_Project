package Client.socket;

import javax.crypto.*;
import java.security.*;

public class encryption {
    private KeyPair keyPair;
    private Key decryptedKey;
    private byte[] encryptedKey;

    public encryption() {
        try {
            // Generate a RSA key pair
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public Key getDecryptedKey() {
        return decryptedKey;
    }

    public void setDecryptedKey(Key decryptedKey) {
        this.decryptedKey = decryptedKey;
    }

    public void decryptKey() {
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate());
			decryptedKey = rsaCipher.unwrap(getEncryptedKey(), "AES", Cipher.SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encryptData(String text) {
		try {
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, decryptedKey);
			byte[] encryptedData = aesCipher.doFinal(text.getBytes());
			return encryptedData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String decryptData(byte[] encryptedData) {
		try {
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE, decryptedKey);
			byte[] decryptedData = aesCipher.doFinal(encryptedData);
			
			return new String(decryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
    

}
