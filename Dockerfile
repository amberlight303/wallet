FROM openjdk:11
MAINTAINER Oleg Vostokov <amberlight303@gmail.com>

ADD ./target/wallet.jar /app/
CMD ["java", "-Xmx300m", "-jar", "/app/wallet.jar"]

EXPOSE 8585