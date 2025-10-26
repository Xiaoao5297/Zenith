#!/bin/bash

# This is the start.sh file for Genisys
# Please input ./start.sh to start server

# Variable define
DIR="$(cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"

# Change Directory
cd "$DIR"

# Loop starting
# Don't edit this if you don't know what this does!

DO_LOOP="no"

##########################################
# DO NOT EDIT ANYTHING BEYOND THIS LINE! #
##########################################

while getopts "p:f:l" OPTION 2> /dev/null; do
    case ${OPTION} in
        p)
            PHP_BINARY="$OPTARG"
            ;;
        f)
            POCKETMINE_FILE="$OPTARG"
            ;;
        l)
            DO_LOOP="yes"
            ;;
        \?)
            break
            ;;
    esac
done

if [ "$PHP_BINARY" == "" ]; then
    if [ -f ./bin/php ]; then
        export PHPRC=""
        PHP_BINARY="./bin/php"
    elif type php 2>/dev/null; then
        PHP_BINARY=$(type -p php)
    elif [ -f ./bin/php7/bin/php ]; then
        PHP_BINARY="./bin/php7/bin/php"
    else
        echo "[ERROR] 未找到 PHP 文件，请安装"
        exit 1
    fi
fi

if [ "$POCKETMINE_FILE" == "" ]; then
    if [ -f ./PocketMine-iTX.phar ]; then
        POCKETMINE_FILE="./PocketMine-iTX.phar"
    elif [ -f ./Genisys*.phar ]; then
            POCKETMINE_FILE="./Genisys*.phar"
    elif [ -f ./PocketMine-MP.phar ]; then
        POCKETMINE_FILE="./PocketMine-MP.phar"
    elif [ -f ./src/pocketmine/PocketMine.php ]; then
        POCKETMINE_FILE="./src/pocketmine/PocketMine.php"
    elif [ -f ./Zenith.phar ]; then
        POCKETMINE_FILE="./Zenith.phar"
    else
        echo "[ERROR] 未找到 PocketMine 核心"
        exit 1
    fi
fi

LOOPS=0

if [ -f ./php.ini ]; then
    PHP_INI_OPTION="-c"
    PHP_INI_FILE="./php.ini"
else
    PHP_INI_OPTION=""
    PHP_INI_FILE=""
fi

set +e
while [ "$LOOPS" -eq 0 ] || [ "$DO_LOOP" == "yes" ]; do
    if [ "$DO_LOOP" == "yes" ]; then
        if [ -n "$PHP_INI_OPTION" ]; then
            "$PHP_BINARY" $PHP_INI_OPTION "$PHP_INI_FILE" "$POCKETMINE_FILE" $@
        else
            "$PHP_BINARY" "$POCKETMINE_FILE" $@
        fi
    else
        if [ -n "$PHP_INI_OPTION" ]; then
            exec "$PHP_BINARY" $PHP_INI_OPTION "$PHP_INI_FILE" "$POCKETMINE_FILE" $@
        else
            exec "$PHP_BINARY" "$POCKETMINE_FILE" $@
        fi
    fi
    ((LOOPS++))
done

if [ ${LOOPS} -gt 1 ]; then
    echo "[INFO] 重启 $LOOPS 次"
fi
