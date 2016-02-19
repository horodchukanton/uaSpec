#!/usr/bin/env bash
java -cp out:lib/*:lib/jetty-production/lib/*: -Dfile.encoding=UTF-8 com.anykey.uaspec.Main COM5 80
