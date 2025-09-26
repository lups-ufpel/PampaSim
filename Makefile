.PHONY: build run

build:
	mvn clean install

run: build
	cd sim && mvn javafx:run

