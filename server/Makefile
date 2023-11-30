app:
	python3 main.py

# delete all files in Flow and Cover_flows
clean:
	rm -rf Flow/*
	rm -rf Cover_flows/*
	rm -rf *.png
	rm -rf *.jpg
	rm -rf *.pyc
	rm -rf *.log
	rm -rf *.mp3

# build the docker image
build:
	docker build -t Shibaflow .

# run the docker image
run:
	docker run -it Shibaflow -p 8080:8080
