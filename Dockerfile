FROM openjdk:8-jdk-alpine
ADD target/LikeRestServices.jar ws_LikeRestServices_sf.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","ws_LikeRestServices_sf.jar"]