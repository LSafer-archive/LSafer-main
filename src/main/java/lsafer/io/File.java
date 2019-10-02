/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package lsafer.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lsafer.json.JSON;
import lsafer.util.Arrays;
import lsafer.util.Loop;
import lsafer.util.StringParser;
import lsafer.util.Strings;

/**
 * A {@link java.io.File} with useful tools.
 *
 * @author LSaferSE
 * @version 9 release (28-sep-2019)
 * @since 18 May 2019
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class File extends java.io.File {
	/**
	 * The real this.
	 * Because some operations needs to replace the hole object to effect results.
	 * Such as renaming or moving the file.
	 */
	public File self = this;

	/**
	 * This file's title without copy number.
	 * <br><br><b>example:</b>
	 * <pre>
	 * title :      "title (1)"
	 * cleanTitle : "title"
	 * </pre>
	 */
	private String clean_title;

	/**
	 * if this file's name starts with dot.
	 * <br>
	 * normal :     "file"
	 * dotHidden :  ".file"
	 */
	private Boolean dot_hidden;

	/**
	 * This file's type extension.
	 * <br><br><b>example:</b>
	 * <pre>
	 * name :       "title.extension"
	 * extension :  "extension"
	 * </pre>
	 */
	private String extension;

	/**
	 * General mime Of this file.
	 * <br><br><b>example:</b>
	 * <pre>
	 *     "image/png"
	 * </pre>
	 */
	private String mime;

	/**
	 * This file's copy number. In case there is other files with the same name.
	 */
	private int suffix = 1;

	/**
	 * This file's name without type extension.
	 * <br><br><b>example:</b>
	 * <pre>
	 * name :   "title.extension"
	 * title :  "title"
	 * </pre>
	 */
	private String title;

	/**
	 * Initialize this with an absolute path.
	 *
	 * @param absolute absolute path
	 * @see java.io.File#File(String) original method
	 */
	public File(String absolute) {
		super(absolute);
	}

	/**
	 * Copy from other file.
	 *
	 * @param file to copy from
	 */
	public File(java.io.File file) {
		super(file.toString());
	}

	/**
	 * Initialize this using parent's file object.
	 *
	 * @param parent file of the targeted file
	 * @param name   of the targeted file
	 * @see java.io.File#File(java.io.File, String) the original method
	 */
	public File(java.io.File parent, String name) {
		super(parent, name);
	}

	/**
	 * Initialize this using parent's path.
	 *
	 * @param absolute path of the targeted file's parent
	 * @param name     of the targeted file
	 * @see java.io.File#File(String, String) the original method
	 */
	public File(String absolute, String name) {
		super(absolute, name);
	}

	/**
	 * Initialize this using an {@link URI}.
	 *
	 * @param uri of the targeted file
	 * @see java.io.File#File(URI) original method
	 */
	public File(URI uri) {
		super(uri);
	}

	/**
	 * Delete this file. This method deletes folders with it's children too.
	 *
	 * @return success of deleting
	 */
	@Override
	public boolean delete() {
		if (!this.exists()) //this is what we need :)
			return true;

		//case this is a folder then we have to clear all children before deleting
		//noinspection ResultOfMethodCallIgnored
		this.children().forEach(File::delete);
		return super.delete();
	}

	@Override
	public String getParent() {
		String parent = super.getParent();
		return parent == null ? "" : parent;
	}

	@Override
	public File getParentFile() {
		return new File(this.getParent());
	}

	/**
	 * Make this file as a directory.
	 *
	 * <ul>
	 * <li>
	 * note: if this file's parent is not a directory.
	 * This method will do nothing about that.
	 * Use {@link #mkdirs()} if you want a solution for that.
	 * </li>
	 * </ul>
	 *
	 * @return whether this is a directory now or not
	 */
	@Override
	public boolean mkdir() {
		return this.isDirectory() || super.mkdir();
	}

	/**
	 * Make this file and it's parent file as a directory.
	 *
	 * @return whether this is a directory now or not
	 */
	@Override
	public boolean mkdirs() {
		return this.isDirectory() || super.mkdirs();
	}

	/**
	 * Get a child of this with the given name.
	 *
	 * @param name name of child to get
	 * @return a child of this file with specified name
	 */
	public File child(String name) {
		return new File(this, name);
	}

	/**
	 * Get this file's children in a {@link List}.
	 *
	 * @return this file's children
	 */
	public List<File> children() {
		List<File> children = new ArrayList<>();

		String[] list = this.list();

		if (list != null)
			for (String child : list)
				children.add(new File(this, child));

		return children;
	}

	/**
	 * Get this file's title without the suffix.
	 * <br><br><b>example</b>
	 * <pre>
	 * title :      "title (1).extension"
	 * cleanTitle : "title.extension"
	 * </pre>
	 *
	 * @return this file's title without the suffix
	 * @see #clean_title cache
	 */
	public String cleanTitle() {
		if (this.clean_title != null) //if it's already defined or not
			return this.clean_title;

		String title = this.title();
		String[] split = title.split(" ");
		String number = split[split.length - 1];

		if (number.charAt(0) == '(' && number.charAt(number.length() - 1) == ')') {
			number = Strings.crop(split[split.length - 1], 1, 1);

			if (JSON.instance.is_integer(number)) {
				this.suffix = Integer.valueOf(number) + 1;
				split = Arrays.crop(split, 0, 1);
				return this.clean_title = Strings.join(" ", "", split);
			}
		}

		return this.clean_title = title;
	}

	/**
	 * Copy all this file's content to other folder.
	 *
	 * @param parent folder to copy in
	 * @return success of operation
	 */
	public boolean copy(java.io.File parent) {
		if (!parent.mkdirs())
			return false;

		File output = new File(parent, this.getName());

		if (this.isDirectory()) {
			if (!output.mkdir()) //making the output folder
				return false; //can't make folder

			boolean w = true; //result handler

			for (File child : this.children()) //coping each child
				w &= child.copy(output);

			return w;
		} else {
			try (FileInputStream fis = new FileInputStream(this);
				 FileOutputStream fos = new FileOutputStream(output)) {
				byte[] buffer = new byte[1024]; // transaction buffer
				int point; //holder point

				while ((point = fis.read(buffer)) > 0)
					fos.write(buffer, 0, point);

				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Copy all this file's content to other folder.
	 *
	 * <ul>
	 * <li>note: better to invoke in a secondary {@link Thread}.</li>
	 * </ul>
	 *
	 * @param parent       folder to copy in
	 * @param synchronizer to control the operation and get results fro
	 */
	@SuppressWarnings("unchecked")
	public void copy(java.io.File parent, lsafer.util.Synchronizer synchronizer) {
		synchronizer.put("input_folder", this.getParentFile());
		synchronizer.put("output_folder", parent);
		synchronizer.computeIfAbsent("max_progress", k -> this.filesCount());
		synchronizer.bind();

		if (!parent.mkdirs()) {
			synchronizer.put("results", false);
			synchronizer.doIfPresent(List.class, "error", (Consumer<List>) l ->
					l.add(new RuntimeException("can't make directory in ( " + parent + " )")));
			synchronizer.bind();
			return; //can't copy because no path
		}

		File output = new File(parent, this.getName());

		if (this.isDirectory()) {
			if (!output.mkdir()) { //making the output folder
				synchronizer.put("results", false);
				synchronizer.doIfPresent(List.class, "error", (Consumer<List>) l ->
						l.add(new RuntimeException("can't make directory in ( " + output + " )")));
				synchronizer.bind();
				return; //can't make folder
			}

			//coping for each child
			synchronizer.loop(new Loop.Foreach<>(this.children(), (child) -> {
				child.copy(output, synchronizer); //copping each child
				return true; //to receive next child
			}));
		} else {
			synchronizer.put("input_file", this);
			synchronizer.put("output_file", output);
			synchronizer.bind();

			try (FileInputStream fis = new FileInputStream(this);
				 FileOutputStream fos = new FileOutputStream(output)) {
				byte[] buffer = new byte[1024]; // transaction buffer

				//coping file
				synchronizer.loop(new Loop.Forever((i) -> {
					try {
						int point = fis.read(buffer); //reading next package find original mFile
						if (point < 1) return false; //break the loop case empty
						fos.write(buffer, 0, point); //write at the destiny
						return true; //continue looping there is more to copy
					} catch (IOException e) {
						e.printStackTrace();
						synchronizer.put("results", false); //break the loop there is a problem
						synchronizer.doIfPresent(List.class, "error", (Consumer<List>) l -> l.add(e));
						synchronizer.bind();
						return false;
					}
				}));
			} catch (Exception e) {
				e.printStackTrace();
				synchronizer.doIfPresent(List.class, "error", (Consumer<List>) l -> l.add(e));
				synchronizer.put("results", false);
				synchronizer.bind();
			} finally {
				synchronizer.compute("progress", (k, v) -> v instanceof Integer ? ((int) v) + 1 : 1);
				synchronizer.bind();
			}
		}
	}

	/**
	 * Delete all this file's content this method deletes folders with it's children too.
	 *
	 * <ul>
	 * <li>note: better to invoke in a secondary {@link Thread}.</li>
	 * </ul>
	 *
	 * @param synchronizer to control the operation and get results from
	 */
	@SuppressWarnings("unchecked")
	public void delete(lsafer.util.Synchronizer synchronizer) {
		synchronizer.put("output_folder", this.getParentFile());
		synchronizer.computeIfAbsent("max_progress", k -> this.filesCount());
		synchronizer.bind();

		if (this.isDirectory()) {
			//deleting children
			synchronizer.loop(new Loop.Foreach<>(this.children(), (child) -> {
				child.delete(synchronizer);
				return true;
			}));
		} else {
			boolean w = !this.exists() || super.delete();

			synchronizer.put("output_file", this);
			synchronizer.compute("results", (k, v) -> v instanceof Boolean ? ((boolean) v) && w : w);
			synchronizer.compute("progress", (k, v) -> v instanceof Integer ? ((int) v) + 1 : 1);
			synchronizer.bind();
		}
	}

	/**
	 * Get this file's type extension as written on it's name
	 * <br><br><b>example:</b>
	 * <pre>
	 * name :       "title.extension"
	 * extension :  "extension"
	 * </pre>
	 *
	 * @return extension of this file
	 * @see #extension cache
	 */
	public String extension() {
		if (this.extension != null) //if it's already defined or not
			return this.extension;

		String[] name = this.getName().split("[.]");
		return this.extension = name.length == 1 || name.length == 2 && name[0].equals("") ? "" : name[name.length - 1];
	}

	/**
	 * Get this file's parent and grand and grand grand and so on. Sorted from main-root to this.
	 *
	 * @return this file and it's parents
	 */
	public List<File> fileStack() {
		//getting ready
		List<File> stack = new ArrayList<>(); //stack

		for (File file = this; !file.getName().equals(""); file = file.getParentFile())
			stack.add(file); //sortInfo parent

		return stack;
	}

	/**
	 * Get the total count of files inside this (without folders).
	 *
	 * @return count of files
	 */
	public int filesCount() {
		if (this.isDirectory()) {
			int count = 0;
			for (File child : this.children())
				count += child.filesCount();
			return count;
		} else {
			return this.exists() ? 1 : 0;
		}
	}

	/**
	 * Search for files that contains one of the given queries on it's name.
	 *
	 * @param queries the queries of the wanted files
	 * @return files that have specific queries
	 */
	public List<File> find(String... queries) {
		List<File> result = new ArrayList<>();

		if (Strings.any(this.getName(), queries))
			result.add(this);

		for (File child : this.children())
			result.addAll(child.find(queries));

		return result;
	}

	/**
	 * Get the total count of folders inside this.
	 *
	 * @return count of folders
	 */
	public int foldersCount() {
		if (this.isDirectory()) {
			int count = 1;
			for (File child : this.children())
				count += child.foldersCount();
			return count;
		} else {
			return 0;
		}
	}

	/**
	 * Get whether this file is hidden by a dot at the first of it's name or not.
	 *
	 * @return whether this file is hidden by a dot or not
	 * @see #dot_hidden cache
	 */
	public boolean getDot_hidden() {
		if (this.dot_hidden != null) //if it's already defined
			return this.dot_hidden;

		return this.dot_hidden = this.getName().split("[.]")[0].equals("");
	}

	/**
	 * whether this file is a directory and it has a file named ".nomedia"
	 *
	 * @return whether this file contains media or not
	 */
	public boolean isNoMedia() {
		return this.isDirectory() && this.child(".nomedia").exists();
	}

	/**
	 * Get a file with a name of this and also hadn't used by any of this file's siblings.
	 * <br><br><b>example:</b>
	 * <pre>
	 * used title :     title.extension
	 * unused title :   title (1).extension
	 * </pre>
	 *
	 * @return a file of this with a name that hadn't used by any of this file's siblings
	 */
	public File junior() {
		if (!this.exists()) //if not exists then no need to rename with a copy extension
			return this;

		//trying loop
		for (; ; this.suffix++) {
			File junior = new File(
					this.getParent() + "/" + this.cleanTitle() + " (" + this.suffix + ")" + this.extension());
			//check if need to get a copy with different copy number
			if (!junior.exists())
				return junior;
		}
	}

	/**
	 * Get stiled last modifying date of the file.
	 *
	 * @return last modifying date of this file
	 */
	public String lastModifiedString() {
		return DateFormat.getInstance().format(new Date(this.lastModified()));
	}

	/**
	 * Get the mime of this file by it's name.
	 *
	 * @return this file's mime
	 * @see #mime cache
	 */
	public String mime() {
		if (this.mime != null) //if it's already defined
			return this.mime;

		//getting ready
		String mime = URLConnection.guessContentTypeFromName(this.getName()); //result

		return this.mime = mime == null ? "*/" + this.extension() : mime;
	}

	/**
	 * Make this as an empty file.
	 *
	 * @return success of making
	 */
	public boolean mk() {
		if (!this.isDirectory() && this.getParentFile().mkdirs())
			if (this.exists())
				return true;
			else try (FileWriter fr = new FileWriter(this)) {
				fr.write("");

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}

		return false;
	}

	/**
	 * Make this as a directory forcefully.
	 *
	 * @return success of the operation
	 */
	public boolean mkdirf() {
		return this.isDirectory() || (this.delete() && this.mkdir());
	}

	/**
	 * Make this and it's parents as a directory forcefully.
	 *
	 * @return success of the operation
	 */
	public boolean mkdirsf() {
		return this.isDirectory() || (this.getParentFile().mkdirsf() && this.mkdirf());
	}

	/**
	 * Force make this as a file. And if it's a directory. Then delete it then make it as a file.
	 * And if one of it's parents is a file. Then delete it then make it as a folder.
	 *
	 * @return success of making
	 */
	public boolean mkf() {
		return this.getParentFile().mkdirsf() && (!this.isDirectory() || this.delete()) && this.mk();
	}

	/**
	 * Copy this file to other folder.
	 *
	 * @param parent new parent
	 * @return result of moving
	 */
	public boolean move(java.io.File parent) {
		File parent_file = parent instanceof File ? (File) parent : new File(parent);
		File file = parent_file.child(this.getName());
		boolean w = parent_file.mkdirs() && this.renameTo(file);
		if (w) this.self = file;
		return w;
	}

	/**
	 * Read this file's Content as a {@link String}.
	 *
	 * @param defaultValue returned value case error reading file
	 * @return value of this file
	 */
	public String read(String defaultValue) {
		StringBuilder value = new StringBuilder();
		try (FileReader fr = new FileReader(this)) {
			while (true) { //until the end of the mFile
				int i = fr.read(); //getTitles next char
				if (i == -1) break; //if reached the end
				else value.append((char) i); //append next char to result holder
			}
		} catch (IOException e) {
			e.printStackTrace();
			return defaultValue; //case errors
		}

		return value.toString();//value
	}

	/**
	 * Read this file's Content as a {@link String}. Then parse it using the specified parser.
	 *
	 * @param parser       to use to parse the read text
	 * @param klass        to make sure the value is instance of
	 * @param defaultValue returned value case error reading file
	 * @param <T>          the assumed type of the written text after parsing
	 * @return parsed value of this file
	 */
	public <T> T read(Class<? extends StringParser> parser, Class<T> klass, Supplier<T> defaultValue) {
		Object object = StringParser.Parse(parser, this.read(""));
		return klass.isInstance(object) ? (T) object : defaultValue.get();
	}

	/**
	 * Read this file's java serial text. And transform it to the targeted class.
	 *
	 * @param klass        klass of needed object (just to make sure the object we read is instance of the targeted class)
	 * @param defaultValue returned value case errors
	 * @param <S>          content type
	 * @return transformed Java Serial write in this file
	 * @see Serializable
	 */
	public <S extends Serializable> S readSerializable(Class<S> klass, Supplier<S> defaultValue) {
		try (FileInputStream fis = new FileInputStream(this);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {

			S value = (S) ois.readObject();//reading //wrong cast well cached

			//result
			if (klass.isInstance(value))
				return value;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return defaultValue.get();
	}

	/**
	 * Rename this file to a new name.
	 *
	 * @param name new name
	 * @return success of operation
	 */
	public boolean rename(String name) {
		File file = new File(this.getParent(), name);
		boolean w = this.renameTo(file);
		if (w) this.self = file;
		return w;
	}

	/**
	 * Get the class of the serializable object. That have written in this file.
	 *
	 * @return the type of the object written on this
	 */
	public Class<? extends Serializable> serializableType() {
		Serializable object = this.readSerializable(Serializable.class, null);
		return object == null ? Serializable.class : object.getClass();
	}

	/**
	 * Get a sibling file of this with the same passed name.
	 *
	 * @param name of the sibling file
	 * @return a sibling of this with the same name of the given name
	 */
	public File sibling(String name) {
		return new File(this.getParent(), name);
	}

	/**
	 * Get the size of this file.
	 *
	 * @return this file's total size
	 */
	public long size() {
		if (this.isDirectory()) {
			long size = 0;

			for (File child : this.children())
				size += child.size();

			return size;
		} else {
			return this.exists() ? this.length() : 0L;
		}
	}

	/**
	 * Get file status.
	 * <br><br><b>example:</b>
	 * <pre>
	 * is directory ?   -> 'd--'
	 * can be redden ?  -> '-r-'
	 * can be edited ?  -> '--w'
	 * </pre>
	 *
	 * @return status of this file
	 */
	public String status() {
		//getting ready
		StringBuilder r = new StringBuilder(); //result

		//is directory ?
		try {
			r.append(this.canExecute() ? "d" : "-");
		} catch (Exception ignored) {
			r.append("?");
		}

		//can read ?
		try {
			r.append(this.canRead() ? "r" : "-");
		} catch (Exception ignored) {
			r.append("?");
		}

		//can write ?
		try {
			r.append(this.canWrite() ? "w" : "-");
		} catch (Exception ignored) {
			r.append("?");
		}

		return r.toString();
	}

	/**
	 * Get this file's name without the type extension.
	 * <br><br><b>example</b>
	 * <pre>
	 * name :   "title.extension"
	 * title :  "title"
	 * </pre>
	 *
	 * @return this file's title
	 * @see #title cache
	 */
	public String title() {
		if (this.title != null)//isCommand if it already defined or not
			return this.title;

		String[] split = this.getName().split("[.]");
		return this.title = split.length == 1 || split.length == 2 && split[0].equals("") ?
							split[split.length - 1] : Strings.join(".", "", Arrays.crop(split, 0, 1));
	}

	/**
	 * Write a text inside this file.
	 *
	 * @param value to write
	 * @return success of writing
	 */
	public boolean write(String value) {
		if (!this.isDirectory() && this.getParentFile().mkdir())
			try (FileWriter fw = new FileWriter(this)) {
				fw.write(value);

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}

		return false;
	}

	/**
	 * Write a text inside this file. A text from stringing the passed object using the specified {@link StringParser}.
	 *
	 * @param parser to stringify the value
	 * @param value  to be write
	 * @return success of writing
	 */
	public boolean write(Class<? extends StringParser> parser, Object value) {
		return this.write(StringParser.Stringify(parser, value));
	}

	/**
	 * Write a java serial text of the given {@link Serializable} in this file.
	 *
	 * @param value to write
	 * @return success of writing
	 */
	public boolean writeSerializable(Serializable value) {
		if (!this.isDirectory() && this.getParentFile().mkdir())
			try (FileOutputStream fos = new FileOutputStream(this);
				 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(value);

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		return false;
	}

	/**
	 * Synchronizer version for files.
	 */
	public static class Synchronizer<K, V> extends lsafer.util.Synchronizer<K, V> {
		/**
		 * Errors that have occurred during the process.
		 */
		public ArrayList<Exception> error = new ArrayList<>();

		/**
		 * The source file that the process is now pointing at.
		 */
		public File input_file = new File("");

		/**
		 * The source folder that the process is now pointing at.
		 */
		public File input_folder = new File("");

		/**
		 * Maximum progress.
		 */
		public Integer max_progress = null;

		/**
		 * The output file that the process is processing now.
		 */
		public File output_file = new File("");

		/**
		 * The output folder that the process is processing now.
		 */
		public File output_folder = new File("");

		/**
		 * Now progressed files count.
		 */
		public Integer progress = 0;

		/**
		 * Results.
		 */
		public Boolean results = true;
	}
}
