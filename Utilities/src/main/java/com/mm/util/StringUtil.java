package com.mm.util;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: miguelmunoz
 * Date: Aug 11, 2006
 * Time: 11:16:56 PM
 */
@SuppressWarnings({"HardcodedFileSeparator", "MagicCharacter", "MagicNumber", "CallToStringEquals", "CallToStringEqualsIgnoreCase", "CallToNumericToString", "HardcodedLineSeparator", "HardCodedStringLiteral", "StringConcatenation"})
public class StringUtil {
	private StringUtil() { }

	public static final int[] EMPTY_INT_ARRAY = new int[0];

	public static final String UNICODE_TRADEMARK = "\u2122";
	public static final String UTF8 = "UTF-8";

//	public static String format(String pattern, Object... values) {
//		StringBuilder bldr = new StringBuilder();
//		Formatter fmt = new Formatter(bldr);
//		fmt.format(pattern, values);
//		return bldr.toString();
//	}
//	
	public static byte[] stringToUTF8(String input) {
		byte[] output=null;
		try {
			output = input.getBytes(UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//noinspection ConstantConditions
		return output;
	}

	public static String UTF8ToString(byte[] utf8Array) {
		try {
			return new String(utf8Array, 0, utf8Array.length, UTF8);
		} catch (UnsupportedEncodingException e) {
			//noinspection ConstantConditions
			return null;
		}
	}

	public static String escapeQuotesInString(String s) {
		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if(c == '"') {
				buf.append("\\\"");
			} else if(c == '\\') {
				buf.append("\\\\");
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public static String unescapeQuotesInString(String s) {
		StringBuilder buf = new StringBuilder();
		boolean escape = false;
		for(int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if(c == '\\') {
				if(!escape) {
					escape = true;
				} else {
					buf.append(c);
					escape = false;
				}
			} else {
				buf.append(c);
				escape = false;
			}
		}
		return buf.toString();
	}

	private static final String basicChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.";
	private static final String basicCharsAndSpace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_. ";

	/**
	 * Deletes all characters that aren't alphabetic or numeric. Allows the
	 * period, the underscore, and optionally the space.
	 * todo:   This code could be made more efficient by using
	 * todo:   the Character.isLetterOrDigit() method and other methods in
	 * todo:   the Character class.
	 * @param s             The string to process
	 * @param allowSpace    Specifes whether to keep spaces
	 * @return The string with all non-basic characters removed.
	 */
	public static String deleteAllNonBasicChars(String s, boolean allowSpace) {
		String allowedChars = allowSpace ? basicCharsAndSpace : basicChars;
		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if(allowedChars.indexOf(c) != -1) {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public static String deleteAllNonBasicChars(String s) {
		return deleteAllNonBasicChars(s, false);
	}

	public static String deleteAllNonNumericChars(String s) {
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if(Character.isDigit(c)) {
				b.append(c);
			}
		}

		return b.toString();
	}

	public static String translateSpacesToUnderscores(String s) {
		return s.replace(' ', '_');
	}

//	public static String replaceBindings(String inputString, String[] bindings)
//		throws gnu.regexp.REException
//	{
//		int pos = 0;
//		String outputString = inputString;
//		while(pos < bindings.length) {
//			String search = "\\$" + bindings[pos++];
//			String replace = bindings[pos++];
//			RE re = new RE(search);
//			outputString = re.substituteAll(outputString, replace, 0, RE.REG_NO_INTERPOLATE);
//		}
//		return outputString;
//	}

	public static String join(Object[] strings, String separator) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < strings.length; ++i) {
			b.append(strings[i].toString());
			if (i != strings.length - 1) {
				b.append(separator);
			}
		}
		return b.toString();
	}

	public static String join2(Object[] strings, String separator) {
		StringBuilder b = new StringBuilder();
		if (strings.length > 0) {
			b.append(strings[0].toString());
			for (int i = 1; i < strings.length; ++i) {
				b.append(separator).append(strings[i].toString());
			}
		}
		return b.toString();
	}

	public static String join3(Object[] strings, String separator) {
		if (strings.length == 0) {
			return "";
		}
		StringBuilder b = new StringBuilder();
		b.append(strings[0].toString());
		for (int i = 1; i < strings.length; ++i) {
			b.append(separator).append(strings[i].toString());
		}
		return b.toString();
	}

	public static String join(Collection strings, String separator) {
		StringBuilder b = new StringBuilder();
		Iterator i = strings.iterator();
		if(!i.hasNext()) return "";
		while(true) {
			b.append(i.next().toString());
			if(!i.hasNext()) break;
			b.append(separator);
		}
		return b.toString();
	}

	public static int[] intArrayFromString(String values) {
		int[] a;
		if(values.length() > 0) {
			String[] s = values.split(" ");
			a = new int[s.length];
			for(int i = 0; i < a.length; i++) {
				a[i] = Integer.valueOf(s[i]);
			}
		} else {
			a = EMPTY_INT_ARRAY;
		}
		return a;
	}

	public static String hexStringFromBytes(byte[] bytes) {
		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < bytes.length; ++i)
		{
			int b = ((int)bytes[i] & 0xFF);
			String s = Integer.toString(b, 16);
			if(s.length() == 1) {
				//noinspection MagicCharacter
				buf.append('0');
			}
			buf.append(s);
		}
		return buf.toString();
	}

	public static byte[] bytesFromHexString(String string) {
		int length = string.length() / 2;
		byte[] bytes = new byte[length];
//		char[] chars = string.toCharArray();
		int c = 0;
		for(int i = 0; i < length; ++i) {
			String s = string.substring(c, c + 2);
			c += 2;
			bytes[i] = (byte)Integer.parseInt(s, 16);
		}

		return bytes;
	}

	public static boolean same(String s1, String s2) {
		if(s1 == null && s2 == null) return true;
		//noinspection SimplifiableIfStatement
		if((s1 == null) != (s2 == null)) return false;
		return s1.equals(s2);
	}

	public static boolean sameIgnoreCase(String s1, String s2) {
		if(s1 == null && s2 == null) return true;
		//noinspection SimplifiableIfStatement
		if((s1 == null) != (s2 == null)) return false;
		return s1.equalsIgnoreCase(s2);
	}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	private static HashMap<Character, String> charsMap = new HashMap<Character, String>();
	@SuppressWarnings({"StringContatenationInLoop"})
	public static String chars(char c, int n) {
		if(n < 1) return "";
		//noinspection UnnecessaryLocalVariable
		Character cc = c;
		String s = charsMap.get(cc);
		if(s == null || s.length() < n) {
			if(s == null) {
				s = cc.toString();
			}
			// todo: convert this to a StringBuilder
			while(s.length() < n) {
				s += c;
			}
			charsMap.put(cc, s);
		}
		return s.substring(0, n);
	}

	public static String spaces(int n) {
		//noinspection MagicCharacter
		return chars(' ', n);
	}
	
	/**
	 * This converts a number into a unique base 26 string. The strings
	 * increase from A to Z then from AA through AZ and BA to ZZ, then from
	 * AAA through AAZ and ABA, and so on. Note that, while the string is
	 * essentially a 26-digit number, it's not equal to the input number.
	 * Instead, this cycles through all the 1-digit base 26 numbers, starting
	 * with zero, then goes through all the 2-digit base 26 numbers, again
	 * starting with zero, and so on. (Since A is zero, A = AA = AAA).
	 * The while loop in the code will cycle through once for each digit 
	 * except the first. So if the number produces a 3-digit string, the while 
	 * loop will run through 2 iterations.
	 */
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
 
	public static String base26(int number)
	{
	  // positive numbers only, please.
	  int radix =alphabet.length();
	  int digitCount = 1;
	  int base = radix;
	  while (number >= base) {
	    number -= base;
	    base *= radix;
	    digitCount++;
	  }
	  return makeDigits(number, radix, digitCount);
	}
   
	private static String makeDigits(int number, int radix, int digitCount) {
	  int num = number;
	  StringBuilder out = new StringBuilder();
	  for (int ii=0; ii<digitCount; ++ii) {
	    int index = num%radix;
	    out.insert(0, alphabet.charAt(index));
	    num /= radix;
	  }
	  return out.toString();
	}

	public static Object[] digitsFromUnsignedInt(int integer, int radix) {
		if(integer < 0) {
			throw new IllegalArgumentException("integer must be >= 0");
		}

		if(radix < 2 || radix > 36) {
			throw new IllegalArgumentException("radix must be in the range 2..36");
		}

		LinkedList<Integer> digits = new LinkedList<Integer>();

		int place = 1;
		do {
			int nextPlace = place * radix;
			int placeValue = integer % nextPlace;
			int digit = placeValue / place;
			digits.addFirst(digit);
			integer -= placeValue;
			place = nextPlace;
		} while(integer > 0);

		return digits.toArray();
	}
	
	public static void main(String[] args) {
		for (int ii=0; ii<500; ++ii) {
			System.out.println(String.format("%3d:  %s", ii, base26(ii)));
		}
	}

	public static String wrapWithTag(String text, String tag) {
		StringBuilder buf = new StringBuilder(text.length() + 2*(tag.length()+2) + 1);
		buf.append('<');
		buf.append(tag);
		buf.append('>');
		buf.append(text);
		buf.append("</");
		buf.append(tag);
		buf.append('>');
		return buf.toString();
	}

	public static String wrapWithTag(String text, String tag, String parameters) {
		StringBuilder buf = new StringBuilder(text);
		buf.insert(0, '>');
		buf.insert(0, parameters);
		buf.insert(0, ' ');
		buf.insert(0, tag);
		buf.insert(0, '<');
		buf.append('<');
		buf.append(tag);
		buf.append('>');
		return buf.toString();
	}

	public static String wrapWithHTML(String text) { return wrapWithTag(text, "HTML"); } // NON-NLS
	public static String wrapWithHTML(String text, int width) {
		//noinspection StringConcatenation
		return wrapWithHTML("<p width=" + width + "> " + text); // NON-NLS
	}

	/**
	 * Convert an unsigned int to a String. By letting you specify a string of
	 * digits, this lets you convert integers 1, 2, 3, ... 26, 27, 28 ... into
	 * column names A, B, C, ... Z, AA, AB, ... To do this, call one of the
	 * other methods, which delegates to this method.
	 * todo:   Portions might duplicate functionality in Integer.class. Look at
	 * todo:   the Integer.toString(int i, int radix) method, which works on a
	 * todo:   signed int. (Not sure about this.)
	 * @param integer x
	 * @param radix x
	 * @param digitChars x
	 * @param offset x
	 * @return  The string representation of the integer.
	 */
	public static String stringFromUnsignedInt(int integer, int radix, String digitChars, boolean offset) {
		Object[] digits = digitsFromUnsignedInt(integer, radix);

		StringBuilder buf = new StringBuilder();

		int digitsCount = digits.length;
		for(int i = 0; i < digitsCount; ++i) {
			int digit = (Integer)digits[i];
			if(offset && i != digitsCount - 1) --digit;
			buf.append(digitChars.charAt(digit));
		}

		return buf.toString();
	}

	public static String stringFromUnsignedInt(int integer, int radix) {
		return stringFromUnsignedInt(integer, radix, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", false); // NON-NLS
	}

	public static String letterCodeFromUnsignedInt(int integer) {
		return stringFromUnsignedInt(integer, 26, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", true); // NON-NLS
	}

	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;
	public static class Column {
		String name;
		private int length, align;
		private Object object;

		public Column(String name, int length, int align) {
			this.name = name;
			this.length = length;
			this.align = align;
			this.object = null;
		}

		private static final int TYPE_OBJECT = 0;
		private static final int TYPE_NAME = 1;
		private static final int TYPE_SEPARATOR = 2;
		public String format(int type, boolean last) {
			switch(type) {
			case TYPE_OBJECT:
				return format(length, align, last, object);
			case TYPE_NAME:
				return format(length, align, last, name);
			case TYPE_SEPARATOR:
				return format(length, align, last, chars('-', length));
			default:
				throw new IllegalStateException("Unknown format type.");
			}
		}

		public void set(Object object) {
			this.object = object;
		}

		public static String format(int length, int align, boolean last, Object o) {
			// todo: convert this to a StringBuilder
			String s;
			if(o == null) {
				s = "";
			} else {
				s = o.toString();
			}
			int slength = s.length();
			if(slength > length) {
				return s.substring(0, length);
			} else {
				int x = length - slength;
				switch(align) {
				case ALIGN_LEFT:
					if(!last) {
						s += spaces(x);
					}
					return s;
				case ALIGN_CENTER:
					int y = x / 2;
					s = spaces(y) + s;
					if(!last) {
						s += spaces(length - slength - y);
					}
					return s;
				case ALIGN_RIGHT:
					return spaces(x) + s;
				default:
					throw new IllegalStateException("Unknown alignment");
				}
			}
		}

		private static String format(Column[] columns, int type) {
			StringBuilder b = new StringBuilder();
			int count = columns.length;
			for(int i = 0; i < count; ++i) {
				b.append(columns[i].format(type, i == count - 1));
			}
			b.append('\n');
			return b.toString();
		}

		public static String format(Column[] columns) {
			return format(columns, TYPE_OBJECT);
		}

		public static String names(Column[] columns) {
			return format(columns, TYPE_NAME);
		}

		public static String separator(Column[] columns) {
			return format(columns, TYPE_SEPARATOR);
		}
	}

	public static Iterable<Character> stringIterable(final String source) {
		return new Iterable<Character>() {
			public Iterator<Character> iterator() {
				//noinspection IteratorNextCanNotThrowNoSuchElementException
				return new Iterator<Character>() {
					private int index=0;
					public boolean hasNext() { return index < source.length(); }
					public Character next() { return source.charAt(index++); }
					public void remove() { throw new AssertionError("remove() is not supported"); }
				};
			}
		};
	}
}
