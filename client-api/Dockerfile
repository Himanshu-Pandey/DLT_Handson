FROM openjdk:11-jdk-slim
WORKDIR /app

COPY ./ /app/

RUN MAVEN_OPTS=-Djansi.force=true ./mvnw -Dmaven.repo.local=/app/.mvn/repository compile \
    jar:jar spring-boot:repackage --batch-mode -Dstyle.color=always --activate-profiles dockerfile-build,no-coverage

CMD java -jar quorum-client-api.jar

