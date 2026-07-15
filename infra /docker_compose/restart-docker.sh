#!/bin/bash

echo ">>> Определяем архитектуру"
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    BROWSERS_FILE="./config/browsers.json"
else
    BROWSERS_FILE="./config/browsers-ci.json"
fi
echo "Architecture: $ARCH, using $BROWSERS_FILE"

echo ">>> Остановить Docker Compose"
docker compose down

echo ">>> Docker pull все образы браузеров"

# Путь до файла
json_file="./config/browsers.json"

# Проверяем, что jq установлен
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

# Извлекаем все значения .image через jq
images=$(jq -r '.. | objects | select(.image) | .image' "$BROWSERS_FILE")

# Пробегаем по каждому образу и выполняем docker pull
for image in $images; do
    echo "Pulling $image..."
    docker pull "$image"
done

echo ">>> Запуск Docker Compose"
docker compose up -d