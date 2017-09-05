/*
 * Copyright 2017 Gideon Mills
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.crim.inspiringfutures.Utilities;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hasher {
	
	public static byte[] hashBytes(String word) {
		try {
			return MessageDigest.getInstance("SHA-256").digest( word.getBytes( Charset.forName("UTF-8") ) );
		} catch (NoSuchAlgorithmException e) {
			// Shouldn't evener happen
			e.printStackTrace();
			return null;
		}
	}
	
	private static String bytesToHex(byte[] hash) {
	    StringBuffer hexString = new StringBuffer();
	    
	    for (byte hashByte : hash) {
		    String hex = Integer.toHexString(0xff & hashByte);
		    if(hex.length() == 1) hexString.append('0');
		    hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	public static String sha256Hash(String word) {
		return bytesToHex( hashBytes(word) );
	}
	
	public static void main(String[] args) {
		String word = args[0];
		String hash = sha256Hash(word);
		
		System.out.println(hash);
	}
	
}
