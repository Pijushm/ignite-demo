FROM apacheignite/ignite:2.16.0

COPY mysql-connector-j-8.0.33.jar /opt/ignite/libs/
COPY server-pojo.jar /opt/ignite/libs/
COPY ignite-server-1.0.0.jar /opt/ignite/libs/
COPY ignite-config.xml /opt/ignite/config/