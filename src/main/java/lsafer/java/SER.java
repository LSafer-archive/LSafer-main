package lsafer.java;

import lsafer.annotation.Underdevelopment;
import lsafer.util.StringParser;
import org.apache.tools.ant.filters.StringInputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A parser for java {@link Serializable} objects.
 *
 * @author LSaferSE
 * @version 1
 * @since 02-Nov-2019
 */
@Underdevelopment(value = "Not working well")
public class SER extends StringParser {

	/**
	 *
	 */
	public static SER global = new SER();

	/**
	 * @param string
	 * @return
	 */
	public Serializable deserialize(String string) {
		try (StringInputStream sis = new StringInputStream(string);
			 ObjectInputStream ois = new ObjectInputStream(sis)) {
			return (Serializable) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return
	 */
	@SwitchingMethod(Serializable.class)
	public boolean is_serial(String string) {
		//return true;
		try {
			return new ObjectInputStream(new StringInputStream(string)).readObject() != null;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param serializable
	 * @return
	 */
	public String serialize(Serializable serializable) {
		StringBuilder string = new StringBuilder();

		//noinspection JavaDoc
		class InnerOutputStream extends OutputStream {
			@Override
			public void write(int i) {
				string.append((char) i);
			}
		}

		try (OutputStream is = new InnerOutputStream();
			 ObjectOutputStream oos = new ObjectOutputStream(is)) {
			oos.writeObject(serializable);
			return string.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
