FROM eclipse-temurin:17
RUN apt-get update && apt-get install -y dumb-init && rm -r /var/lib/apt/*
ADD build/libs/sapcc-toolkit*.jar /app.jar
RUN useradd -m sapcc-toolkit
USER sapcc-toolkit
ENTRYPOINT ["/usr/bin/dumb-init", "--", "java", "-jar", "app.jar"]
