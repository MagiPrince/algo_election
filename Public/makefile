ifneq ($(filter run all,$(MAKECMDGOALS)),$())
ifndef port 
$(error port is not set)
endif
ifndef status
$(error status is not set)
endif
ifndef filename
$(error filename is not set)
endif
endif

.PHONY: info

info:
	@echo "Usage: make [build|clean|run port=XXXX status=INIT|WAIT filename=neighbours/neighbours-X.txt]"
	@echo "Examples:"
	@echo "make build - Builds the java classes"
	@echo "make run port=10000 status=INIT filename=neighbours/neighbours-0.txt - Run the javacode on port 10000 with the status INIT"
	@echo "make all port=10000 status=INIT filename=neighbours/neighbours-0.txt - Clean, build and run the javacode on port 10000 with the status INIT"

build:
	@javac -classpath . BFSTree.java

clean:
	@rm -f *.class

run:
	@java BFSTree ${port} ${status} ${filename}

all: clean build run