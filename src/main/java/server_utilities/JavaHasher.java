/**
 * This is the class that will handle the hashing of passwords (or any other
 * secure peace of data). This class could/should incorporate multiple hashing
 * algorithms, but for now we will just start with md5.
 * 
 * Note, all of the algorithms should have a static variant that returns the
 * hashed String
 * 
 * @author Taylor Cressy, Aaron Caffrey
 * @version 2.0
 */
package server_utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class JavaHasher
{
	public static String	DIGEST_MD2		= "MD2";
	public static String	DIGEST_MD5		= "MD5";
	public static String	DIGEST_SHA_1	= "SHA-1";
	public static String	DIGEST_SHA_256	= "SHA-256";
	public static String	DIGEST_SHA_384	= "SHA-384";
	public static String	DIGEST_SHA_512	= "SHA-512";

	public static String	DIGEST_DEFAULT	= DIGEST_SHA_256;

	public static String md2(String message)
	{
		return HashString(message, DIGEST_MD2);
	}

	public static String md5(String message)
	{
		return HashString(message, DIGEST_MD5);
	}

	public static String sha1(String message)
	{
		return HashString(message, DIGEST_SHA_1);
	}

	public static String sha256(String message)
	{
		return HashString(message, DIGEST_SHA_256);
	}

	public static String sha384(String message)
	{
		return HashString(message, DIGEST_SHA_384);
	}

	public static String sha512(String message)
	{
		return HashString(message, DIGEST_SHA_512);
	}

	public static String HashString(String message)
	{
		return HashString(message, DIGEST_DEFAULT);
	}

	public static String HashString(String message, String algorithm)
	{

		String hash;

		if (message == null)
		{
			return null;
		}

		try
		{

			// get instance of MessageDigest
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

			// calculate hash for message
			messageDigest.update(message.getBytes(), 0, message.length());

			// transfer digest to byte array
			byte[] digest = messageDigest.digest();

			// convert to hex string
			StringBuffer stringBuffer = new StringBuffer();


			for (int i = 0; i < digest.length; i++)
			{
				String hexChar = Integer.toHexString(0xff & digest[i]);
				if (hexChar.length() < 2)
				{
					hexChar = "0" + hexChar;
				}
				stringBuffer.append(hexChar);
			}
			hash = stringBuffer.toString();

			return hash;
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException)
		{
			System.err.println("FATAL: Unable to pull the hash algorithm from the Java RE. This must be fixed to proceed.");
			noSuchAlgorithmException.printStackTrace();
			return null;
		}
	}
}
