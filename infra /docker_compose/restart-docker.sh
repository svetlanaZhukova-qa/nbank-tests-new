#!/bin/bash

echo ">>> Определяем архитектуру"
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    echo "Architecture: arm64, using ./config/browsers.json"
    # Локально — оставляем как есть
else
    echo "Architecture: $ARCH, using ./config/browsers-ci.json"
    # В CI — копируем CI-версию в browsers.json, чтобы Selenoid увидел
    cp ./config/browsers-ci.json ./config/browsers.json
fi

echo ">>> Остановить Docker Compose"
docker compose down

echo ">>> Docker pull все образы браузеров"

# Проверяем, что jq установлен
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

# Извлекаем все значения .image через jq (теперь из browsers.json, который актуальный)
images=$(jq -r '.. | objects | select(.image) | .image' "./config/browsers.json")

# Пробегаем по каждому образу и выполняем docker pull
for image in $images; do
    echo "Pulling $image..."
    docker pull "$image"
done

echo ">>> Запуск Docker Compose"
docker compose up -d