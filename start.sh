#!/bin/bash

# This is the start.sh file for Genisys
# Please input ./start.sh to start server

# Variable define
DIR="$(cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"

# Change Directory
cd "$DIR"

DO_LOOP="no"
PHP_BINARY=""
POCKETMINE_FILE=""

# 设置 PHP 路径为已编译的路径
# PHP_BINARY="/usr/local/php73/bin/php"

# 检查 PHP 是否存在
# if [ ! -f "$PHP_BINARY" ]; then
#     echo "[ERROR] 未找到 PHP 文件: $PHP_BINARY"
#     exit 1
# fi

if [ -e ./bin/php ]; then
    PHP_BINARY="./bin/php"
elif [ -e /usr/local/php73/bin/php ]; then
    PHP_BINARY="/usr/local/php73/bin/php"
elif [ -e /usr/bin/php ]; then
    PHP_BINARY="/usr/bin/php"
elif [ -e ./bin/php73/bin/php ]; then
    PHP_BINARY="./bin/php73/bin/php"
else
    echo "[ERROR] 未找到 PHP 可执行文件"
    exit 1
fi

# 检查核心文件
if [ -f ./PocketMine-iTX.phar ]; then
    POCKETMINE_FILE="./PocketMine-iTX.phar"
elif [ -f ./Genisys*.phar ]; then
    POCKETMINE_FILE="./Genisys*.phar"
elif [ -f ./PocketMine-MP.phar ]; then
    POCKETMINE_FILE="./PocketMine-MP.phar"
elif [ -f ./src/pocketmine/PocketMine.php ]; then
    POCKETMINE_FILE="./src/pocketmine/PocketMine.php"
elif [ -f ./GrassServer*.phar ]; then
    POCKETMINE_FILE="./GrassServer"
else
    echo "[ERROR] 未找到 PocketMine 核心"
    exit 1
fi

# 设置 php.ini 路径
# PHP_INI_FILE="/usr/local/php73/lib/php.ini"
if [ -f "./php.ini" ]; then
    PHP_INI_FILE="./php.ini"
elif [ -f "/usr/local/php73/lib/php.ini" ]; then
    PHP_INI_FILE="/usr/local/php73/lib/php.ini"
elif [ -f "/etc/php.ini" ]; then
    PHP_INI_FILE="/etc/php.ini"
else
    PHP_INI_FILE=""
fi

if [ -f "$PHP_INI_FILE" ]; then
    PHP_INI_OPTION="-c"
    PHP_INI_PATH="$PHP_INI_FILE"
else
    echo "[WARNING] 未找到 php.ini: $PHP_INI_FILE"
    PHP_INI_OPTION=""
fi

set +e
LOOPS=0

while [ "$LOOPS" -eq 0 ] || [ "$DO_LOOP" == "yes" ]; do
    if [ "$DO_LOOP" == "yes" ]; then
        if [ -n "$PHP_INI_OPTION" ]; then
            "$PHP_BINARY" $PHP_INI_OPTION "$PHP_INI_PATH" "$POCKETMINE_FILE" "$@"
        else
            "$PHP_BINARY" "$POCKETMINE_FILE" "$@"
        fi
    else
        if [ -n "$PHP_INI_OPTION" ]; then
            "$PHP_BINARY" $PHP_INI_OPTION "$PHP_INI_PATH" "$POCKETMINE_FILE" "$@"
        else
            "$PHP_BINARY" "$POCKETMINE_FILE" "$@"
        fi
        break
    fi
    ((LOOPS++))
done

if [ ${LOOPS} -gt 1 ]; then
    echo "[INFO] 重启 $LOOPS 次"
fi

exit 0
