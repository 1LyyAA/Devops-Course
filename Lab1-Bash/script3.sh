#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 <log_file> <keyword>"
    exit 1
fi





File=$1
Key_word=$2

if [ ! -f "$File" ]; then
    echo "Error: File '$File' not found"
    exit 1
fi

if [ -z "$Key_word" ]; then
    echo "Error: Keyword is empty"
    exit 1
fi

count=$(grep -c "$Key_word" "$File")
echo "found $count lines with '$Key_word' in file $File"

grep "$Key_word" "$File" > ./errors.txt
