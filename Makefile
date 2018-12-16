
all:
	./gradlew build

setup:
	./gradlew setupDecompWorkspace idea

clean:	
	rm -rf build .gradle .idea run out *.iml *.ipr *.iws
