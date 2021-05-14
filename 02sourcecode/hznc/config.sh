#! /bin/sh
# -*- sh -*-

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

subst()
{
    f=$1
    real_f=$2
    echo "create  $real_f from $f"
    sed  \
	-e "s&@LCM_GUARD_ROOT@&$PRG_FULL_DIR&g" \
	$f > $real_f    
}

backup_file()
{
    [ -f ${1} ] &&    cp -f ${1} ${1}.bak
}

#get shell full path
cd $PRGDIR

PRG_FULL_DIR=`pwd -P`

in_file_list=`find $PRGDIR -name "*.in"`
for f in $in_file_list ; do
    real_f=${f%.*}
    backup_file $real_f
    subst $f  $real_f
done

cd - >/dev/null

softlink=/opt/data-center

##if the symbol link is already exist,delete if first 
if [ -h $softlink ]; then
    rm -rf $softlink
fi

ln -s $PRG_FULL_DIR $softlink
