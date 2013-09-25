/*******************************************************************************
 * 2008-2013 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 *******************************************************************************/
package org.dma.java.utils.numeric;

import java.math.BigDecimal;
import java.util.Random;

import org.dma.java.utils.string.StringUtils;

public class NumericUtils {

	/*
	 * Conversion
	 */
	public static String toString(int value){

		return Integer.toString(value);

	}


	public static String toString(BigDecimal value) {

		return value==null ? "" : value.toString();

	}


	public static char chr(int value) {

		return (char)value;

	}


	public static BigDecimal value(String value) {

		try{
			return new BigDecimal(value);

		}catch(NumberFormatException e){}

		return BigDecimal.valueOf(0);

	}





	/*
	 * Creation
	 */
	public static Integer random(int length) {

		return new Integer(StringUtils.random("0123456789", length));

	}


	public static Integer random() {

		return new Random().nextInt(Integer.MAX_VALUE);

	}





	/*
	 * Analysis
	 */
	public static boolean bit(int value, int bit) {

		return (value & bit)!=0;
	}


	public static boolean isZero(String value) {

		return value(value).signum()==0;

	}


	public static boolean isGreater(BigDecimal value, BigDecimal compareTo){

		return value.compareTo(compareTo) <= 0 ? false : true;

	}


	public static boolean isLesser(BigDecimal value, BigDecimal compareTo){

		return value.compareTo(compareTo) >= 0 ? false : true;

	}


	public static BigDecimal greater(BigDecimal value, BigDecimal compareTo){

		int result = value.compareTo(compareTo);

		switch(result){
		case -1: return compareTo;
		case 1: return value;
		default: return null;
		}

	}


	public static BigDecimal lesser(BigDecimal value, BigDecimal compareTo){

		int result=value.compareTo(compareTo);

		switch(result){
		case -1: return value;
		case 1: return compareTo;
		default: return null;
		}

	}


}
