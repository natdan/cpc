# cpc
CP Counter

For android (build?) to work you need local.properties file at the root of the repository to look something like:
```
# Location of the android SDK
sdk.dir=/home/<user>/java/adt-bundle-mac-x86_64-20131030/sdk
```

where /home/<user> is really your home directory and rest points to Android SDK (downloaded from https://developer.android.com/studio/#downloads)

For importing project to Eclipse or Idea check here: https://libgdx.badlogicgames.com/documentation/gettingstarted/Importing%20into%20IDE.html

## Main class

Main class is in Desktop project called ```org.ah.cpc.desktop.StartCPCDesktop```.
Also, there's ```org.ah.cpc.desktop.StartCPCDesktopJOGL``` for Raspberry Pi. (It might work on the desktop/laptop, too, - at least it
'works on my machine'.)

## Known issues

```
Linking into /Users/daniel/Documents/Projects/AbstractHorizon/libgdx/cpc/html/war/html
   Invoking Linker Cross-Site-Iframe
      [ERROR] The Cross-Site-Iframe linker does not support <script> tags in the gwt.xml files, but the gwt.xml file (or the gwt.xml files which it includes) contains the following script tags: 
soundmanager2-setup.js
soundmanager2-jsmin.js
In order for your application to run correctly, you will need to include these tags in your host page directly. In order to avoid this error, you will need to remove the script tags from the gwt.xml file, or add this property to the gwt.xml file: <set-configuration-property name='xsiframe.failIfScriptTag' value='FALSE'/>
```
when building HTML project with WT 2.8.0 (and current version of RPI enabled 1.9.8-SNAPSHOT of LibGDX). But we
don't need HTML at the moment so 'everything is fine...'
