JAVAC = javac
JAVA = java

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $*.java

default: clean

run: gatorTaxi.class
	$(JAVA) gatorTaxi

.PHONY: clean

clean:
	rm -rf *.class
	rm -rf gatorTaxi output*