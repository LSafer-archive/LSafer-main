package lsafer.java;

import lsafer.util.StringParser;

import java.io.*;
import java.util.Base64;

/**
 * Parses ans stringify the serializable objects from and to {@link Base64}.
 *
 * @author LSaferSE
 * @version 2 release (03-Nov-2019)
 * @since 02-Nov-2019
 */
public class SERB64 extends StringParser {
	/**
	 * The global instance to avoid unnecessary instancing.
	 */
	public static SERB64 global = new SERB64();

	/**
	 * Parse the given string (written on base64) to a serializable object.
	 *
	 * @param string to be deserialize
	 * @return a serializable object from the given string
	 */
	@ParsingMethod
	public Serializable deserialize(String string) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(string));
			 ObjectInputStream ois = new ObjectInputStream(bais)) {
			return (Serializable) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Check if the given string is written on base64 or not.
	 * ALWAYS RETURNS TRUE!
	 * <br/>
	 * this made just to trigger the super class.
	 *
	 * @param string to be checked
	 * @return true
	 */
	@SwitchingMethod(Serializable.class)
	public boolean is_serial(String string) {
		return true;
	}

	/**
	 * Stringify the given serializable using base64.
	 *
	 * @param serializable to be stringed
	 * @return a base64 string from the given serializable
	 */
	@StringingMethod
	public String serialize(Serializable serializable) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(serializable);
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
