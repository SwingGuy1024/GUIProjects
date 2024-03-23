package com.neptunedreams.ascii;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.MatteBorder;

/**
 * <p>ASCII Table</p>
 * <p>I created this because every ASCII table I found in a quick internet search was missing some of what I was
 * looking for. Either some of the extended characters were missing, or they didn't show hex values, or didn't 
 * show decimal values</p>
 * <p>Sources:</p>
 * <p>&nbsp;&nbsp;<a href="https://www.ascii-code.com/">https://www.ascii-code.com/</a></p>
 * <p>&nbsp;&nbsp;<a href="https://www.fileformat.info/info/charset/ISO-8859-1/list.htm">https://www.fileformat.info/info/charset/ISO-8859-1/list.htm</a></p>
 * <p>&nbsp;&nbsp;<a href="https://en.wikipedia.org/wiki/C0_and_C1_control_codes">https://en.wikipedia.org/wiki/C0_and_C1_control_codes</a></p>
 */
public class AsciiTable extends JPanel {

  private static final Color codeBgColor = new Color(224, 224, 224);
  public static final int SPACE = 32;
  public static final int ASCII_MAX = 256;

  public static void main(String[] args) {
    JFrame frame = new JFrame("ASCII/ISO-8859-1 Values");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.add(new AsciiTable());
    frame.pack();
    frame.setVisible(true);
  }
  
  private enum Code {
    NUL("Null"),
    SOH("Start of Heading"),
    STX("Start of Text"),
    ETX("End of Text"),
    EOT("End of Transmission"),
    ENQ("Enquiry"),
    ACK("Acknowledgement"),
    BEL("Bell"),
    BS("Back Space"),
    HT("Horizontal Tab"),
    LF("Line Feed"),
    VT("Vertical Tab"),
    FF("Form Feed"),
    CR("Carriage Return"),
    SO("Shift Out / X-On"),
    SI("Shift In / X-Off"),
    DLE("Data Line Escape"),
    DC1("Device Control 1 (X-ON)"),
    DC2("Device Control 2"),
    DC3("Device Control 3 (X-OFF)"),
    DC4("Device Control 4"),
    NAK("Negative Acknowledgement"),
    SYN("Synchronous Idle"),
    ETB("End of Transmit Block"),
    CAN("Cancel"),
    EM("End of Medium"),
    SUB("Substitute"),
    ESC("Escape"),
    FS("File Separator"),
    GS("Group Separator"),
    RS("Record Separator"),
    US("Unit Separator");
    
    private final String def;
    Code(String def) {
      this.def = def;
    }
    
    String getDef() { return def; }
  }
  
  private enum HighCode {
    DEL(0x7F, "Delete"),
    PAD(0x80, "Padding Character"),
    HOP(0x81, "High Octet Present"),
    BPH(0x82, "Break Permitted Here"),
    NBH(0x83, "No Break Here"),
    IND(0x84, "Index"),
    NEL(0x85, "Next Line"),
    SSA(0x86, "Start of Selected Area"),
    ESA(0x87, "End of Selected Area"),
    HTS(0x88, "Horizontal Tabulation Set"),
    HTJ(0x89, "Horizontal Tabulation with Justification"),
    VTS(0x8A, "Line Tabulation Set"),
    PLD(0x8B, "Partial Line Forward"),
    PLU(0x8C, "Partial Line Backward"),
    RI(0x8D, "Reverse Line Feed"),
    SS2(0x8E, "Single Shift 2"),
    SS3(0x8F, "Single Shift 3"),
    DCS(0x90, "Device Control String"),
    PU1(0x91, "Private Use 1"),
    PU2(0x92, "Private Use 2"),
    STS(0x93, "Set Transmit State"),
    CCH(0x94, "Cancel Character"),
    MW(0x95, "Message Waiting"),
    SPA(0x96, "Start of Guarded ARea"),
    EPA(0x97, "End of Guarded Area"),
    SOS(0x98, "Start of String"),
    SGC(0x99, "Single Graphic Character Introducer"),
    SCI(0x9A, "Single Character Introducer"),
    CSI(0x9B, "Control Sequence Introducer"),
    ST(0x9C, "String Terminator"),
    OSC(0x9D, "Operating System Command"),
    PM(0x9E, "Privacy Message"),
    APC(0x9F, "Application Program Command"),
    NBS(0xA0, "Non-Breaking Space"),
    SHY(0xAD, "Soft Hyphen");
    
    private final String def;
    private final int key;
    
    HighCode(int key, String def) {
      this.def = def;
      this.key = key;
    }

    public String getDef() {
      return def;
    }

    public int getKey() {
      return key;
    }
  }
  
  private final Map<Integer, HighCode> highCodes = makeHighCodes();
  
  private Map<Integer, HighCode> makeHighCodes() {
    Map<Integer, HighCode> highMap = new HashMap<>();
    for (HighCode highCode: HighCode.values()) {
      highMap.put(highCode.getKey(), highCode);
    }
    
    return highMap;
  }
  
  private final Font displayFont = Font.decode(Font.MONOSPACED);
  
  @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
  AsciiTable() {
    super(new GridLayout(0, 8));
    System.out.printf("%s%n", codeBgColor); // NON-NLS
    for (int i=0; i<8; ++i) {
      JComponent label = makeLabel(" dec hx  c", "");
      label.setBorder(new MatteBorder(0, 1, 1, 0, Color.BLACK));
      add(label);
    }
    Code[] values = Code.values();
    List<JComponent> labelList = new LinkedList<>();
    for (int i = 0; i< SPACE; ++i) {
      labelList.add(makeCharView(values[i], i));
    }
    for (int i = SPACE; i< ASCII_MAX; ++i) {
      labelList.add(makeCharView(i));
    }
    
    for (int i = 0; i< SPACE; ++i) {
      for (int c = 0; c< ASCII_MAX; c+= SPACE) {
        add(labelList.get(i+c));
      }
    }
    setBorder(new MatteBorder(1, 0, 1, 1, Color.BLACK));
  }
  
  private JComponent makeCharView(Code code, int value) {
    final JComponent label = makeLabel(String.format("<html>\u00a0%3d\u00a0%02x\u00a0<b><i>%-3s</i></b>\u00a0</html>", value, value, code), code);
    label.setToolTipText(String.format("%s: %s", code, code.getDef()));
    label.setBackground(codeBgColor);
    label.setOpaque(true);
    return label;
  }
  
  private JComponent makeCharView(int value) {
    if (highCodes.containsKey(value)) {
      final HighCode highCode = highCodes.get(value);
      final String s = highCode.toString();
      final JComponent label = makeLabel(String.format("<html>\u00a0%3d\u00a0%02x\u00a0<b>%-3s</b>\u00a0</html>", value, value, s), highCode.getDef());
      label.setToolTipText(String.format("%s: %s", highCode, highCode.getDef()));
      label.setBackground(codeBgColor);
      label.setOpaque(true);
      return label;
    }
    return makeLabel(String.format(" %3d %02x  %c  ", value, value, (char)value), "");
  }
  
  private JComponent makeLabel(String s, Code code) {
    return makeLabel(s, code.getDef());
  }
  
  private JComponent makeLabel(String s, String def) {
    JLabel label = new JLabel(s);
    label.setFont(displayFont);
    label.setBorder(new MatteBorder(0, 1, 0, 0, Color.BLACK));
    if (!def.isEmpty()) {
      label.setToolTipText(def);
    }
    return label;
  }
}
