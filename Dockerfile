FROM openjdk
WORKDIR /pagamento
COPY ./target/pagamento-0.0.1-SNAPSHOT.jar ./
ENTRYPOINT java -jar /pagamento/pagamento-0.0.1-SNAPSHOT.jar