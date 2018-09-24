# MapNav
J2ME GPS navigation in mobile phone

[![Travis CI](https://travis-ci.org/magdel/MapNav.svg?branch=master)](https://travis-ci.org/magdel/MapNav)
[![Test Coverage](https://img.shields.io/codecov/c/github/magdel/MapNav.svg)](https://codecov.io/github/magdel/MapNav?branch=master)
[![codecov](https://codecov.io/gh/magdel/MapNav/branch/master/graph/badge.svg)](https://codecov.io/gh/magdel/MapNav)
[![codebeat badge](https://codebeat.co/badges/0ffbfbf4-bbeb-480a-a6e1-45eac3b06724)](https://codebeat.co/projects/github-com-magdel-mapnav-master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Mobile navigation with J2ME-enabled phone


### Map direction rotatio, track record

[![Base map with rotation](https://raw.githubusercontent.com/magdel/MapNav/master/docs/img/maprot.gif)]


### Navigational compass with active waypoint info

[![Compass with waypoint direction](https://raw.githubusercontent.com/magdel/MapNav/master/docs/img/comp_ani.gif)]


### Speedometer with active waypoint info

[![Speedometer mode](https://raw.githubusercontent.com/magdel/MapNav/master/docs/img/speed_ani.gif)]


### Flight plan navigation

[![Flight navigation](https://raw.githubusercontent.com/magdel/MapNav/master/docs/img/navrot.gif)]


## How to build

* checkout the repo
* mvn clean package
* get JAR & JAD from /target

## How to develop

To compile and run in emulator or device:
* WTK 2.5, CLDC-1.1, MIDP-2.0 device profile, with extra libs (may be found in /WTK).
* IDE supporting JavaME development: Netbeans 7 is great for that, just add custom user device with extra libs and name mapping some device in emulator. Netbeans project is ready in /nb

How to:
1. Open in Netbeans project in ./nb
2. Some warnings may occur because of platform absense, it's OK
3. Copy files from ./WTK/lib to C:/WTK25/lib (in lib dir of your current WTK installation)
4. Open project Propeties, Platform, Manage emulators..
5. Select Add platform, custom user emulator MIDP JavaME platform
6. Select platform home dir (C:/WTK25)
7. Set Platform name to CustomDeviceRun, set Device name to DefaultColorPhone
8. Clear list of initials libraries. Add all files from C:\WTK25\lib
9. Add javadocs pathes: C:\WTK25\docs\api\midp, C:\WTK25\docs\api\jsr082 and other similars
10. Press ready, select the platform and press OK.
11. Just Run app in Netbeans! (press F6)



To run unit tests:
* Open Maven project in your favorite IDE (like Intellij IDEA)
* Execute unit tests you need

