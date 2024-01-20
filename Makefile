VERSION ?= 2.3

build:
	docker build -t pagamento:${VERSION} .

tag:
	docker tag pagamento:${VERSION} pmoon1993/pagamento:${VERSION}

push:
	docker push pmoon1993/pagamento:${VERSION}

all: build tag push