
all:
	./gradlew build

dev:
	./gradlew setupDecompWorkspace idea

clean:	
	rm -rf build .gradle .idea run out *.iml *.ipr *.iws

rel:
	docker build -t prospects .
	docker run -a stdin -a stdout -a stderr -e CF_API_TOKEN -w /prospects -it prospects make docker.rel

docker.rel:
	python3 release.py -project 290289 -name prospects -rel release
