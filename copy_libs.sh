#!/bin/bash
#Copies the generated library files from ccn-lite into the directory required by the android app

BASEDIR=$(dirname "$0")
LIBDIR="ccn-lite/src/mkC-android/libs/"
TARGETDIR="android/app/src/main/asd/asd/jniLibs/"

echo "Checking directories..."

if [ ! -d "$BASEDIR/$LIBDIR" ]
then
	echo "Couldn't find source dir $BASEDIR/$LIBDIR !"
	exit
fi

if [ ! -d "$BASEDIR/$TARGETDIR" ]
then
	echo "Couldn't find target dir $BASEDIR/$TARGETDIR !"
	echo "Create new target directory?"
	while true
		do
		echo -n "Please confirm (y or n) :"
		read CONFIRM
		case $CONFIRM in
		y|Y|YES|yes|Yes) break ;;
	      n|N|no|NO|No)
	           echo Aborting - you entered $CONFIRM
	          exit
	        ;;
	    *) echo Please enter only y or n
	      esac
	      done
	      echo "Creating the directiory (and possibly subdirectories) for $TARGETDIR ..."
	      mkdir -p "$TARGETDIR"

fi

echo "Directories ok!"
echo "Copying libraries to target directory..."

rsync -avh "$BASEDIR/$LIBDIR" "$BASEDIR/$TARGETDIR"

echo "Done."
