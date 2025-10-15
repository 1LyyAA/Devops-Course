#!/bin/bash

repo_url=$1
branch1=$2
branch2=$3

timestamp=$(date "+%Y-%m-%d %H:%M:%S")

filename="diff_report_${branch1}_${branch2}.txt"

exec > "$filename"


echo "Отчет о различиях между ветками"
echo "==============================================="
echo "Репозиторий: $repo_url"
echo "Ветка 1: $branch1"
echo "Ветка 2: $branch2"
echo "Дата генерации: $timestamp"
echo "==============================================="

echo
echo "СПИСОК ИЗМЕНЕННЫХ ФАЙЛОВ:"

git clone "$repo_url" repo_temp

cd repo_temp || exit 1

git fetch origin
git diff --name-status "origin/$branch1" "origin/$branch2"


echo
echo "СТАТИСТИКА:"
echo "Всего измененных файлов: $(git diff --name-status "origin/$branch1" "origin/$branch2" | wc -l)"
echo "Добавлено (A): $(git diff --name-status "origin/$branch1" "origin/$branch2" | grep '^A' | wc -l)"
echo "Удалено (D): $(git diff --name-status "origin/$branch1" "origin/$branch2" | grep '^D' | wc -l)"
echo "Изменено (M): $(git diff --name-status "origin/$branch1" "origin/$branch2" | grep '^M' | wc -l)"

cd ..
rm -rf repo_temp