FROM openjdk:11-jdk-slim
WORKDIR /app
COPY ./.mvn /app/.mvn
COPY ./mvnw ./pom.xml  /app/

RUN MAVEN_OPTS=-Djansi.force=true ./mvnw -Dmaven.repo.local=/app/.mvn/repository dependency:go-offline --batch-mode

COPY ./src /app/src

RUN MAVEN_OPTS=-Djansi.force=true ./mvnw -Dmaven.repo.local=/app/.mvn/repository compile \
    jar:jar spring-boot:repackage --batch-mode -Dstyle.color=always --activate-profiles dockerfile-build,no-coverage

CMD sleep 10 && java -jar /app/target/quorum-client-api.jar

