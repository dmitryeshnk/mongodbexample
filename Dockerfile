FROM maven:latest AS build  
COPY src /usr/src/25_mongodb/src  
COPY pom.xml /usr/src/25_mongodb 
RUN mvn -f /usr/src/25_mongodb/pom.xml clean compile assembly:single

FROM mongo:latest
COPY src/database/orders.json  /tmp/orders.json
CMD mongoimport --host mongodb --db yeshenko --collection orders --type json --file /tmp/orders.json

FROM openjdk:latest
COPY --from=build /usr/src/25_mongodb/target/25_mongodb-jar-with-dependencies.jar 25_mongodb.jar
ENTRYPOINT ["java","-Dmongodb.uri=mongodb://mongo:27017" ,"-jar","/25_mongodb.jar", "com.nixsolutions.ppp.Main"]
