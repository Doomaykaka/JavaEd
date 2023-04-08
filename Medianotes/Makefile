PROJECT_NAME = Medianotes
PROJECT_VERSION = 0.1.0
JAR_PATH = build/libs

.PHONY: about help run build clean

help:
	@echo "make about: printing information about app"
	@echo "make help: printing information about available commands"
	@echo "make run: application launch"
	@echo "make build: building an application"
	@echo "make clean: deleting an existing application build"
about:
	@echo "Web application for working with notes"
run:
	java -jar $(JAR_PATH)/$(PROJECT_NAME)-$(PROJECT_VERSION).war
build:
	gradle build
clean:
	gradle clean
