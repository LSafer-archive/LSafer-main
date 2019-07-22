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

import lsafer.lang.INI;
import lsafer.lang.JSON;
import lsafer.threading.Loop;
import lsafer.util.Arrays;
import lsafer.util.Strings;

/**
 * a {@link java.io.File file object} with useful tools.
 *
 * @author LSaferSE
 * @version 6
 * @since 18 May 2019
 */
@SuppressWarnings({"WeakerAccess"})
public class File extends java.io.File {

    /**
     * the real this
     * because some operations
     * needs to replace the hole object
     * to effect results
     * such as renaming or moving the file.
     *
     * hello2
     */
    public File self = this;

    /**
     * this file's title without copy number.
     * <p>
     * title :      "title (1)"
     * cleanTitle : "title"
     */
    private String cleanTitle;

    /**
     * if this file's name starts with dot.
     * <p>
     * normal :     "file"
     * dotHidden :  ".file"
     */
    private Boolean dotHidden;

    /**
     * this file's type extension.
     * <p>
     * name :       "title.extension"
     * extension :  "extension"
     */
    private String extension;

    /**
     * this file's copy number case there is other files with the same name.
     */
    private int jrsuffix = 1;

    /**
     * General mime Of this file.
     * <p>
     * example :    "image/png"
     */
    private String mime;

    /**
     * this file's name without type extension.
     * <p>
     * name :   "title.extension"
     * title :  "title"
     */
    private String title;

    /**
     * init this with absolute path.
     *
     * @param pathname absolute path
     * @see java.io.File#File(String) original method
     */
    public File(String pathname) {
        super(pathname);
    }

    /**
     * copy from other file.
     *
     * @param file to Copy from
     */
    public File(java.io.File file) {
        super(file.toString());
    }

    /**
     * init this using parent's file object.
     *
     * @param parent of targeted file
     * @param name   of targeted file
     * @see java.io.File#File(java.io.File, String) the original method
     */
    public File(java.io.File parent, String name) {
        super(parent, name);
    }

    /**
     * init this using parent's path.
     *
     * @param path of targeted file's parent
     * @param name of targeted file
     * @see java.io.File#File(String, String) the original method
     */
    public File(String path, String name) {
        super(path, name);
    }

    /**
     * init this using {@link URI}.
     *
     * @param uri of targeted file
     * @see java.io.File#File(URI) original method
     */
    public File(URI uri) {
        super(uri);
    }

    @Override
    public String getParent() {
        String parent = super.getParent();
        return parent == null ? "" : parent;
    }

    /**
     * get this file's parent file.
     *
     * @return Parent file
     */
    @Override
    public File getParentFile() {
        return new File(this.getParent());
    }

    /**
     * delete this file
     * this method deletes folders with it's children too.
     *
     * @return success of deleting
     */
    @Override
    public boolean delete() {
        if (!this.exists()) //this is what we need :)
            return true;

        boolean w = true;

        //case this is a folder then we have to clear all children before deleting
        if (this.isDirectory())
            for (File child : this.children())
                w &= child.delete();

        w &= super.delete();

        return w;
    }

    @Override
    public boolean mkdir() {
        return this.isDirectory() || super.mkdir();
    }

    @Override
    public boolean mkdirs() {
        return this.isDirectory() || super.mkdirs();
    }

    /**
     * get a child of this with the given name.
     *
     * @param name name of child to get
     * @return a child of this file with specified name
     */
    public File child(String name) {
        return new File(this, name);
    }

    /**
     * get this file's children in a {@link List list}.
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
     * get this file's title without copy mark
     * <p>
     * title :      "title (1).extension"
     * cleanTitle : "title.extension"
     *
     * @return this file's title without copy number
     * @see #cleanTitle solved-cache
     */
    public String cleanTitle() {
        if (this.cleanTitle != null) //if it's already defined or not
            return this.cleanTitle;

        String title = this.title();
        String[] split = title.split(" ");
        String number = split[split.length - 1];

        if (number.charAt(0) == '(' && number.charAt(number.length() - 1) == ')') {
            number = Strings.crop(split[split.length - 1], 1, 1);

            if (JSON.is_integer(number)) {
                this.jrsuffix = Integer.valueOf(number) + 1;
                split = Arrays.crop(split, 0, 1);
                return this.cleanTitle = Strings.join(" ", "", split);
            }
        }

        return this.cleanTitle = title;
    }

    /**
     * copy all this file's content to other folder.
     *
     * @param parent folder to copy in
     * @return success of operation
     */
    public boolean copy(File parent) {
        if (!parent.mkdirs())
            return false;

        File output = parent.child(this.getName());

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
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * copy all this file's content to other folder.
     * <p>
     * note : better to invoke in a secondary {@link Thread}
     *
     * @param parent       folder to copy in
     * @param synchronizer to control the operation and get results fro
     */
    public void copy(File parent, lsafer.threading.Synchronizer synchronizer) {
        synchronizer.put("input_folder", this.getParentFile());
        synchronizer.put("output_folder", parent);
        if (synchronizer.get("max_progress") == null)
            synchronizer.put("max_progress", this.filesCount());
        synchronizer.bind();

        if (!parent.mkdirs()) {
            synchronizer.put("results", false);
            synchronizer.<List<Exception>>get("error").add(new IllegalStateException("can't make directory in ( " + parent + " )"));
            synchronizer.bind();
            return; //can't copy because no path
        }

        File output = parent.child(this.getName());

        if (this.isDirectory()) {
            if (!output.mkdir()) { //making the output folder
                synchronizer.put("results", false);
                synchronizer.<List<Exception>>get("error").add(new IllegalStateException("can't make directory in ( " + output + " )"));
                synchronizer.bind();
                return; //can't make folder
            }

            //coping for each child
            synchronizer.startLoop(new Loop.Foreach<>(this.children(), (child) -> {
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
                synchronizer.startLoop(new Loop.Forever((i) -> {
                    try {
                        int point = fis.read(buffer); //reading next package find original mFile
                        if (point < 1) return false; //break the loop case empty
                        fos.write(buffer, 0, point); //write at the destiny
                        return true; //continue looping there is more to copy
                    } catch (IOException e) {
                        e.printStackTrace();
                        synchronizer.put("results", false); //break the loop there is a problem
                        synchronizer.<List<Exception>>get("error").add(e);
                        synchronizer.bind();
                        return false;
                    }
                }));
            } catch (Exception e) {
                e.printStackTrace();
                synchronizer.<List<Exception>>get("error").add(e);
                synchronizer.put("results", false);
                synchronizer.bind();
            } finally {
                synchronizer.put("progress", synchronizer.<Integer>get("progress") + 1); //update track info
                synchronizer.bind();
            }
        }
    }

    /**
     * delete all this file's content
     * this method deletes folders with it's children too.
     * <p>
     * note : better to invoke in a secondary {@link Thread}
     *
     * @param synchronizer to control the operation and get results from
     */
    public void delete(lsafer.threading.Synchronizer synchronizer) {
        synchronizer.put("output_folder", this.getParentFile());
        if (synchronizer.get("max_progress") == null)
            synchronizer.put("max_progress", this.filesCount());
        synchronizer.bind();

        if (this.isDirectory()) {
            //deleting children
            synchronizer.startLoop(new Loop.Foreach<>(this.children(), (child) -> {
                child.delete(synchronizer);
                return true;
            }));
        } else {
            synchronizer.put("output_file", this);
            synchronizer.put("results", synchronizer.<Boolean>get("results") && !this.exists() || super.delete());
            synchronizer.put("progress", synchronizer.<Integer>get("progress") + 1);
            synchronizer.bind();
        }
    }

    /**
     * get this file's type extension as written on it's name
     * <p>
     * name :       "title.extension"
     * extension :  "extension"
     *
     * @return extension of this file
     * @see #extension solved-cache
     */
    public String extension() {
        if (this.extension != null) //if it's already defined or not
            return this.extension;

        String[] name = this.getName().split("[.]");
        return this.extension = name.length == 1 || name.length == 2 && name[0].equals("") ? "" : name[name.length - 1];
    }

    /**
     * get this file's parent and grand and grand grand and so on.
     * <p>
     * sorted from main-root to this
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
     * get the total count of files inside this (without folders).
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
     * search for files that contains one of the given queries on it's name.
     *
     * @param queries the queries of the wanted files
     * @return files that have specific queries
     */
    public List<File> find(String... queries) {
        List<File> result = new ArrayList<>();

        if (Strings.any(this.getName(), queries))
            result.add(this);

        if (this.isDirectory())
            for (File child : this.children())
                result.addAll(child.find(queries));

        return result;
    }

    /**
     * get the total count of folders inside this.
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
     * if this file is hidden by a dot at the first of it's name.
     *
     * @return whether this file is hidden by a dot or not
     * @see #dotHidden solved-cache
     */
    public boolean isDotHidden() {
        if (this.dotHidden != null) //if it's already defined
            return this.dotHidden;

        return this.dotHidden = this.getName().split("[.]")[0].equals("");
    }

    /**
     * if this file is a directory and it has a file named ".nomedia"
     *
     * @return whether this file contains media or not
     */
    public boolean isNoMedia() {
        return this.isDirectory() && this.child(".nomedia").exists();
    }

    /**
     * get a file with a name of this and also hadn't used by any of this file's siblings.
     * <p>
     * used title :     title.extension
     * unused title :   title (1).extension
     *
     * @return a file of this with a name that hadn't used by any of this file's siblings
     */
    public File junior() {
        if (!this.exists()) //if not exists then no need to rename with a copy extension
            return this;

        //trying loop
        for (; ; this.jrsuffix++) {
            File junior = new File(this.getParent() + "/" + this.cleanTitle() + " (" + this.jrsuffix + ")" + this.extension());
            //check if need to get a copy with different copy number
            if (!junior.exists())
                return junior;
        }
    }

    /**
     * get stiled last modifying date of the file.
     *
     * @return last modifying date of this file
     */
    public String lastModifiedString() {
        return DateFormat.getInstance().format(new Date(this.lastModified()));
    }

    /**
     * get the mime of this file by it's name.
     *
     * @return this file's mime
     * @see #mime solved-cache
     */
    public String mime() {
        if (this.mime != null) //if it's already defined
            return this.mime;

        //getting ready
        String mime = URLConnection.guessContentTypeFromName(this.getName()); //result

        return this.mime = mime == null ? "*/" + this.extension() : mime;
    }

    /**
     * make empty file at this file's path.
     *
     * @return success of making
     */
    public boolean mk() {
        if (this.exists())
            return true;

        if (!this.isDirectory() && this.getParentFile().mkdirs())
            try {
                FileWriter writer = new FileWriter(this);
                writer.write("");

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        return false;
    }

    /**
     * make this as a directory forcefully.
     *
     * @return success of the operation
     */
    public boolean mkdirf() {
        return this.isDirectory() || (this.delete() && this.mkdir());
    }

    /**
     * make this and it's parents as a directory forcefully.
     *
     * @return success of the operation
     */
    public boolean mkdirsf() {
        return this.isDirectory() || (this.getParentFile().mkdirsf() && this.mkdirf());
    }

    /**
     * force make this as a file
     * and if it's a directory
     * then delete it then make it
     * and if one of it's parents
     * is a file the delete it then
     * make it as a folder.
     *
     * @return success of making
     */
    public boolean mkf() {
        return this.getParentFile().mkdirsf() && (!this.isDirectory() || this.delete()) && this.mk();
    }

    /**
     * copy this file to other folder.
     *
     * @param parent new parent
     * @return result of moving
     */
    public boolean move(File parent) {
        File file = parent.child(this.getName());
        boolean w = parent.mkdirsf() && this.renameTo(file);
        if (w) this.self = file;
        return w;
    }

    /**
     * read this file's Content as a {@link String string}.
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
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue; //case errors
        }

        return value.toString();//value
    }

    /**
     * Read this file's INI text
     * and transform it to the targeted class.
     *
     * @param defaultValue returned value case error reading file
     * @param <TYPE>       type of target object
     * @return transformed INI object write in this file
     */
    public <TYPE> TYPE readINI(TYPE defaultValue) {
        try {
            return (TYPE) INI.parse(this.read(""));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Read this file's JSON text
     * and transform it to the targeted class.
     *
     * @param defaultValue returned value case error reading file
     * @param <TYPE>       type of target object
     * @return transformed JSON object write in this file
     */
    public <TYPE> TYPE readJSON(TYPE defaultValue) {
        try {
            return (TYPE) JSON.parse(this.read(""));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * read this file's java serial text
     * and transform it to the targeted class.
     *
     * @param defaultValue returned value case errors
     * @param <TYPE>       content type
     * @return transformed Java Serial write in this file
     */
    public <TYPE extends Serializable> TYPE readSerial(TYPE defaultValue) {
        try (FileInputStream fis = new FileInputStream(this);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            TYPE value = (TYPE) ois.readObject();//reading //wrong cast well cached

            //result
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            //case no mFile or wrong cast
            return defaultValue;
        }
    }

    /**
     * rename to new name.
     *
     * @param name new Name
     * @return success of operation
     */
    public boolean rename(String name) {
        File file = new File(this.getParent(), name);
        boolean w = this.renameTo(file);
        if (w) this.self = file;
        return w;
    }

    /**
     * search for files that matching one of given queries.
     *
     * @param query to search for
     * @return files that have specific query on it's name
     */
    public List<File> search(String... query) {
        List<File> result = new ArrayList<>(); //result holder

        for (File child : this.children()) { //for each child
            if (child.isDirectory()) //so it's contains children
                result.addAll(child.search(query)); //check grandchildren
            if (Strings.any(child.getName(), query)) //it's have one of the needed names
                result.add(child);//define it to the results
        }

        return result;
    }

    /**
     * get the class of the serializable object
     * written in this file.
     *
     * @return the type of the object written on this
     */
    public Class<? extends Serializable> serialType() {
        Object object = this.readSerial(null);
        return object == null ? Serializable.class : (Class<? extends Serializable>) object.getClass();
    }

    /**
     * get a sibling file of this with the
     * same passed name.
     *
     * @param name of the sibling file
     * @return a sibling of this with the same name of the given name
     */
    public File sibling(String name) {
        return new File(this.getParent(), name);
    }

    /**
     * get this file and it's children and their children size.
     *
     * @return this file's total size
     */
    public long size() {
        if (this.isDirectory()) {
            long size = 0;
            for (File child : this.children())
                size += child.size();
            return size;
        } else if (this.exists()) {
            try {
                return this.length();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * get file status.
     * <p>
     * is directory ? -> d--
     * can read ? -r-
     * can write ? --w
     *
     * @return status of this file
     */
    public String status() {
        //getting ready
        StringBuilder r = new StringBuilder(); //result

        //is directory ?
        try {
            r.append(this.canExecute() ? "d" : "-");
        } catch (Exception e) {
            r.append("?");
        }

        //can read ?
        try {
            r.append(this.canRead() ? "r" : "-");
        } catch (Exception e) {
            r.append("?");
        }

        //can write ?
        try {
            r.append(this.canWrite() ? "w" : "-");
        } catch (Exception e) {
            r.append("?");
        }

        return r.toString();
    }

    /**
     * get this file's name without the type extension.
     * <p>
     * name :   "title.extension"
     * title :  "title"
     *
     * @return this file's title
     */
    public String title() {
        if (this.title != null)//isCommand if it already defined or not
            return this.title;

        String[] split = this.getName().split("[.]");
        return this.title = split.length == 1 || split.length == 2 && split[0].equals("") ?
                split[split.length - 1] : Strings.join(".", "", Arrays.crop(split, 0, 1));
    }

    /**
     * write a text inside this file.
     *
     * @param value to write
     * @return success of writing
     */
    public boolean write(String value) {
        try (FileWriter fw = new FileWriter(this)) {
            fw.write(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * write a INI text of the given object
     * in this file.
     *
     * @param value to write
     * @return success of writing
     */
    public boolean writeINI(Object value) {
        return this.write(INI.stringify(value));
    }

    /**
     * write a JSON text of the given object
     * in this file.
     *
     * @param value to write
     * @return success of writing
     */
    public boolean writeJSON(Object value) {
        return this.write(JSON.stringify(value));
    }

    /**
     * write a java serial text of the given object
     * in this file.
     *
     * @param value to write
     * @return success of writing
     */
    public boolean writeSerial(Serializable value) {
        try (FileOutputStream fos = new FileOutputStream(this);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * synchronizer version for files.
     */
    public static class Synchronizer extends lsafer.threading.Synchronizer {

        /**
         * errors that have occurred during the process.
         */
        public ArrayList<Exception> error = new ArrayList<>();

        /**
         * the source file that the process is now pointing at.
         */
        public File input_file = new File("");

        /**
         * the source folder that the process is now pointing at.
         */
        public File input_folder = new File("");

        /**
         * max progress.
         */
        public Integer max_progress = null;

        /**
         * the output file that the process is processing now.
         */
        public File output_file = new File("");

        /**
         * the output folder that the process is processing now.
         */
        public File output_folder = new File("");

        /**
         * now progressed files count.
         */
        public Integer progress = 0;

        /**
         * results.
         */
        public Boolean results = true;

    }

}
