package com.ekinoks.followme.trackingutils.pbkdf2;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

class SecurePassword {

	public String generateStrongPasswordHash(String password) {

		int iterations = 1000;
		char[] chars = password.toCharArray();
		byte[] salt = getSalt();
		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
		SecretKeyFactory secretKeyFactory;

		try {

			secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
			return iterations + ":" + toHex(salt) + ":" + toHex(hash);

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

			e.printStackTrace();
		}

		return null;
	}

	public boolean validatePassword(String originalPassword, String storedPassword) {

		String[] parts = storedPassword.split(":");
		int iterations = Integer.parseInt(parts[0]);
		byte[] salt = null;
		byte[] hash = null;
		byte[] testHash = null;
		int diff;

		salt = fromHex(parts[1]);
		hash = fromHex(parts[2]);

		PBEKeySpec pbeKeySpec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);

		try {

			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			testHash = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();

		} catch (InvalidKeySpecException e) {

			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}

		diff = hash.length ^ testHash.length;

		for (int i = 0; i < hash.length && i < testHash.length; i++) {

			diff |= hash[i] ^ testHash[i];
		}

		return diff == 0;
	}

	private byte[] getSalt() {

		SecureRandom secureRandom;
		byte[] salt = new byte[16];

		try {

			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.nextBytes(salt);

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}

		return salt;
	}

	private String toHex(byte[] array) {

		BigInteger bigInteger = new BigInteger(1, array);
		String hex = bigInteger.toString(16);
		int paddingLength = (array.length * 2) - hex.length();

		if (paddingLength > 0) {

			return String.format("%0" + paddingLength + "d", 0) + hex;

		} else {

			return hex;
		}
	}

	private byte[] fromHex(String hex) {

		byte[] bytes = new byte[hex.length() / 2];

		for (int i = 0; i < bytes.length; i++) {

			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}

		return bytes;
	}
}
