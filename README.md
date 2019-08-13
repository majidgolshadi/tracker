Tracker
=======
Driver position tracking to collect and store driver position in memory first and then persist them every `tracker.db.update-mysql-milliseconds-rate` second in mysql DB.

![tracker](./doc/pic/tracker_arch.png)

Configuration
-------------
```application.properties
spring.mysql.datasource.tracker.username=root
spring.mysql.datasource.tracker.password=root
spring.mysql.datasource.tracker.jdbc-url=jdbc:mysql://<TRACKER_DB_ADDRESS>:3306/<DB_NAME>

spring.mysql.datasource.bi.username=root
spring.mysql.datasource.bi.password=root
spring.mysql.datasource.bi.jdbc-url=jdbc:mysql://<BI_DB_ADDRESS>:3306/<DB_NAME>

spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.generate-ddl=true
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.mysql.MySQL56InnoDBSpatialDialect

tracker.mqtt.url=tcp://<URL_ADDRESS>
tracker.mqtt.username=<USER_NAME>
tracker.mqtt.password=<PASSWORD>
tracker.mqtt.connection-timeout=5

tracker.mqtt.location-topic=<MQTT_TOPIC>

tracker.cache.expire-time-milliseconds=10000

tracker.db.update-tracker-mysql-milliseconds-rate=1000
tracker.db.update-bi-mysql-milliseconds-rate=2000

```