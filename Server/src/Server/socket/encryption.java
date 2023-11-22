package Server.socket;

import javax.crypto.*;
import java.security.*;

public class encryption {
	private SecretKey secretKey;
	private PublicKey publicKey;
	private KeyPair keyPair;
	private byte[] encryptedKey;
	private Key decryptedKey;

	public encryption() {
		try {
			// Generate a RSA key pair
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
			
			// Generate a random AES key
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
			secretKey = keyGenerator.generateKey();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getEncryptedKey() {
		return encryptedKey;
	}

	public void setEncryptedKey(byte[] encryptedKey) {
		this.encryptedKey = encryptedKey;
	}

	public byte[] encryptData(String text) {
		try {
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
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
			aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedData = aesCipher.doFinal(encryptedData);
			
			return new String(decryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void encryptKey() {
		try {
			Cipher rsaCipher = Cipher.getInstance("RSA");
			rsaCipher.init(Cipher.WRAP_MODE, publicKey);
			encryptedKey = rsaCipher.wrap(secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void decryptKey() {
		try {
			Cipher rsaCipher = Cipher.getInstance("RSA");
			rsaCipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate());
			decryptedKey = rsaCipher.unwrap(encryptedKey, "AES", Cipher.SECRET_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
