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
package lsafer.test
/**
 * Groovy test.
 *
 * @author LSaferSE* @version 1* @since 27-Oct-2019
 */
//@SuppressWarnings("ALL")
class GroovyTest {
    /**
     * Groovy static field test.
     */
    public static GroovyTest test0 = new GroovyTest()

	/**
	 * Groovy member field test.
	 */
	public GroovyTest test1 = test0

	/**
	 * Invoke java from groovy test.
	 *
	 * @param flavor to be returned as a results
	 * @return test results
	 */
	static String javaTest(String flavor) {
		JavaTest.test0("javaFromGroovy " + flavor)
	}

	static void main(String[] args) {
		System.out.println(javaTest("groovy-main flavored"))
	}

	/**
	 * Groovy static method test.
	 *
	 * @param flavor to be returned as a results
	 * @return test results
	 */
	static String test0(String flavor) {
		return "Groovy test result is: " + flavor
	}

	/**
	 * Groovy static method test.
	 *
	 * @param flavor to be returned as a results
	 * @return test results
	 */
	static String test1(String flavor) {
		return test0(flavor)
	}
}