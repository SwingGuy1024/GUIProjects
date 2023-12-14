# GUIProjects
Miscellaneous GUI projects, mostly for fun or specialty use in software development. Each project builds to a Mac executible and an executable jar file.
### (Icon Notes)
Icons may be converted at AnyConv.com, or using the Graphics Converter. The Converter file types are listed alphabetically, but you won't find the .icns files under I, but under Apple.

## Amazing
My friend Wolf McNalley introduced me to Labyrinths, like the on drawn on the floor of the Cathedral at Chartes. That labyrinth has four axes, and Wolf showed
me one he came up with that had five axes. After giving it some thought, I wrote this tool to generate a labyrinth with any number of axes. (It takes a long time,
even on today's machines, to create one with more than a hundres axes.)

## Anagram
This fun tool is for anagram lovers. Given an anagram, it helps you solve it by removing letters from the anagram when you type them in the solution field, and 
prevents you from typing letters that aren't allowed.

## CValues
If you have some unfamiliar text and want to know the unicode values of the characters, paste them in here, and it will show you the hex values. This is useful
for debugging strange copy/paste operations.

## Escape
Shows the unicode escape values of any text, and translates between readable text and Java unicode escape sequences.

## QuordleAid
This helps me solve [Quardle](URL "https://www.quordle.com/#/") puzzles. It also helps with [Wordle](URL "https://www.nytimes.com/games/wordle/index.html").

## Notes:
### Making Application Icons
Here's the quick way I used to make my pretty simple Application icons, which need to be .icns files. 
1. I started with Microsoft Word. I went to **Preferences : General : Personalize** and chose the **Dark Mode has a White Page Color** option.
1. In a new Word document, I typed a letter in the 72 pt size, and chose a font and style. Then I selected the text.
1. I left the text color at black, and went to Text Effects and chose an outline color of very light gray. I set the size of the outline to 1.5 pts for most icons.
1. I used Cmd-shift-4 to drag across the icons to create a screen dump.
1. I opened the screen dump in PaintBrush.
1. I chose the selection tool, which adds two options to the tool bar. I chose the second option, which makes the selectioin background transparent. Then I selected the entire image and chose **Copy**.
1. I launched Gimp, and chose **File: Create: From Clipboard**. It should show up with a checkerboard background, which means transparent.
1. I chose File: **Export As…** and gave the new file a `.png` file extension.
1. I opened that file in Graphic Converter and clicked on **Open and Retain** to keep the  alpha channel. Then I converted it to **Apple File Icons (*.icns)**
1. Some of the Icons were converted using AnyConv.com, but we don't need this site.
