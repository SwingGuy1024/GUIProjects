package com.neptunedreams;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.net.URL;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

/**
 * <p>This class was written to try to find a way to paste table data copied from a browser into another
 * application that can handle table data. If the web page using HTML's table tag, this will work fine. But
 * these days, most pages have found other ways to present tabular data, and they don't work well with copy
 * and paste. Typically, each table cell goes on a different line</p>
 * <p>I wrote this to inspect all the different data flavors on the clipboard, hoping to find one that would
 * provide enough info to find the line breaks.</p>
 * <p>I couldn't find anything that works. I did discover that only the basic mime type matters. There may be many variations of a single mime type (70 for text/html) but they all return the same text. This isn't surprising, but there were so many variations that I had to try them just to see for sure.</p>
 * <p>Things to try:</p>
 * <p>Try decoding text/rtf; class="[B"<br>
 * (That didn't work. It extracted the text correctly, but still put each cell on a different line.)</p>
 * <p>TryConverting HTML to Text.
 * <br>Try the Jsoup Library: 
 * <br>{@code String plainText = Jsoup.parse(html).text();}
 * <br>{@code String plainText = Jsoup.parse(html).wholeText();}
 * <br>{@code String plainText = Jsoup.parse(html).wholeOwnText();}
 * <br>None of these worked. 
 * <br><br>Or use HTMLEditorKit:</p>
 * <pre>
 *   // Use a StringBuilder to collect text in a callback
 *   StringBuilder sb = new StringBuilder();
 *   HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() &#123;
 *    {@literal @Override}
 *     public void handleText(char[] data, int pos) &#123;
 *       sb.append(data);
 *     &#125;
 *   &#125;
 *   new ParserDelegator().parse(new StringReader(html), callback, false);
 *   String result = sb.toString();
 *   </pre>
 *   <br>This didn't work either.
 *   <p><strong>What will work:</strong></p>
 *   <p>I am going to write an application that lets the user specify the number of columns, and shows the user
 *   the results of their choice in a JTable, allowing them to adjust their choice before pasting into 
 *   another application.</p>
 *
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 4/16/26
 * <br>Time: 11:36 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
public enum ClipboardSpy {
  ;

  public static void main(String[] args) throws AWTException {
    SystemTray tray = SystemTray.getSystemTray();
    tray.add(getSpyTool());
  }
  
  private static TrayIcon getSpyTool() {
    ImageIcon imageIcon = getImageIcon("/eye_half.png");
    TrayIcon trayIcon = new TrayIcon(imageIcon.getImage(), "Clipboard Spy");
    trayIcon.setImageAutoSize(true);
    PopupMenu popupMenu = new PopupMenu();
    trayIcon.setPopupMenu(popupMenu);
    addDataFilter(popupMenu, "Clipboard Data View…");
    popupMenu.addSeparator();
    popupMenu.add(exitItem());
    return trayIcon;
  }

  @NotNull
  private static ImageIcon getImageIcon(String iconName) {
    final URL resource = ClipboardSpy.class.getResource(iconName);
    return new ImageIcon(Objects.requireNonNull(resource));
  }

  private static MenuItem exitItem() {
    MenuItem exitItem = new MenuItem("Exit");
    exitItem.addActionListener((e) -> System.exit(0));
    return exitItem;
  }

  private static void addDataFilter(PopupMenu popupMenu, String name) {
    MenuItem menuItem = new MenuItem(name);
    menuItem.addActionListener(e -> FlavorView.processClipboardData());
    popupMenu.add(menuItem);
  }
  
  private static JPanel constrainWidth(JComponent component, int width) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(component, BorderLayout.CENTER);
    panel.add(Box.createHorizontalStrut(width), BorderLayout.PAGE_END);
    return panel;
  }

}

/*
<meta charset='utf-8'><div class="

• flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400;
 letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal;
  text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="

  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$999</div><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">15</div></div><div class="

• flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Smartphone</div><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$699</div><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">28</div></div><div class="

• flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Tablet</div><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$399</div><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">12</div></div><div class="

• flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400;
 letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal;
  text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="

  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">Chromebook</div><div class="
  ---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">$299</div></div>


• flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400;
• flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400;

 letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal;
 letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal;

  text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
  text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="

 */
