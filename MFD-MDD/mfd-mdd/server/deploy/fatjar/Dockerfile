FROM tomcat:8.5-jdk8-openjdk
ADD target/mdd-server-deploy-fat-jar-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
COPY context.xml /usr/local/tomcat/conf/context.xml
EXPOSE 8080
CMD ["catalina.sh", "run"]