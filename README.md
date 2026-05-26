# GUIProjects
Miscellaneous GUI projects, mostly for fun or specialty use in software development. Each project builds to a Mac executible and an executable jar file.
### (Icon Notes)
Icons may be converted at AnyConv.com, or using the Graphics Converter. The Converter file types are listed alphabetically, but you won't find the .icns files under I, but under Apple.

## Amazing
My friend Wolf McNalley introduced me to Labyrinths, like the one drawn on the floor of the Cathedral at Chartes. That labyrinth has four axes, and Wolf showed
me one he came up with that had five axes. After giving it some thought, I wrote this tool to generate a labyrinth with any number of axes. (It takes a long time,
even on today's machines, to create one with more than a hundred axes.)

## Anagram
This fun tool is for anagram lovers. Given an anagram, it helps you solve it by removing letters from the anagram when you type them in the solution field, and 
prevents you from typing letters that aren't allowed.

## AsciiTable
This pops open a window with a table of ASCII and ISO-8859 characters. The ones I find on the internet are often missing some essential feature, like hex values, or ISO-8859 characters, so I created my own chart. Tool tips show the meanining of non-printing and special characters, even those that are obsolete.

## BeatCounter
I'm a swing dancer, so I like to record the beats-per-second of songs in my playlists. With this tool, I can tap the space bar once per beat, and it will show the beats per second.

## ClipboardSpy
This lets you look at what's on the system clipboard, if it's text data. I wrote this to investigate the various ways that table data is structured when copied from a Web Page. I incorporated what I learned into the TableTool module of this project.

This has some minor problems as a GUI application, but I mainly used it directly from my IDE. But it's still usable. When you launch it, it puts an eye icon on your System Tray, which is the right side of the menubar on a Mac, or the right side of the taskbar on Windows. (I've never tried this on Linux.) When you click on the eyeball, it pops up a menu with "Clibboard Data View…" and "Exit." (Exit will remove it from the menu.) When you choose Clipboard Data View, it pops up a window that lets you choose what mime type you want to view, and which encoding and representation class to use to view the data. The point is to let you know what options are available. It's intended for developers who need to process clipboard text data. I don't support image data because I didn't need it, but it could be added pretty easily.

## ClipboardTray
This puts a menu on the System Tray, which is on the task bar on Windows and Linux machines, and on the right side of the toolbar on Macintosh. The menu contains tools that act on whatever text is on the clipboard. They transform the text, then put it back on the clipboard. For example, to convert styled text to plain text, just copy your styled text, choose "To Plain" from the menu, and paste it somewhere. It will show up as plain, unformatted text. This is useful when an ordinary paste will paste text with the wrong size or font.

## Crypto
Helps to solve cryptograms. I need to re-think the interface.

## CValues
If you have some unfamiliar text and want to know the unicode values of the characters, paste them in here, and it will show you the hex values. This is useful for debugging strange copy/paste operations.

## EinsteinTiles
Rudimentary code for drawing Einstein tiles at various values on the continuum. 

## Escape
Shows the unicode escape values of any text, and translates between readable text and Java unicode escape sequences. You may also type an escape sequence into the text and it will get automatically translated into the character it represents. This also shows many obscure but potentially useful characters like lines, arrows, math symbols, emoticons, and others. These may be easily copied and pasted into other applications.

## LinkBuilder
Convenience editor to build links for pasting into documents that accept HTML.

## Penrose
Adapted from a applet I found online to design and modify Penrose Tiles. 

## QuordleAid
This helps me solve [Quardle](URL "https://www.quordle.com/#/") puzzles. It also helps with [Wordle](URL "https://www.nytimes.com/games/wordle/index.html").

## RefBuilder
A tool to create and edit references for use in Wikipedia articles. You may copy existing references and edit them, then paste them back in. It makes the process go much smoother. This also supports undo for most edits. Currently, changes to the table are not undoable. (The table is used for author and editor names.)

## SetGame
The game of Set, modified for use on a computer.

## TableTool
When copying table data from a web page to an application like Excel, the positions of the column breaks often gets lost, and each cell is put on a separate line. This tool lets you specify the number of columns before pasting your table data. It can sometimes infer how many columns there were in the copied text. If not, it lets you specify the number of columns and presents a table for you to confirm your number is correct. (There are at least three ways to present table data in a web page. If the table was created using the table tag, it will preserve the column breaks. The other ways don't do so.)

This project lets you build three ways. If you have a java installed on your system, you can build an app that doesn't include the Java runtime. Or you can include a java runtime for Intel Macs or Silicon Macs (also called ARM). Each build get its own target folder.

## UrlDecode
Simple utility for decoding URL-encoded characters in web links. URL encoding is also known as percent encoding.

## Notes:
### Making Application Icons
Here's the quick way I used to make my pretty simple Application icons, which need to be .icns files. 
1. I started with Microsoft Word. I went to **Preferences : General : Personalize** and chose the **Dark Mode has a White Page Color** option.
1. In a new Word document, I typed a letter in the 72 pt size, and chose a font and style. Then I selected the text.
1. I chose a text color, and went to Text Effects and chose an outline color of very light gray. I set the size of the outline to 1.5 pts for most icons.
1. I used Cmd-shift-4 to drag across the icons to create a screen dump.
1. I opened the screen dump in PaintBrush.
1. I chose the selection tool, which adds two options to the tool bar. I chose the second option, which makes the selection background transparent. Then I selected the entire image and chose **Copy**.
1. I chose "New from Clipboard" in PaintBrush. It shows up with a checkerboard background, which means
   transparent. 
1. I saved the image file with a `.png` file extension.
1. I opened that file in Graphic Converter and clicked on **Open and Retain** to keep the  alpha channel, which is where transparency is recorded. Then I converted it to **Apple File Icons (*.icns)**
1. Some of the Icons were converted using AnyConv.com, but we don't need this site. Any .png file may be converted by the Mac GraphicConverter application by going to "Save As…" and choosing the **Apple File Icon** format. (Don't look for **.icns**.).

### Apple File Extensions
1. For Jar files with Apple extensions, check out https://mvnrepository.com/artifact/com.apple/applejavaextensions/1.5.4

### Writing agents
Agents are applications that don't appear on the task bar. To make an application an agent, just set the `HideOnDock` property to true in the subproject's `pom.xml` file:

    <properties>
      <HideOnDock>true</HideOnDock>
    <properties>
