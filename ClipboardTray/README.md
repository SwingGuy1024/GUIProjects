# ClipboardTray
This is called Clipboard Tray because it's items act on the text contents of the clipboard. They transform it, then put
the modified text back on the clipboard.

# Build Notes

If you want an application to be an agent, which does not appear on the task bar, you can do this by setting the `HideOnDock` property to true. Support for this property is written into the master pom file for the parent of this project. (I had to file a bug with the plug-in developer, in the link below. I fixed it in a fork, and sent in a pull request. The developer was very quick about integrating the fix into the master branch. Here's a link to the issue: https://github.com/perdian/macosappbundler-maven-plugin/issues/50)
