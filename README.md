Tracker
=======
Driver position tracking to collect and store driver position in memory first and then persist them every `tracker.db.update-mysql-milliseconds-rate` second in mysql DB.

![tracker](./doc/pic/tracker_arch.png)

Configuration
-------------
```application.properties
server.port=8080

spring.datasource.username=root
spring.datasource.password=root
spring.datasource.url=jdbc:mysql://172.100.0.2:3306/carpino

spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.hibernate.ddl-auto=none
spring.jpa.generate-ddl=true
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.mysql.MySQL56InnoDBSpatialDialect

tracker.proxy.service-url=172.20.2.59
tracker.proxy.service-port=7080

tracker.mqtt.url=tcp://loc.carpino.info
tracker.mqtt.username=DRIVER
tracker.mqtt.password=DRIVER
tracker.mqtt.connection-timeout=5

tracker.mqtt.location-topic=loc/drv/#
tracker.mqtt.create-ride-log-topic=log/tracker/create-ride
tracker.mqtt.near-driver-log-topic=log/tracker/nearby-drivers
tracker.mqtt.data-duplication-threshold=5
tracker.mqtt.connection-validation-milliseconds-rate=3000

tracker.cache.expire-time-milliseconds=15000

tracker.db.update-tracker-mysql.active=true
tracker.db.update-tracker-mysql-milliseconds-rate=50000

tracker.driver.car-category-type=NORMAL,VIP,WOMEN,VAN,AFFORDABLE
```

API
---
|Type|Uri|Description|
|---|---|---|
|GET|`/v1/driver/near`|get driver ids that they near lat & lon|
|GET|`/v1/driver/location`|get all driver information if no driverId specified|
|POST|`/v1/master`|set master node|
|ANY|`/proxy/*`|reverse proxy endpoint|

request sample
```bash
# Get all near online driver ids
# distance unit is kilometer
# category return all type if this parameter is not set
 
$ curl -XGET "localhost:8080/v1/driver/near?lat=35.7018057&lon=51.4254936&distance=0.2&category=VIP" 
```
response sample is like:
```json
["5bcec53924aa9a000149546e","5b6bd112a7b11b000186df23"]
```
if the category type is not define in configuration file the API `return 400` status code 

---

request sample
```bash
# Get single driver lat and lon
 
$ curl -XGET "localhost:8080/v1/driver/location?driverId=5bcec53924aa9a000149546e" 
```
response sample is like:
```json
[{"id":"5bcec53924aa9a000149546e","lat":35.7055252,"lon":51.3799262}]
```

---

request sample
```bash
# Get all online drivers location
 
$ curl -XGET localhost:8080/v1/driver/location
```
response sample is like:
```json
[
  {"id":"5ced14b52ab79c000152eaa9","lat":35.7081011,"lon":51.3953885},
  {"id":"5b8a40e324aa9a0001335815","lat":35.7573133,"lon":51.4094223}
]
```