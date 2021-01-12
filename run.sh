#!/bin/sh
cd ./target
java -Droot.level=INFO -jar ./subfolders-1.0.0-SNAPSHOT-fat.jar -cp .
