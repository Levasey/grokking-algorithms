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
- `chapter08.balancedTrees/` — Сбалансированные деревья (2-е изд.): AVL-дерево с поворотами и проверкой инвариантов.
- `chapter09.dijkstraAlgorithm/` — Алгоритм Дейкстры (неотрицательные веса).
- `chapter09.bellmanFord/` — Алгоритм Беллмана–Форда: кратчайшие пути из одной вершины при возможных отрицательных весах рёбер; детекция достижимого из источника отрицательного цикла (`calculate` возвращает `false`, если цикл есть). Рёбра задаются как исходящие из каждого узла; для **ориентированного** графа используйте `Node.addEdge` напрямую (класс `Graph` по умолчанию добавляет симметричные рёбра). Тесты: `BellmanFordTest`.
- `chapter10.greedyAlgorithms/` — Жадные алгоритмы (задача о покрытии множества).
- `chapter11.dynamicProgramming/` — Динамическое программирование (задача о рюкзаке, самая длинная общая подпоследовательность).
- `chapter12.kNearestNeighbors/` — k ближайших соседей (KNN): классификация по голосованию и регрессия средним по соседям, евклидово расстояние.
- **Глава 13** (финал книги, «куда двигаться дальше», плюс параллельные варианты типичных шагов):
  - `chapter13.linearRegression/` — линейная регрессия, минимизация MSE градиентным спуском: `train`, `trainParallel` (параллельно по объектам выборки, накопление градиента через `DoubleAdder`).
  - `chapter13.linearProgramming/` — [линейное программирование](https://en.wikipedia.org/wiki/Linear_programming) (оптимизация линейной цели при линейных ограничениях), **не путать с линейной регрессией**: симплекс-метод для максимума \(c^\top x\) при \(A x \le b\), \(x \ge 0\) (слаки добавляются внутри; требуется \(b_i \ge 0\) для всех строк — одна фаза, без искусственных переменных). Публичный API: `maximize`, `minimize` (через максимизацию \(-c\)), результат `Solution` со статусами `OPTIMAL` / `UNBOUNDED` / зарезервированным `INFEASIBLE`; утилиты `objectiveAt`, `isFeasible`. Тесты: `LinearProgrammingTest`.
  - `chapter13.invertedIndex/` — инвертированный индекс, поиск по терминам и комбинациям (И / ИЛИ): `add`, статические `buildParallel` для параллельной индексации списка документов.
  - `chapter13.localitySensitive/` — [локально-чувствительное хеширование](https://en.wikipedia.org/wiki/Locality-sensitive_hashing) (LSH) на базе MinHash для множеств признаков в виде `long` (например, хеши шинглов): `MinHash` строит подпись, `MinHashLshIndex` — индекс с полосами (`add`, `queryCandidates`, `candidateNeighbors`, оценка Жаккара `estimatedJaccard`). Тесты: `MinHashLshIndexTest`.
  - `chapter13.fourier/` — ДПФ для произвольной длины и БПФ Cooley–Tukey (основание 2) для длин — степень двойки; прямые и обратные преобразования; параллельные варианты `parallelDft`, `parallelInverseDft`, `parallelFft`, `parallelInverseFft` на общем `ForkJoinPool`.
  - `chapter13.parallel/` — учебные примитивы в духе map/reduce: `ParallelMapReduce.parallelSum`, `mapReduce`.
  - `chapter13.bloomFilter/` — [фильтр Блума](https://en.wikipedia.org/wiki/Bloom_filter): вероятностное множество без ложноотрицательных ответов; `add`, `mightContain`; размер битовой карты и число хеш-функций подбираются по ожидаемому числу вставок и целевой вероятности ложноположительного срабатывания (есть и явный конструктор с фиксированными `bitSize` и `hashFunctionCount`).
  - `chapter13.hyperLogLog/` — [HyperLogLog](https://en.wikipedia.org/wiki/HyperLogLog): оценка кардинальности (числа различных элементов) с фиксированной памятью `2^precision` регистров; `add`, `estimateCardinality`; параметр `precision` в диапазоне [4, 18]; для малых истинных кардинальностей используется поправка linear counting по числу нулевых регистров. Тесты: `HyperLogLogTest`.
  - `chapter13.httpsDh/` — [протокол Диффи–Хеллмана](https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange) и связь с HTTPS/TLS на учебном уровне: класс `DiffieHellman` (`Parameters` с модулем и образующим, сторона `Party`, общий секрет `sharedSecret`); для примеров — `textbookDemoParameters()` (малые `p` и `g`, не для реальных систем). `HttpsKeyAgreement` имитирует DHE-рукопожатие (эфемерные открытые ключи, одинаковый pre-master у клиента и сервера) и демонстративно выводит два 32-байтовых направленных ключа через SHA-256; это не полный PRF/HKDF TLS 1.2. Для трассировки шагов есть перегрузка `performDheHandshake(random, StringBuilder)`. Тесты: `DiffieHellmanTest`.
  - `chapter13.distributed/` — распределённые идеи в учебной форме: `LamportClock`, `VectorClock`, симуляция выборов лидера `BullyLeaderElection`, симуляция MapReduce `DistributedWordCount`.
  - `chapter13.heap/` — бинарная мин-куча на массиве (`MinHeap`: `offer`, `peek`, `poll`, сборка из коллекции за O(n)) и приоритетная очередь поверх неё (`PriorityQueue`). Тесты: `MinHeapAndPriorityQueueTest`.
- `utilities/` — Общие структуры данных (Граф, Узел, Рёбра и т.д.).

## 🚀 Как запустить

### Предварительные требования

- Установленный **JDK 24** (как в `pom.xml`; на других машинах можно использовать ту же мажорную версию или выше при необходимости согласовать `maven.compiler.*`).
- [Apache Maven](https://maven.apache.org/) для сборки из командной строки (в IntelliJ обычно встроен).

### Сборка и запуск

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/Levasey/grokking-algorithms.git
   cd grokking-algorithms
   ```

2. Сборка и тесты:

   ```bash
   mvn test
   ```

   Если в терминале команда `mvn` не находится, используйте Maven из IDE (IntelliJ / VS Code) или укажите полный путь к исполняемому файлу `mvn`.

В IDE можно запускать отдельные классы с `main` или JUnit-тесты, подключив на classpath зависимости из `pom.xml` (JUnit 5).

### Тесты

В `src/test/java/` лежат JUnit 5-тесты для модулей выше (включая `DiffieHellmanTest` для DH и упрощённого согласования ключей, `MinHashLshIndexTest` для LSH, `LinearProgrammingTest` для симплекса). Полный прогон `mvn test` проверяет согласованность последовательных и параллельных реализаций там, где обе есть (глава 13).
