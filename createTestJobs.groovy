// --- Читаем параметры ---
def targetClassName = binding.variables.TARGET_CLASS_NAME
def testMethodsData = binding.variables.TEST_METHODS_DATA

println "[dsl] 🎯 Целевой класс: ${targetClassName}"
println "[dsl] 📝 Полученные данные о методах:"
println testMethodsData

// --- Парсим данные о тестовых методах ---
def classes = [:]

testMethodsData.split('\n').each { line ->
    line = line.trim()

    // Пропускаем комментарии и пустые строки
    if (line.startsWith('#') || line.isEmpty()) {
        return
    }

    // Парсим строку формата: className=method1,method2,method3
    def parts = line.split('=')
    if (parts.length == 2) {
        def className = parts[0].trim()
        def methods = parts[1].split(',').collect { it.trim() }

        // Проверяем, что класс соответствует целевому
        if (className.endsWith(targetClassName)) {
            classes[className] = methods
            println "[dsl] ✅ Найден класс: ${className} с методами: ${methods}"
        }
    }
}

// --- Проверяем, найдены ли тесты ---
if (classes.isEmpty()) {
    println "[dsl] ⚠️ Тестовые методы для класса ${targetClassName} не найдены — джобы не будут созданы."
    return
}

// --- Создаем джобы для каждого найденного класса ---
classes.each { className, methods ->
    def simpleClassName = className.tokenize('.').last()

    println "[dsl] 📁 Создаём структуру для класса: ${simpleClassName}"

    // --- Создаём папку для класса ---
    folder("TestJobs/${simpleClassName}") {
        description("Тестовые джобы для класса ${className}")
    }

    // --- Создаём MultiJob для запуска всех тестов класса ---
    multiJob("TestJobs/${simpleClassName}/${simpleClassName}_AllTests") {
        description("MultiJob для запуска всех тестов класса ${className}")

        // Указываем, что MultiJob тоже должен запускаться на ui агенте
        label('ui')

        // Настройка SCM для MultiJob
        scm {
            git {
                remote {
                    url('https://github.com/DariaGuzich/ApiForJenkin.git')
                }
                branch('master')
            }
        }

        // Создаем фазу с джобами для всех методов
        steps {
            phase("Run all ${simpleClassName} tests") {
                continuationCondition('SUCCESSFUL')
                methods.each { method ->
                    phaseJob("TestJobs/${simpleClassName}/${simpleClassName}_${method}") {
                        currentJobParameters()
                    }
                }
            }
        }

        // Публикаторы для MultiJob
        publishers {
            archiveJunit('**/target/surefire-reports/*.xml') {
                allowEmptyResults()
            }
        }
    }

    // --- Создаём отдельные джобы для каждого метода ---
    methods.each { method ->
        def jobName = "${simpleClassName}_${method}"

        println "[dsl]   📝 Создаём джобу: ${jobName}"

        job("TestJobs/${simpleClassName}/${jobName}") {
            description("Джоба для запуска теста ${className}#${method}")

            // ВАЖНО: Указываем, что джоба должна запускаться на ui агенте
            label('ui')

            // Настройка SCM
            scm {
                git {
                    remote {
                        url('https://github.com/DariaGuzich/ApiForJenkin.git')
                    }
                    branch('master')
                }
            }

            // Шаги сборки
            steps {
                // Shell скрипт для очистки директории скриншотов
                shell('''
                    echo "=== Cleaning screenshots ==="
                    rm -rf screenshots/*.png target/screenshots/*.png
                    mkdir -p screenshots target/screenshots
                    echo "Old screenshots cleared"
                    
                    echo "=== Current workspace ==="
                    pwd
                    ls -la
                ''')

                // Maven команда для запуска конкретного теста
                maven {
                    goals('clean test')
                    mavenOpts("-Dtest=${className}#${method}")
                    // Если у вас настроен Maven в Jenkins, укажите его имя:
                    // mavenInstallation('Maven-3.8.6')
                }
            }

            // Публикаторы результатов
            publishers {
                // Архивирование JUnit результатов
                archiveJunit('**/target/surefire-reports/*.xml') {
                    allowEmptyResults()
                    testDataPublishers {
                        allowClaimingOfFailedTests()
                        publishTestStabilityData()
                    }
                }

                // Архивирование скриншотов
                archiveArtifacts {
                    pattern('screenshots/*.png')
                    pattern('target/screenshots/*.png')
                    allowEmpty(true)
                    fingerprint(false)
                    onlyIfSuccessful(false)
                }

                // Опционально: HTML отчеты если используете
                // publishHtml {
                //     report('target/site/surefire-report.html') {
                //         reportName('Test Results')
                //         allowMissing(true)
                //     }
                // }
            }
        }
    }

    println "[dsl] ✅ Создано: папка ${simpleClassName}, MultiJob и ${methods.size()} джоб(а) для класса ${className}"
}

println "[dsl] 🎉 Всё готово! Обработано классов: ${classes.size()}"