# Настройка мониторинга тестов в Grafana

## Обзор

Настроена интеграция для отображения результатов тестов в Grafana с использованием InfluxDB как источника данных.

## Компоненты

1. **InfluxDB** - база данных для хранения метрик тестов
2. **Grafana** - визуализация данных и дашборды
3. **Java утилита** - отправка результатов тестов в InfluxDB
4. **Jenkins pipeline** - автоматическая отправка метрик

## Запуск

### 1. Запуск инфраструктуры

```bash
docker-compose up -d
```

Это запустит:
- Jenkins (порт 8080)
- InfluxDB (порт 8086)
- Grafana (порт 3000)
- MySQL (порт 3306)

### 2. Доступ к сервисам

- **Jenkins**: http://localhost:8080
- **Grafana**: http://localhost:3000 (admin/admin)
- **InfluxDB**: http://localhost:8086

### 3. Настройка в Jenkins

1. Создайте pipeline job в Jenkins
2. Укажите репозиторий с Jenkinsfile
3. Pipeline автоматически будет отправлять метрики после выполнения тестов

### 4. Просмотр дашборда

1. Откройте Grafana: http://localhost:3000
2. Войдите с учетными данными admin/admin
3. Источник данных InfluxDB уже настроен автоматически
4. Дашборд "Test Results Dashboard" будет доступен в разделе Dashboards

## Метрики

### Test Suite метрики:
- `total_tests` - общее количество тестов
- `passed_tests` - количество успешных тестов
- `failed_tests` - количество провалившихся тестов
- `error_tests` - количество тестов с ошибками
- `skipped_tests` - количество пропущенных тестов
- `execution_time` - время выполнения
- `success_rate` - процент успешности

### Test Case метрики:
- Детализированная информация по каждому тесту
- Статус выполнения
- Время выполнения
- Информация об ошибках

## Теги для фильтрации:
- `suite_name` - имя тест-сьюта
- `job_name` - имя Jenkins job
- `build_number` - номер сборки
- `test_name` - имя отдельного теста
- `class_name` - имя класса теста
- `status` - статус теста (PASSED/FAILED/ERROR/SKIPPED)

## Отправка метрик при локальном запуске

### Вариант 1: Тесты без метрик (по умолчанию)
```bash
mvn test
```

### Вариант 2: Тесты с автоматической отправкой метрик
```bash
mvn test -Pwith-metrics
```

### Вариант 3: Ручная отправка метрик
```bash
# Запустите тесты
mvn test

# Отправьте метрики
./send-metrics.sh target/surefire-reports
```

## Настройки InfluxDB

По умолчанию используются:
- Organization: test-org
- Bucket: test-results
- Token: my-super-secret-auth-token

Для изменения настроек отредактируйте docker-compose.yml и соответствующие переменные окружения.