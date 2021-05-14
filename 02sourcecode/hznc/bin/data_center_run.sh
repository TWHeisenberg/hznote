#! /bin/bash
# -*- sh -*-

# fd
ulimit -Hn 65535
ulimit -Sn 65535

# Resolve links - $0 may be a softlink
PRG="$0"
while [ -h "$PRG" ]; do
        ls=`ls -ld "$PRG"`
        link=`expr "$ls" : '.*-> \(.*\)$'`
        if expr "$link" : '.*/.*' > /dev/null; then
                PRG="$link"
        else
                PRG=`dirname "$PRG"`/"$link"
        fi
        done
PRGDIR=`dirname "$PRG"`

cd $PRGDIR/..
MY_PROGRAM_HOME=`pwd`
cd - >/dev/null 2>&1

if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif [ -x "/usr/java/default/bin/java" ]; then
    JAVA="/usr/java/default/bin/java"
else
    JAVA=`which java`
fi

if [ ! -x "$JAVA" ]; then
     echo "Could not find any executable java binary. Please install java in your PATH or set JAVA_HOME"
     exit 1
fi

OPTION=${1}
suffix=$(echo $OPTION |awk -F '=' '{print $2}')

JAVA_OPTS="$JAVA_OPTS -Djava.io.tmpdir=/opt/data-center/tmp"
JAVA_OOM="-XX:OnOutOfMemoryError='kill -9 %p'"
# GC logging options
MY_PROGRAM_USE_GC_LOGGING=y
if [ "x$MY_PROGRAM_USE_GC_LOGGING" != "x" ]; then
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDateStamps"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintClassHistogram"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintTenuringDistribution"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime"
  JAVA_OPTS="$JAVA_OPTS -Xloggc:$MY_PROGRAM_HOME/logs/data_center_gc.out"
  JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$MY_PROGRAM_HOME/lib"
  JAVA_OPTS="$JAVA_OPTS -Djna.library.path=$MY_PROGRAM_HOME/lib"
  
fi


cd $MY_PROGRAM_HOME
LIB_JAR=${MY_PROGRAM_HOME}/lib/*
exec $JAVA $JAVA_OPTS "$JAVA_OOM" -cp "$LIB_JAR" com.hznc.datacenter.DataCenterLaunch realtime_spif_hbase etc/config.properties &
exit $?

