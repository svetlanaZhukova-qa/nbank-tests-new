#!/bin/bash

# Настройка
IMAGE_NAME=nbank-tests-iteration2
TEST_PROFILE=${1:-iteration2_api} # аргумент запуска
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output-iteration2/$TIMESTAMP

# Собираем Docker образ
echo ">>> Сборка тестов запущена"
#docker build -t $IMAGE_NAME .- на случай, если один докерфайл в проекте
 docker build -f Dockerfile_iteration2 -t $IMAGE_NAME .
#docker build -f Dockerfile_iteration2 --no-cache -t nbank-tests-iteration2 .
#docker build --no-cache -t <name> .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

# Запуск Docker контейнера
echo ">>> Тесты запущены"
docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://94.41.189.137 \
  -e UIBASEURL=http://94.41.189.137 \
 $IMAGE_NAME

# Вывод итогов
echo ">>> Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты тестов: $TEST_OUTPUT_DIR/results"
echo "Репорт: $TEST_OUTPUT_DIR/report"



# Как запустить докер-контейнер на основе докер-образа?

# собрать докер-образ(как компиляция для класса)
# запустить докер-контейнер по образу