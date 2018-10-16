# Idra - Open Data Federation Platform

Idra is a web application able to federate existing Open Data Management Systems (ODMS) based on different technologies providing a unique access point to search and discover open datasets coming from heterogeneous sources. Idra uniforms representation of collected open datasets, thanks to the adoption of international standards (DCAT-AP) and provides a set of RESTful APIs to be used by third party applications. 
 
Idra supports natively ODMS based on CKAN, DKAN, Socrata, Orion Context Broker and many other technologies: Idra provides also a set of APIs to federate ODMS not natively supported. In addition, it is possible to federate generic open data portals, that don't expose API, using the web scraping functionality or providing a dump file of the datasets in DCAT-AP format. Moreover Idra provides a SPARQL endpoint in order to perform queries on 5 stars RDF linked open data collected from federated ODMS and allows to easily create charts based on federated open datasets (through [DatalEt-Ecosystem Provider (DEEP)](https://github.com/routetopa/deep-components))
 
Idra is an open source software developed by [Engineering Ingegneria Informatica SpA](http://www.eng.it) inside the EU founded project [FESTIVAL](http://www.festival-project.eu/)


## Requirements

Idra has the following requirements that must be correctly installed and
configured

| Framework                                                                        |Version          |Licence                                                               |
|----------------------------------------------------------------------------------|-----------------|----------------------------------------------------------------------|
|[Java SE Development Kit](http://docs.oracle.com/javase/8/docs/technotes/guides/install/install\_overview.html) |8.0 |Oracle Binary Code License         |
|[Apache Tomcat](https://tomcat.apache.org/tomcat-8.0-doc/setup.html) |8.0 |Apache License v.2.0 |
|[MySQL](https://dev.mysql.com/doc/refman/5.7/en/)|5.7.5 Community  |GNU General Public License Version 2.0 |
|[RDF4J Server](http://rdf4j.org/download/)|2.2.1 |[EDL 1.0 (Eclipse Distribution License) ](https://eclipse.org/org/documents/edl-v10.php) |                                                                      
|[RDF4J Workbench](http://rdf4j.org/download/)) |2.2.1 |[EDL 1.0 (Eclipse Distribution License) ](https://eclipse.org/org/documents/edl-v10.php) |

## Libraries

Idra is based on the following software libraries and frameworks.

|Framework                           |Version       |Licence                                      |
|------------------------------------|--------------|---------------------------------------------|
|[Antlr](http://www.antlr.org) | 2.7.7 | BSD License|
|[Apache Commons](https://commons.apache.org/) Subpackages| 2.x & 3.x| Apache License 2.0|
|[Apache Http Client](https://hc.apache.org/httpcomponents-client-ga/index.html) |4.5.2 |Apache License 2.0 |
|[Apache Http Core](https://hc.apache.org/httpcomponents-core-ga/index.html) |4.5.2 |Apache License 2.0 |
|[Apache Jena ARQ](https://jena.apache.org/documentation/query/)| 3.30| Apache License 2.0|
|[Apache Log4j](http://logging.apache.org/log4j/2.x/)| 2.7| Apache License 2.0|
|[Apache SOLR-Lucene (SOLR Core)](http://lucene.apache.org/solr) |6.6.0 |Apache License 2.0|
|[Bytecode OpenCSV](https://github.com/EmergentOrder/opencsv)|2.4| Apache License 2.0|
|[CKANClient-J](https://github.com/okfn/CKANClient-J) |1.7 |AGPL 3.0 (GNU Affero General Public License) |
|[Google Gson](https://github.com/google/gson)| 2.8.0| Apache License 2.0|
|[Google Guava](https://github.com/google/guava)| 20.0| Apache License 2.0|
|[Hibernate](http://hibernate.org/) |5.2.10.Final |LGPL 2.1 (GNU Lesser General Public License) |
|[Hikari](https://github.com/brettwooldridge/HikariCP) |2.6.1 |Apache License 2.0 |
|[Jackson](https://github.com/codehaus/jackson)| 1.9.13| Apache License 2.0|
|[Jersey](https://jersey.github.io/)|2.23.2|GPL 2.0 (GNU General Public License Version)|
|[Joda-Time](http://www.joda.org/joda-time/)|2.9.5|Apache License 2.0|
|[Jsoup](https://jsoup.org)|1.10.1|MIT License|
|[JTS Topology Suite](https://sourceforge.net/projects/jts-topo-suite/)|1.13|LGPL 2.0 (GNU Lesser General Public License)|
|[Mysql connector (Community Release)](https://www.mysql.it/products/connector/) |5.1.39 |GPL 2.0 (GNU General Public License Version) |
|[Quartz Enterprise Job Scheduler](http://www.quartz-scheduler.org/) | 2.3.0|Apache License 2.0|
|[RDF4J-Runtime](http://rdf4j.org/download/) | 2.2.1 | [EDL 1.0 (Eclipse Distribution License) ] 
|[Ace Editor](https://ace.c9.io) |1.2.0 |BSD License |
|[AngularJS](https://angularjs.org/) |1.5.9 |MIT License |
|[Angular-d3-word-cloud](https://github.com/weihanchen/angular-d3-word-cloud)|0.2.0|MIT License|
|[Angular Dialog Service](https://github.com/m-e-conroy/angular-dialog-service)| 5.2.8| MIT License|
|[Angular MD5](https://github.com/gdi2290/angular-md5)| 0.1.8| MIT License|  
|[Angular UI - Bootstrap](https://angular-ui.github.io/bootstrap/) |0.13.3 |MIT License|
|[Angular UI - ACE](https://github.com/angular-ui/ui-ace) |0.2.3 |MIT License |
|[Angular Utils Pagination](https://github.com/michaelbromley/angularUtils/tree/master/src/directives/pagination) |0.11.0 |MIT License |
|[Angular ZeroClipboard](https://github.com/lisposter/angular-zeroclipboard) |0.8.0|MIT License|
|[Angular-xeditable](https://vitalets.github.io/angular-xeditable/) |0.1.8 |MIT License|
|[Bootstrap](http://getbootstrap.com/) |3.3.2 |MIT License |
|[Bootstrap-Material](http://fezvrasta.github.io/bootstrap-material-design/) |3 |MIT License |
|[Flag Icon CSS](https://github.com/lipis/flag-icon-css)|1.x|MIT License|
|[JQuery](https://jquery.com/)|1.10.2|MIT License|
|[ngCountrySelect](https://github.com/navinpeiris/ng-country-select)| 0.1.4| MIT License|
|[ngImageCrop](https://github.com/alexk111/ngImgCrop) |0.3.2 |MIT License |
|[ngTagsInput](http://mbenford.github.io/ngTagsInput/)|3.0.x| MIT License|
|[spin.js](https://spin.js.org/) |2.3.2 |MIT License |
[Smart Table](http://lorenzofox3.github.io/smart-table-website/) |2.1.3 |MIT License |
|[ZeroClipboard](https://github.com/zeroclipboard/zeroclipboard)| 2.2.0| MIT License|

## Prerequisites

The following tools should be properly installed on your computer:

-   [Git](https://git-scm.com/downloads)

-   [NodeJs (with NPM)](https://nodejs.org/en/download/)

-   [Bower](https://bower.io/\#install-bower)

-   [Maven](https://maven.apache.org/download.cgi)

### Proxy configurations

In order to use the different tools behind a proxy please execute the
following commands (*username* and *password* are your credential,
*proxyhost* is the host name or the IP address of the proxy and
*proxyport* is the TCP port of the proxy):

-   **Git**: open a command prompt and execute:

    ```
    git config --global http.proxy http://username:password@proxyhost:proxyport
    ```
    ```
    git config --global https.proxy http://username:password@proxyhost:proxyport
    ```
    
-   **Npm**: open a command prompt and execute:

    ```
    npm config set proxy http://username:password@proxyhost:proxyport
    ```
    ```
    npm config set https-proxy http://username:password@proxyhost:proxyport
    ```
-   **Bower**: change the current directory to the one that contains the
    `bower.json` file and create/edit the `.bowerrc` file and add
    the proxy configuration:

    -   `"proxy" : "http://username:password@proxyhost:proxyport"`

    -   `"https-proxy" : "http://username:password@proxyhost:proxyport"`

-   **Maven**: edit the file `*Path\_Of\_Maven/conf/settings.xml*` and
    add to the `&lt;proxies&gt;` section the proper configuration
    following the example provided in the same file (please refer to
    Maven guide https://maven.apache.org/guides/mini/guide-proxies.html)

## Create WAR packages

Open a command prompt and execute the following command to clone the
repository:

  ```
   git clone https://github.com/OPSILab/Idra.git
  ```

Move in the folder OpenDataFederation
  ```
  cd Idra
  `````

In this folder you will find two subfolders:

-   **`Idra`**: this folder contains the server side
    application of Idra

-   **`IdraPortal:`** this folder contains the client side application
    of Idra

### Execute the following commands to create the *Idra.war*

-   move in Idra folder:
    ```
    cd Idra
    ```
    and then:
    
    ```
    mvn package
    ```
**Note**. Execute this command in a network without proxy because of jitpack dependency.

### Execute the following commands to create the *IdraPortal.war*

-   move in IdraPortal folder:
   
    ```
    cd IdraPortal
    ```
    ```
    cd /src/main/webapp
    ```
    ```
    bower install
    ```
    ```
    cd ../../..
    ```
    ```
    mvn package
    ```
## Deployment

### Artefacts

These are the artefacts that must be installed in order to run Idra:

-   **`Idra.war`**

-   **`IdraPortal.war`**

-   **`rdf4j-workbench.war & rdf4j-sesame.war`**  (you can get both [here](http://www.eclipse.org/downloads/download.php?file=/rdf4j/eclipse-rdf4j-2.2.1-sdk.zip) , into "war" folder)

-   **`idra_db.sql`**

### Database creation

Idra relies on a MySQL database to store all the application data and
collected Open Datasets.

So before deploying the application, it is necessary to create a new
database, by importing in the MySQL server the provided SQL dump file:

-   **`idra_db.sql`**

This dump already contains the statement that creates the
`idra_db` DB automatically. In addition it creates an
administration user with the following credentials:

**`username: admin`**

**`password: admin`**

**Note**. To change the administrator password login in the Idra Portal with the previous credentials then go to the **Administration -> Manage Configurations -> Update Password** section.

### WARs deployment

Move all the WAR artifacts to the `webapps` folder of Tomcat
installation, start it up and wait until they are deployed.

### RDF repository creation

Once the Tomcat server started, go with browser to the URL
`localhost:8080/rdf4j-workbench`

Note. Change the port number according to the configuration of
`server.xml` file of Tomcat `conf` folder (default 8080)

Through the RDF4J GUI, select “new repository” on the left menu, then
create a new repository of type `Native Java Store` called `Idra`.

### Configuration

Once all the WAR files are deployed and the server has started, modify
the following configuration files, located in the deployed folders of
Tomcat `webapps` folder.

-   **`IdraPortal/WEB-INF/classes/`**

    -   In **`configuration.properties`** file, change the following properties:
		- Base url part of **`ADMIN\_SERVICES\_BASE\_URL`** property with the **PUBLIC** domain where
			is exposed the runtime environment. (Example:
			[*https://idra.eng.it/Idra/api/v1/administration*](https://idra.eng.it/Idra/api/v1/administration))
		- Base url part of **`CLIENT\_SERVICES\_BASE\_URL`** property with the **PUBLIC** domain where
			is exposed the runtime environment. (Example:
			[*https://idra.eng.it/Idra/api/v1/client*](https://idra.eng.it/Idra/api/v1/client))
-   **`Idra/WEB-INF/classes/`**

    -   In **`configuration.properties`** file, change the following
        properties:

        -   **`idra.db.host`, `idra.db.user`, `idra.db.password`** with the actual
            parameters of the MySQL server installation.

        -   **`http.proxyHost`, `http.proxyPort`,
            `http.proxyUser`, `http.proxyPassword`** with the proxy
            parameters, leave blank if none. Change **`http.proxyEnabled`** to **`true`** if the previous proxy parameters are provided.

        -   **`idra.odms.dump.file.path`** and **`idra.dump.file.path`** with the folder path where to save the DCAT-AP dump files.
        **NOTE**. The path **MUST** end with "\\" or              "/". 

        -   **`idra.lod.enable`** to **`true`**, in order to enable RDF (Linked Open Data) retrieval, configured with the following parameters, according to the Tomcat configuration, as described in the “**RDF repository creation**” step:
             -   **`idra.lod.server.uri`** with the URL where to find the "repositories" endpoint of RDF4J.  Example: `http\\://localhost\:8080/rdf4j-server/repositories/`
             -  **`idra.lod.server.uri.query`** with the URL where to find the "**query**" endpoint. Example: `http\://localhost\:8080/rdf4j-workbench/repositories/Idra/query`
             -   **`idra.lod.repo.name`** must have the same value of the
            newly created RDF repository.
   
    -   In **`hibernate.properties`** file, change the following
        properties:
        
        -   **`hibernate.connection.url`**, **`hibernate.connection.username`,
            `hibernate.connection.password`** with the actual parameters
            of the MySQL server installation.
#### Authentication Configuration
Idra Platform supports the following authentication mechanisms:
- **Basic Authentication**: Basic login of the Administrator provided by Idra Platform itself.
- **Fiware Identity Manager**: Authentication of the Administrator via an external     instance of Fiware Identity Manager, namely **Keyrock**. Both versions 6 and 7 are supported.

In order to select which Authorization mechanism to use and configure it accordingly, modify the following configuration files, located in the deployed folders of
Tomcat `webapps` folder.
-   **`IdraPortal/WEB-INF/classes/`**
    -   in **`configuration.properties`** file, change the following property:
        - **`idm.authentication.method`**: allowed values are **`BASIC`** and **`FIWARE`** 
-  **`Idra/WEB-INF/classes/`**
    - in **`configuration.properties`** file, change the following property:
        - **`idra.authentication.method`**: allowed values are **`BASIC`** and **`FIWARE`** 

##### Configuring Idra with Fiware IdM Authentication
If the Fiware Identity Manager option was selected (**`FIWARE`**), in order to correctly execute the OAuth2 flow:
- Idra Platform must be registered as an **Application** in the Fiware IdM, by specifying, in the registration form, following parameters:
    - **URL**: **`http://IDRA_PORTAL_HOST/IdraPortal`**
    - **CallbackURL**: **`http://IDRA_HOST/Idra/api/v1/administration/login`**

**Note**. Replace **`IDRA_PORTAL_HOST`** and **`IDRA_HOST`** with the actual values, namely the Base URl where Idra Platform is deployed.

- The User that wants to authenticate itself as Administrator in Idra, must be authorized and have the **"Admin"** role for that application.

**Note**. Please see the [Fiware Identity Manager](https://fiware-idm.readthedocs.io/en/latest/api/#def-apiOAuth) manual for further information about the registration process, user roles, and **Oauth2** APIs.

###### Configuring Idra as OAuth2 Client for Fiware IdM authentication
The registration process, described above, provides **`Client Id`** and **`Client Secret`**, which will be used by Idra platform to perform the Oauth2 flow as a Client.
Modify the following configuration files, located in the deployed folders of Tomcat `webapps` folder.
-   **`IdraPortal/WEB-INF/classes/`**
    -   in **`configuration.properties`** file, change the following properties:
        - **`idm.client.id`**: **`Client Id`** provided by the Fiware IdM .
        - **`idm.client.secret`**: **`Client Secret`** provided by the Fiware IdM.
        - **`idm.redirecturi`**:**`http://IDRA_HOST/Idra/api/v1/administration/login`**, (same value of the **callbackURL** specified above in the IdM).
        - **`idm.logout.callback`**: **`http://IDRA_PORTAL_HOST/IdraPortal`**, (same value of the **URL** specified above in the IdM).
        - **`idm.protocol`**: Protocol of Fiware IdM instance (`http` or `https`)
        - **`idm.host`**: Host of Fiware IdM instance. (**INCLUDE ALSO THE PORT, IF ANY**).
    
-  **`Idra/WEB-INF/classes/`**
    - in **`configuration.properties`** file, change the following property:
        - **`idm.client.id`**, **`idm.client.secret`**, **`idm.redirecturi`**, **`idm.logout.callback`**, **`idm.protocol`**, **`idm.host`** with the **same** values specified above for Idra Portal.
        - **`idm.fiware.version`**: The version of the Fiware IdM, namely Keyrock. Allowed values are **6** and **7**.
        - **`idm.admin.role.name`**: Role name that User must have in the IDM to be authenticated as Idra Administrator. (default: **ADMIN**).

**Note**. Replace **`IDRA_PORTAL_HOST`** and **`IDRA_HOST`** with the actual values, namely the **Base URL** where Idra Platform is deployed.

### Sanity Checks
In order to apply the previous changes, restart the Tomcat server. The Sanity Checks are the steps that the Administrator will take to verify that the installation is ready to be used and tested.

**Note**. Change the “BASEPATH” value with the actual host and port
where is exposed the runtime environment (Tomcat).

##### Catalogue Access Testing
Once the server restarted, go with browser to *http://BASEPATH/IdraPortal*


When the home page is showed, perform the following steps:
- Check that the message "There are no federated catalogues" is showed.
- Check that you can perform the Login as Administrator, in the appropriate section in the topbar.

##### Platform API testing
- Open a command prompt and execute:
    `curl http://BASEPATH/IdraPortal/api/v1/administration/version`
 - Check that you get the version number as output, along with other information about palatform version and release timestamp    

## Idra Sandbox
A demo instance of Idra - Open Data Federation Platform is available at the following link: 
https://idra-sandbox.opsilab.it

## Support / Contact / Contribution

- Martino Maggio: [*martino.maggio@eng.it*](mailto:martino.maggio@eng.it)
- Giuseppe Ciulla: [*giuseppe.ciulla@eng.it*](mailto:giuseppe.ciulla@eng.it)

## Copying and License

Idra - Open Data Federation Platform
 Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
 
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.
 
You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
