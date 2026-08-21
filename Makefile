.PHONY: test build lint coverage assemble check setup

# Standard entry points so static scanners and fresh clones find a runnable suite.
# Primary: ./gradlew test  (also invoked by scripts/test.sh and CI)

setup:
	./scripts/setup.sh

test:
	./gradlew test --no-daemon

build:
	./gradlew build --no-daemon

assemble: build

lint:
	./gradlew :app:lintDebug ktlintCheck --no-daemon

coverage:
	./gradlew test :app:jacocoTestReport :app:jacocoTestCoverageVerification --no-daemon

check: test lint coverage
