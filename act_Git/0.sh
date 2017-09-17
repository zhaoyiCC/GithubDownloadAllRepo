#!/bin/bash
mvn package
cd target
scp act-g.jar ../1.sh ../2.sh root@192.168.7.117:/sdpdata1/zhaoyi


#nohup java -jar act-u.jar &
