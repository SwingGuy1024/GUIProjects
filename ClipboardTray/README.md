# ClipboardTray
This is called Clipboard Tray because it's items act on the text contents of the clipboard. They transform it, then put
the modified text back on the clipboard.

# Build Notes
When building, the Maven app bundler plug-in makes one mistake. I've coded it to set a special property in the Mac
Application Bundle's Info.plist file. The purpose is to prevent this application from appearing in the Dock when it's
open. When done correctly, it works. But the plug-in handles it wrong. The plug-in puts the property in like this: 

    <key>LSUIElement</key>
    <true/>

But it's supposed to look like this:

    <key>LSUIElement</key>
    <string>true</string>

So to get this right, I need to make this modification to the Info.plist file after building. I have a tool to do this
called ClipboardFix in the project's base directory. It's an executible file, so it can be run from the command line
in that directory and it will find the Info.plist file and fix it.

Without this fix, the ClipboardTray still works fine, it just shows up in the Dock, which I want to avoid.

Once an application has been signed, the .plist file can't be modified, but I haven't signed it so this is okay.

I've filed this bug with the developer, but I wouldn't be surprised if he never even looks at this:

https://github.com/perdian/macosappbundler-maven-plugin/issues/50
