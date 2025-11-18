# Чтобы запустить

docker compose build
docker compose up -d

# Чтобы остановить
docker compose down

# Чтобы выполнить плейбук Ansible
docker exec ansible-master ansible-playbook -i lab.hosts lab.yml

# для проверки работы лабораторной 
docker compose ps

curl http://localhost:8080