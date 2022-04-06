package com.mm.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * Created by IntelliJ IDEA.
 * User: Miguel
 * Date: Jan 24, 2006
 * Time: 12:12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class Anagram extends JPanel {
	private JTextField mOriginal;
	private JTextField mCode;
	private JTextField mSolution;

	public static void main(String[] args) {
		JFrame anagramWindow = new JFrame("Anagrams");
		anagramWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		anagramWindow.add(new Anagram(), BorderLayout.CENTER);
		anagramWindow.setBounds(10, 10, 300, 200);
		anagramWindow.setVisible(true);
	}

	public Anagram() {
		super(new BorderLayout());
		mOriginal = new JTextField();
		mCode = new JTextField();
		mCode.setEditable(false);
		mSolution = new JTextField();
		Font font = new Font("monospaced", Font.PLAIN, 12);
		mCode.setFont(font);
		mSolution.setFont(font);
		JPanel pnl = new JPanel(new GridLayout(0, 1));
		add(pnl, BorderLayout.NORTH);
		pnl.add(mOriginal);
		pnl.add(mCode);
		pnl.add(mSolution);
		mOriginal.setText("So Dark the Con of Man");
		((PlainDocument)mSolution.getDocument()).setDocumentFilter(makeSolutionListener());
		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent e) {
				mOriginal.selectAll();
			}
			public void focusLost(FocusEvent e) {
				if (!e.isTemporary())
				{
					mCode.setText(mOriginal.getText());
					mSolution.setText("");
					mSolution.requestFocusInWindow();
				}
			}
		};
		mOriginal.addFocusListener(fl);
	}

	private DocumentFilter makeSolutionListener() {
		return new DocumentFilter() {
			public void insertString(FilterBypass fb, int offset, String answer, AttributeSet attr) throws BadLocationException {
				StringBuilder riddle = new StringBuilder(mCode.getText().toUpperCase());
				for (int ii=0; ii<answer.length(); ++ii)
				{
					char cc = answer.charAt(ii);
					String ccx = "" + Character.toUpperCase(cc);
					if (Character.isLetterOrDigit(cc)) {
						int where = riddle.indexOf(ccx);
						if (where >= 0) {
							riddle.setCharAt(where, ' ');
							fb.insertString(offset+ii, ""+cc, attr);
						} else {
							java.awt.Toolkit.getDefaultToolkit().beep();
						}
					} else {
						fb.insertString(offset+ii, ""+cc, attr);
					}
				}
				mCode.setText(riddle.toString());
			}

			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				String gone = mSolution.getDocument().getText(offset, length);
				StringBuilder riddle = new StringBuilder(mCode.getText().toUpperCase());
				String original = mOriginal.getText().toUpperCase();
				for (int ii=0; ii<gone.length(); ++ii) {
					char cc = Character.toUpperCase(gone.charAt(ii));
					String ccx = "" + cc;
					if (Character.isLetterOrDigit(cc)) {
						int where = original.indexOf(ccx);
						while (where >= 0 && Character.toUpperCase(original.charAt(where)) == Character.toUpperCase(riddle.charAt(where))) {
							where = original.indexOf(ccx, where+1);
						}
						if (where >= 0) {
							mCode.getDocument().remove(where, 1);
							mCode.getDocument().insertString(where, ccx, null);
							riddle = new StringBuilder(mCode.getText().toUpperCase());
						}
					}
				}
				fb.remove(offset, length);
			}

			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
				if (length>0)
					remove(fb, offset, length);
				if (text != null)
					insertString(fb, offset, text, attrs);
			}
		};
	}
}
