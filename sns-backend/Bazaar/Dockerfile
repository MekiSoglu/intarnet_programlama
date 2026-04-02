FROM openjdk:17-jdk

WORKDIR /app

# PostgreSQL bağlantı ayarları
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.postgresql.Driver"
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://postgres-container:5432/snsDB"
ENV SPRING_DATASOURCE_USERNAME="postgres"
ENV SPRING_DATASOURCE_PASSWORD="postgres"
ENV SPRING_JPA_HIBERNATE_DDL_AUTO="update"
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT="org.hibernate.dialect.PostgreSQLDialect"
ENV SPRING_DATA_REST_BASE_PATH="/api"

# Uygulama jar dosyasını ekle
ADD target/devops-integration.jar devops-integration.jar

# Port tanımlaması
EXPOSE 80

# Uygulama başlatma komutu
CMD ["java", "-jar", "devops-integration.jar"]
