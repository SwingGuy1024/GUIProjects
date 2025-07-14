# Penrose Tiles
I found this Penrose Tile applet on the internet somewhere, a long time ago. I downloaded the jar file and packaged it as a Mac app for my own use. It was very useful in experimenting with different designs for my kitchen floor tiles.

With the release of Mac OS Big Sur, or Maybe High Sierra or Catalina, this app stopped working because it was out of date. Mostly it was the packaging that was out of date, but some of the APIs were deprecated, too. So I unzipped the jar file and decompiled it to get it running again under a modern Java VM. I didn't need to restore all the classes in the jar file. Some came from a geometry library and weren't used in the app. The class files are stored in the TargetX folder.

## Building
To build the application, you need to use Maven. I currently use Maven 3.9.9, although some earlier versions will probably work, too. I have the Java level set to 17, although 11 and 21 will probably work just as well.

### Instructions
First, install Maven 3.9.9 or later, and Java 17 or later. Then put both on your path.
Finally, go to the root folder of this project and type 
`mvn clean install`

This will create a target folder, which will contain a folder called `Penrose.app` and a jar file called `Penrose-1.0-SNAPSHOT.jar`, among other things.

### Running
#### Macintosh
On the Mac, the `Penrose.app` folder is the application, which you may launch like any other application by double-clicking it.

#### Other Systems
On windows, Linux, and, yes, Macintosh, you may also run it by opening a command shell, changing to the target directory, and typing `java -jar Penrose-1.0-SNAPSHOT.jar`

The .jar file is self contained, so you may move it to any other directory you choose and run it from there.
