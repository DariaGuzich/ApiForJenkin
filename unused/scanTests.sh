#!/bin/bash

TARGET_CLASS="${TARGET_CLASS_NAME}"
OUTPUT_FILE="test_methods.properties"

echo "# Scanning tests for class: ${TARGET_CLASS}" > ${OUTPUT_FILE}

cd src/test/java

# Находим все тестовые файлы для указанного класса
find . -name "*${TARGET_CLASS}.java" | while read file; do
    # Преобразуем путь файла в имя класса
    className=$(echo "$file" | sed 's|^\./||;s|/|.|g;s|\.java$||')

    echo "Processing: $className"

    # Извлекаем методы с аннотацией @Test
    methods=$(grep -B2 "@Test" "$file" | grep -E "public.*void.*\(" | sed 's/.*void[[:space:]]*\([a-zA-Z0-9_]*\).*/\1/' | tr '\n' ',')

    # Удаляем последнюю запятую
    methods=${methods%,}

    if [ ! -z "$methods" ]; then
        echo "${className}=${methods}" >> ${OUTPUT_FILE}
    fi
done

echo "Scan complete. Results:"
cat ${OUTPUT_FILE}