# Grokking Algorithms — обёртка над Maven (JDK 24, см. pom.xml).
# Переопределение: make MVN=mvnw test

MVN ?= mvn

.PHONY: all compile test package clean verify help

.DEFAULT_GOAL := test

all: test

compile:
	$(MVN) -q compile

test:
	$(MVN) test

package:
	$(MVN) -q package -DskipTests

package-with-tests:
	$(MVN) package

clean:
	$(MVN) -q clean

verify:
	$(MVN) verify

help:
	@echo "Цели: compile, test (по умолчанию), package, package-with-tests, clean, verify, all"
	@echo "Переменная MVN: исполняемый файл Maven (по умолчанию: mvn)."
