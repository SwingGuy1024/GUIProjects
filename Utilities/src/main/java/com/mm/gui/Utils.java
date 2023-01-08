package com.mm.gui;

import java.awt.GridLayout;
import java.text.Normalizer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Created by IntelliJ IDEA.
 * User: miguelmunoz
 * Date: Sep 6, 2010
 * Time: 8:35:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
	public static void main(String[] args) {
		String testString = "r\u00f4le\u030a, ro\u0302le 35\u00c5, A\u030arch \ufb01ne, first, 2\u2075 C\u0152R D\u00c6R";
		
		JFrame frame = new JFrame("Normal Test");
		JPanel mainPanel = new JPanel(new GridLayout(1, 0));
		frame.add(mainPanel);
		JTextArea field = new JTextArea(5, 40);
		JTextArea outField = new JTextArea(5, 40);
		StringBuilder bldr = new StringBuilder();
		append(testString, bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFC), bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFD), bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFKC), bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFKD), bldr);
		field.setText(bldr.toString());
		mainPanel.add(field);
		outField.setText(strippedString(field.getText()));
		mainPanel.add(outField);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private static void append(String text, StringBuilder bldr) {
		bldr.append(text);
		bldr.append(" (");
		bldr.append(text.length());
		bldr.append(")\n");
	}
	
	public static String strippedString(String rawInputString) {
//		for (int ii=0; ii<rawInputString.length(); ++ii) {
//			final char c = rawInputString.charAt(ii);
//			//noinspection HardcodedLineSeparator,HardcodedFileSeparator  
//			System.out.printf("%c  (\\u%4x): %s\n", c, ((int)c), Character.getType(c)); //NON-NLS
//		}
		String inputString = Normalizer.normalize(rawInputString, Normalizer.Form.NFKC);
		StringBuilder builder = new StringBuilder(inputString.length());
		for (int ii=0; ii<inputString.length(); ++ii) {
			final char c = inputString.charAt(ii);
			// transform the character to its decomposed form (n, followed by ~)
			final String s = Normalizer.normalize(String.valueOf(c), Normalizer.Form.NFD);
			// The stripped character will be the first character of the normalized string
			final char strippedChar = s.charAt(0);
			if (Character.getType(strippedChar)!=Character.NON_SPACING_MARK) {
				builder.append(strippedChar);
			}
		}
		return builder.toString();
	}
}
