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
package lsafer.test;

/**
 * Java test.
 *
 * @author LSaferSE
 * @version 1
 * @since 27-Oct-2019
 */
@SuppressWarnings({"ALL"})
public class JavaTest {
	/**
	 * Java static field test.
	 */
	public static JavaTest test0 = new JavaTest();
	/**
	 * Java member field test.
	 */
	public JavaTest test1 = test0;

	/**
	 * Invoke groovy from java test.
	 *
	 * @param flavor to be returned as a results
	 * @return test results
	 */
	public static String groovyTest(String flavor) {
		return GroovyTest.test0("groovyFromJava " + flavor);
	}

	public static void main(String[] args) {
		System.out.println(groovyTest("java-main flavored"));
	}

	/**
	 * Java static method test.
	 *
	 * @param flavor to be returned as a results
	 * @return test results
	 */
	public static String test0(String flavor) {
		return "Java test result is: " + flavor;
	}

	/**
	 * Java static method test.
	 *
	 * @param flavor to be returned as a results
	 * @return test results
	 */
	public String test1(String flavor) {
		return test0(flavor);
	}
}
