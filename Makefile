run:	$(addprefix bin/, $(addsuffix .class, $(notdir $(basename $(wildcard src/*.java)))))
	java -cp bin Lancer

bin/%.class:	src/%.java
	mkdir -p bin
	javac -d bin src/*.java

clean:
	rm -fr bin
