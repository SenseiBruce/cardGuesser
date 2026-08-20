.PHONY: test build lint coverage assemble check

# Standard entry points so static scanners and fresh clones find a runnable suite.
test:
	./scripts/test.sh

build:
	./scripts/build.sh

assemble: build

lint:
	./gradlew :app:lintDebug ktlintCheck --no-daemon

coverage:
	./gradlew :app:testDebugUnitTest :app:jacocoTestReport :app:jacocoTestCoverageVerification --no-daemon

check: test lint coverage
