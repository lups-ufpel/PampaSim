.PHONY: build run

JAVA_MIN := 21
MAVEN_MIN := 3.9.9

build:
	# Check Java version
	@JAVA_VER=$$(java -version 2>&1 | awk -F '"' '/version/ {print $$2}'); \
	JAVA_MAJOR=$$(echo $$JAVA_VER | cut -d. -f1); \
	if [ $$JAVA_MAJOR -lt $(JAVA_MIN) ]; then \
		echo "Java $$JAVA_VER found, but Java $(JAVA_MIN)+ is required"; \
		exit 1; \
	fi; \
	echo "Java $$JAVA_VER OK"
	# Check Maven version
	@MAVEN_VER=$$(mvn -v | awk '/Apache Maven/ {print $$3}'); \
	if [ "$$(printf '%s\n' $(MAVEN_MIN) $$MAVEN_VER | sort -V | head -n1)" != "$(MAVEN_MIN)" ]; then \
		echo "Maven $$MAVEN_VER found, but Maven $(MAVEN_MIN)+ is required"; \
		exit 1; \
	fi; \
	echo "Maven $$MAVEN_VER OK"

	# Build project
	mvn clean install

run: build
	cd sim && mvn javafx:run

