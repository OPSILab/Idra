# Idra - Open Data Federation Platform

Idra is a web application able to federate existing Open Data Management Systems
(ODMS) based on different technologies providing a unique access point to search
and discover open datasets coming from heterogeneous sources. Idra uniforms
representation of collected open datasets, thanks to the adoption of
international standards (DCAT-AP) and provides a set of RESTful APIs to be used
by third party applications.

Idra supports natively ODMS based on [CKAN](https://ckan.org/),
[DKAN](https://getdkan.org/), [Socrata](https://socrata.com/), Orion Context
Broker
([NGSI v2](https://swagger.lab.fiware.org/?url=https://raw.githubusercontent.com/Fiware/specifications/master/OpenAPI/ngsiv2/ngsiv2-openapi.json), [NGSI-LD](https://www.etsi.org/deliver/etsi_gs/CIM/001_099/009/01.01.01_60/gs_CIM009v010101p.pdf))
and many other technologies: Idra provides also a set of APIs to federate ODMS
not natively supported. In addition, it is possible to federate generic open
data portals, that don't expose API, using the web scraping functionality or
providing a dump file of the datasets in
[DCAT-AP](https://joinup.ec.europa.eu/solution/dcat-application-profile-data-portals-europe)
format. Furthermore, Idra provides a
[SPARQL](https://www.w3.org/TR/sparql11-query/) endpoint in order to perform
queries on 5 stars RDF
[linked open data](https://dvcs.w3.org/hg/gld/raw-file/default/glossary/index.html)
collected from federated ODMS and allows to easily create charts based on
federated open datasets (through DatalEt-Ecosystem Provider
[DEEP](https://github.com/routetopa/deep2-components) )

Idra is an open source software developed by
[Engineering Ingegneria Informatica SpA](http://www.eng.it) inside the EU
founded project [FESTIVAL](http://www.festival-project.eu/). This project is
part of [FIWARE](https://www.fiware.org/). For more information check the FIWARE
Catalogue entry for
[Data Publication](https://github.com/Fiware/catalogue/tree/master/data-publication). The roadmap of this FIWARE GE is described [here](./roadmap.md)

## How to use this image

Both DockerFile and docker-compose files are provided, in order to use both methods. However, the recommended way is to use 
Docker Compose, which allows you to start an Idra container, based on the official 
[Docker Hub image](https://hub.docker.com/r/idraopendata/idra/).

Docker Compose allows to run and link an Idra container (running on a Tomcat Alpine container) to MySQL and RDF4J containers 
very easily. The `docker-compose` file can be found below.

```yml
version: '3'
services :
    db:
            image: mysql:5.7
            restart: always
            ports:
                - 3306:3306
            volumes:
                - ./opt/idra/mysql-data:/var/lib/mysql
                - ../idra_db.sql:/docker-entrypoint-initdb.d/idra_db.sql
            networks:
                idra_main:
            environment:
                - MYSQL_ROOT_PASSWORD_FILE=/run/secrets/my_secret_data
                - MYSQL_DATABASE=idra_db
            secrets:
                - my_secret_data 

            command: --default-authentication-plugin=mysql_native_password --lower_case_table_names=1
    rdf4j:
            image: yyz1989/rdf4j
            ports:
                - 8081:8080
            volumes:
                - ./opt/idra/rdf4j-data:/opt/rdf4j/data
            environment:
                - RDF4J_DATA=/opt/rdf4j/data
            networks:
                idra_main:
    deep:
            image: idraopendata/deep:2.0.0
            ports:
                - 80:80
            volumes:
                - ./deep/configuration.js:/var/www/app/deep/deep-components/configuration.js
            networks:
                idra_main:
    idra:
            image: idraopendata/idra:latest
            ports:
                - 8080:8080
            depends_on:
                - db
                # - rdf4j
            command: ["./wait-for-it.sh", "db:3306", "--timeout=0", "--", "catalina.sh", "run"]

            environment:
                - idra.db.host=jdbc:mysql://db:3306/idra_db?serverTimezone=UTC&useLegacyDatetimeCode=false&useEncoding=true&characterEncoding=UTF-8&useSSL\=false
                - idra.db.host.min=jdbc:mysql://db:3306
                - idra.db.user_FILE=/run/secrets/my_secret_data
                - idra.db.password_FILE=/run/secrets/my_secret_data
                - idra.db.name=idra_db
                - org.quartz.dataSource.myDS.URL=jdbc:mysql://db:3306/idra_db?serverTimezone=UTC&useLegacyDatetimeCode=false&useEncoding=true&characterEncoding=UTF-8&useSSL=false
                - idra.cache.loadfromdb=true
                - idra.synch.onstart=false
                - idra.odms.dump.file.path=/opt/idra/dump/
                - idra.dump.file.path=/opt/idra/dump/
                - idra.lod.enable=true
                - idra.lod.repo.name=Idra
                - idra.lod.server.uri=http://rdf4j:8081/rdf4j-server/repositories/
                - idra.lod.server.uri.query=http://rdf4j:8081/rdf4j-workbench/repositories/Idra/query
                - idra.orion.orionDumpFilePath=/opt/idra/dump/
                - idra.orion.orionInternalAPI=http://localhost:8080/Idra/api/v1/client/executeOrionQuery
                - idra.authentication.method=BASIC
                - idm.fiware.version=7
                - idm.protocol=https
                - idm.host=
                - idm.path.base=
                - idm.client.id=
                - idm.client.secret=
                - idm.redirecturi=http://IDRA_HOST/Idra/api/v1/administration/login
                - idm.logout.callback=http://IDRA_HOST/IdraPortal
                - idm.admin.role.name=Admin
                - DATALET_ENABLED=true
                - DATALET_URL=http://localhost:80/deep/deep-components/demo.html
                - JAVA_OPTS=-Xms1024m -Xmx3g -XX:PermSize=1024m -XX:MaxPermSize=2048m
            secrets:
                - my_secret_data           
            volumes:
                - ./opt/idra/dump:/opt/idra/dump
                - /dev/urandom:/dev/random
            networks:
                idra_main:

networks:
    idra_main:
          driver: bridge
          
secrets:
  my_secret_data:
    file: ./secrets.txt
```

## Configuration with environment variables

Many settings can be configured using Docker environment variables. A typical Idra Docker container is driven by
environment variables such as those shown below:

-   `idra.db.host` - Hostname of the MySQL database to store all the application data and collected Open Datasets
-   `idra.db.host.min` - Hostname where the MySQL database resides
-   `idra.db.user` - Username of the MySQL server installation
-   `idra.db.password` - Password of the MySQL server installation
-   `idra.db.name` - Name of the reference database for Idra
-   `org.quartz.dataSource.myDS.URL` - URL of the actual MySQL server installation
-   `idra.cache.loadfromdb` - This is used to restore SOLR cache from the database
-   `idra.synch.onstart` - This forces the synchronization of the catalogues on server start up 
-   `idra.odms.dump.file.path` - Folder path where to save the DCAT-AP dump files (The path MUST end with \ or /)
-   `idra.dump.file.path` - Folder path where to save the DCAT-AP dump files (The path MUST end with \ or /)
-   `idra.lod.enable` - If set to true, it allows to enable RDF (Linked Open Data) retrieval
-   `idra.lod.repo.name` - Name of the new RDF repository of type Native Java Store to create in RDF4J
-   `idra.lod.server.uri` - URL where to find the "repositories" endpoint of RDF4J
-   `idra.lod.server.uri.query` - URL where to find the "query" endpoint of RDF4J
-   `idra.orion.orionDumpFilePath` - The path where Orion dumps are stored
-   `idra.orion.orionInternalAPI` - The internal endpoint used to wrap Orion's queries
-   `idra.authentication.method` - Allowed values are BASIC and FIWARE to select which Authorization mechanism to use 
-   `idm.fiware.version` - The version of the Fiware IdM, namely Keyrock. Allowed values are 6 and 7
-   `idm.protocol` - Protocol of Fiware IdM instance (http or https)
-   `idm.host` - Host of Fiware IdM instance (include also the port, if any)
-   `idm.path.base` - Base URL of the Idra Platform registered as an Application in the Fiware IdM
-   `idm.client.id` -  Client Id provided by the Fiware IdM
-   `idm.client.secret` - Client Secret provided by the Fiware IdM
-   `idm.redirecturi` - Callback login URL of the Idra Platform registered as an Application in the Fiware IdM
-   `idm.logout.callback` - URL of the Idra Platform registered as an Application in the Fiware IdM
-   `idm.admin.role.name` - Role name that User must have in the IDM to be authenticated as Idra Administrator (default: admin)
-   `DATALET_ENABLED` - If set to true, it allows to enable the possibility to create and manage the datalets 
-   `DATALET_URL` - URL where to find the instance of the deep component to create and manage the datalets 
-   `JAVA_OPTS` - Memory flags options for the Java Virtual Machine (JVM)

### Further Information

Additional information and instructions on how to install and launch the Idra platform using Docker can be found in 
the "Install with Docker" section of the [Installation Guide](https://idra.readthedocs.io/en/latest/admin/install_docker/).

## How to build an image

The [Dockerfile](https://github.com/OPSILab/Idra/blob/master/docker/Dockerfile) associated with this image can be used 
to build an image to run only a container running Idra platform. It is up to you to provide MySQL and RDF4J instances. 
These may be running on localhost, other host on your network, another container, or anywhere you have network access to. 
Link them accordingly, see the Docker [link](https://docs.docker.com/network/links/) 
and [bridge networks](https://docs.docker.com/network/bridge/) guides.

The Idra Docker image can be build in several ways:

-   Pulled from official [Docker Hub](https://hub.docker.com/r/idraopendata/idra/):

```console
docker pull idraopendata/idra:<VERSION>
```

-   Manually built the **latest** release with (go to /Idra/docker folder):

```console
docker build -t idraopendata/idra .
```

-   You can also download a specific release by running this `Dockerfile` with the build argument `DOWNLOAD=<version>`:

```console
docker build -t idraopendata/idra --build-arg DOWNLOAD=v2.0.2 .
```

Once the Idra image was pulled or created, launch a container based on it:

```console
docker run idraopendata/idra
```

## Building from your own fork

To download code from your own fork of the GitHub repository add the `GITHUB_ACCOUNT`, `GITHUB_REPOSITORY` and
`DOWNLOAD` arguments (default `latest`) to the `docker build` command.

```console
docker build -t idraopendata/idra \
    --build-arg GITHUB_ACCOUNT=<your account> \
    --build-arg GITHUB_REPOSITORY=<your repo> \
	--build-arg DOWNLOAD=<version> . \
```

### Docker Secrets

As an alternative to passing sensitive information via environment variables, `_FILE` may be appended to some sensitive
environment variables, causing the initialization script to load the values for those variables from files present in
the container. In particular, this can be used to load passwords from Docker secrets stored in
`/run/secrets/<secret_name>` files. 

Currently, this `_FILE` suffix is supported for:

-   `MYSQL_ROOT_PASSWORD`
-   `idra.db.user`
-   `idra.db.password`
