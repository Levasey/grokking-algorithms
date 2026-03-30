# Grokking Algorithms (Java 24)

Практическая реализация примеров и задач из книги Адитьи Бхаргавы **"Грокаем алгоритмы"** на Java.

[![Java](https://img.shields.io/badge/Java-24-007396?logo=openjdk)](https://openjdk.java.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 📚 О проекте

Этот репозиторий содержит мои реализации алгоритмов, разобранных в книге, с использованием возможностей современного JDK (где уместно). Цель — глубже понять алгоритмы и получить практический опыт их реализации.

**Книга:** Адитья Бхаргава - "Грокаем алгоритмы" (перевод с английского "Grokking Algorithms").

**Целевая версия Java в проекте:** 24 (совпадает с JDK в системе разработки: `source`/`target` в Maven — 24).

## 🗂️ Структура проекта

Каждая папка соответствует главе или алгоритму из книги:

- `chapter01.binarySearch/` — Бинарный поиск.
- `chapter02.selectionSort/` — Сортировка выбором, Big O.
- `chapter03.recursion/` — Рекурсия, стек вызовов, факториал, числа Фибоначчи.
- `chapter04.quicksort/` — Быстрая сортировка, стратегия "разделяй и властвуй".
- `chapter05.hashTable/` — Хеш-таблицы, разрешение коллизий цепочками, пример регистрации «голосов».
- `chapter06.breadthFirstSearch/` — Поиск в ширину (BFS), графы.
- `chapter07.depthFirstSearch/` — Поиск в глубину, деревья.
- `chapter09.dijkstraAlgorithm/` — Алгоритм Дейкстры.
- `chapter10.greedyAlgorithms/` — Жадные алгоритмы (задача о покрытии множества).
- `chapter11.dynamicProgramming/` — Динамическое программирование (задача о рюкзаке, самая длинная общая подпоследовательность).
- `utilities/` — Общие структуры данных (Граф, Узел и т.д.).

## 🚀 Как запустить

### Предварительные требования

- Установленный **JDK 24** (как в `pom.xml`; на других машинах можно использовать ту же мажорную версию или выше при необходимости согласовать `maven.compiler.*`).
- [Apache Maven](https://maven.apache.org/) для сборки из командной строки (в IntelliJ обычно встроен).

### Сборка и запуск

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/yourusername/grokking-algorithms.git
   cd grokking-algorithms
   ```

2. Сборка и тесты:

   ```bash
   mvn test
   ```

В IDE можно запускать отдельные классы с `main` или JUnit-тесты без Maven, указав на classpath зависимости из `pom.xml`.
