#!/bin/bash


pid_file="/tmp/my_script.pid"

argument=$1

case $argument in
    START)
        if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
            echo "Скрипт уже запущен (PID $(cat "$pid_file"))"
        else
            echo "Запуск скрипта в фоне..."
            
            while true; do
                timestamp=$(date "+%Y-%m-%d %H:%M:%S")
                all_memory=$(free -h | grep Mem | awk '{print $2}')
                free_memory=$(free -h | grep Mem | awk '{print $4}')
                memory_used=$(free -h | grep Mem | awk '{print $3}')
                cpu_used=$(top -bn1 | grep "Cpu(s)" | awk '{print $2 + $4"%"}')  # %
                disk_used=$(df -h / | awk 'NR==2 {print $5}')
                load_average_1m=$(uptime | awk -F'load average:' '{ print $2 }' | cut -d, -f1 | xargs)

                 csv_file="./system_report_$(date "+%Y-%m-%d").csv"
                [ -f "$csv_file" ] || echo "timestamp;all_memory;free_memory;memory_used;cpu_used;disk_used;load_average_1m" > "$csv_file"
                echo "$timestamp;$all_memory;$free_memory;$memory_used;$cpu_used;$disk_used;$load_average_1m" >> "$csv_file"
                # sleep 10 minutes
                sleep 600
            done &

            echo $! > "$pid_file"
            echo "Скрипт запущен (PID $!)"
        fi
        ;;

    STOP)
        if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
            kill $(cat "$pid_file")
            rm -f "$pid_file"
            echo "Скрипт остановлен"
        else
            echo "Скрипт не запущен"
        fi
        ;;
    STATUS)
        if [ -f "$pid_file" ] && kill -0 "$(cat "$pid_file")" 2>/dev/null; then
            echo "Скрипт запущен (PID $(cat "$pid_file"))"
        else
            echo "Скрипт не запущен"
        fi
        ;;
    *)
        echo "Usage: $0 {START|STOP|STATUS}"
        exit 1
        ;;
esac

