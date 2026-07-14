#!/bin/bash

# Настройки
IMAGE_NAME="nbank-tests-iteration2"
DOCKERHUB_USERNAME="swoya"  # поменяй на свой логин Docker Hub
TAG="latest"

# Чтение токена из переменной окружения
if [ -z "$DOCKERHUB_TOKEN" ]; then
    echo "Ошибка: переменная окружения DOCKERHUB_TOKEN не установлена"
    echo "Установите: export DOCKERHUB_TOKEN=ваш_токен"
    exit 1
fi

echo ">>> Логин в Docker Hub..."
echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin

if [ $? -ne 0 ]; then
    echo "Ошибка: не удалось войти в Docker Hub"
    exit 1
fi

echo ">>> Тегирование образа..."
docker tag "$IMAGE_NAME:$TAG" "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

echo ">>> Пуш образа в Docker Hub..."
docker push "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

if [ $? -eq 0 ]; then
    echo ""
    echo ">>> Успешно! Образ загружен."
    echo ">>> Скачать образ можно командой:"
    echo "    docker pull $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"
else
    echo "Ошибка: не удалось загрузить образ"
    exit 1
fi

#переменные для имени образа, пользователя Docker Hub и тега;
 #чтение токена (лучше из переменной окружения DOCKERHUB_TOKEN, а не хранить в файле);
 #логин в Docker Hub через --password-stdin;
 #тегирование образа в формат <dockerhub-username>/<image-name>:<tag>;
 #пуш образа в Docker Hub;
 #финальное сообщение с тем, как скачать образ командой docker pull ....