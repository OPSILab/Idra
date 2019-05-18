# Introduction

This document describes how to install and launch the Idra platform, by building
the WAR packages from source code. After completing the build process, the
following artefacts can be deployed on the Apache Tomcat Server:

-   **`Idra.war`**
-   **`IdraPortal.war`**
-   **`rdf4j-workbench.war & rdf4j-sesame.war`** (you can get both
    [here](http://www.eclipse.org/downloads/download.php?file=/rdf4j/eclipse-rdf4j-2.2.1-sdk.zip)
    , into "war" folder)

## Prerequisites

In order to build correctly the packages, the following tools should be properly
installed on your computer:

-   [Git](https://git-scm.com/downloads)
-   [Node.js (with NPM)](https://nodejs.org/en/download/)
-   [Bower](https://bower.io/#install-bower)
-   [Maven](https://maven.apache.org/download.cgi)

** Proxy configurations **

In order to use the different tools behind a proxy please execute the following
commands (**username** and **password** are your credentials, **proxyhost** is
the hostname or the IP address of the proxy and **proxyport** is the TCP port of
the proxy):

-   **Git**: open a command prompt and execute

```bash
git config --global http.proxy http://username:password@proxyhost:proxyport
```

```bash
git config --global https.proxy http://username:password@proxyhost:proxyport
```

-   **Npm**: open a command prompt and execute:

```bash
npm config set proxy http://username:password@proxyhost:proxyport
```

```bash
npm config set https-proxy http://username:password@proxyhost:proxyport
```

-   **Bower**: change the current directory to the one that contains the
    `bower.json` file and create/edit the `.bowerrc` file and add the proxy
    configuration:

```json
"proxy" : "http://username:password@proxyhost:proxyport",
"https-proxy" : "http://username:password@proxyhost:proxyport"
```

-   **Maven**: edit the file `Path_To_Maven/conf/settings.xml` and add to the
    `<proxies>` section the proper configuration following the example provided
    in the same file (please refer to
    [Maven guide](https://maven.apache.org/guides/mini/guide-proxies.html)).

**Get the source code from repository**

Open a command prompt and execute the following command to clone the source code
from the Idra [GitHub](https://github.com/OPSILab/Idra.git) repository:

```bash
git clone https://github.com/OPSILab/Idra.git
```

Move into **`Idra`** folder:

```bash
cd Idra
```

In this folder you will find two subfolders:

-   **`Idra`**: this folder contains the server-side application of Idra
-   **`IdraPortal:`** this folder contains the client-side application of Idra

See the [Architecture Overview](../architecture/architecture.md) for further
detail.

## Build WAR packages

Execute the following commands to create the **Idra.war**.

-   Move into `Idra` folder:

```bash
cd Idra
```

-   Then execute Maven package goal:

```bash
mvn package
```

**Note**. Execute this command in a network without proxy because of jitpack
dependency.

---

Execute the following commands to create the _IdraPortal.war_.

-   Move into IdraPortal `webapp` folder:

```bash
cd IdraPortal/src/main/webapp
```

-   Execute Bower install:

```bash
bower install
```

-   Return to the `IdraPortal` folder:

```bash
cd ../../..
```

-   Then execute Maven package goal:

```bash
mvn package
```

## Deployment & configuration

### Database creation

Idra relies on a MySQL database to store all the application data and collected
Open Datasets.

So before deploying the application, it is necessary to create a new database,
by importing in the MySQL server the provided SQL dump file:

-   **`idra_db.sql`**

This dump already contains the statement that creates the `idra_db` DB
automatically. In addition it creates an administration user with the following
credentials:

-   **`username: admin`**
-   **`password: admin`**

**Note**. To change the administrator password login in the Idra Portal with the
previous credentials then go to the **Administration -> Manage Configurations ->
Update Password** section.

### WARs deployment

Move all the WAR artifacts to the `webapps` folder of Tomcat installation, start
it up and wait until they are deployed.

### RDF repository creation

Once the Tomcat server started, go with browser to the URL
`localhost:8080/rdf4j-workbench`

Note. Change the port number according to the configuration of `server.xml` file
of Tomcat `conf` folder (default 8080)

Through the RDF4J GUI, select “new repository” on the left menu, then create a
new repository of type `Native Java Store` called `Idra`.

### Configuration

Once all the WAR files are deployed and the server has started, modify the
properties of following configuration files, located in the deployed folders of
Tomcat `webapps` folder.

-   **`Idra/WEB-INF/classes/configuration.properties`** :

    -   **`idra.db.host`, `idra.db.user`, `idra.db.password`** with the actual
        parameters of the MySQL server installation.

    -   **`http.proxyHost`, `http.proxyPort`,`http.proxyUser`,
        `http.proxyPassword`** with the proxy parameters, leave blank if none.
        Change **`http.proxyEnabled`** to **`true`** if the previous proxy
        parameters are provided.

    -   **`idra.odms.dump.file.path`** and **`idra.dump.file.path`** with the
        folder path where to save the DCAT-AP dump files. **NOTE**. The path
        **MUST** end with `\` or `/`.

    -   **`idra.lod.enable`** to **`true`**, in order to enable RDF (Linked Open
        Data) retrieval, configured with the following parameters, according to
        the Tomcat configuration, as described in the “**RDF repository
        creation**” step: - **`idra.lod.server.uri`** with the URL where to find
        the "repositories" endpoint of RDF4J. Example:
        `http\\://localhost\:8080/rdf4j-server/repositories/` -
        **`idra.lod.server.uri.query`** with the URL where to find the
        "**query**" endpoint. Example:
        `http\://localhost\:8080/rdf4j-workbench/repositories/Idra/query` -
        **`idra.lod.repo.name`** must have the same value of the newly created
        RDF repository.

-   **`Idra/WEB-INF/classes/hibernate.properties`** :

    -   **`hibernate.connection.url`**, **`hibernate.connection.username`,
        `hibernate.connection.password`** with the actual parameters of the
        MySQL server installation.

-   **`Idra/WEB-INF/classes/quartz.properties`** : -
    **`org.quartz.dataSource.myDS.URL`**, **`org.quartz.dataSource.myDS.user`**,
    **`org.quartz.dataSource.myDS.password`** with the actual parameters of the
    MySQL server installation.

**IMPORTANT Note.** Previous properties will be eventually overwritten if there
is an environment variable with the same name. This is the wanted behaviour in
case of [Installation with Docker](install_docker.md).

### Authentication Configuration

Idra Platform supports the following authentication mechanisms:

-   **Basic Authentication**: Basic login of the Administrator provided by Idra
    Platform itself.

-   **Fiware Identity Manager**: Authentication of the Administrator via an
    external instance of Fiware Identity Manager, namely **Keyrock**. Both
    versions 6 and 7 are supported.

In order to select which Authorization mechanism to use and configure it
accordingly, modify the properties of following configuration files, located in
the deployed folders of Tomcat `webapps` folder.

-   **`IdraPortal/WEB-INF/classes/configuration.properties`** :

    -   **`idm.authentication.method`**: allowed values are **`BASIC`** and
        **`FIWARE`**

-   **`Idra/WEB-INF/classes/configuration.properties`** :

        	- **`idra.authentication.method`**: allowed values are **`BASIC`** and **`FIWARE`**

##### Configuring Idra with Fiware IdM Authentication

If the Fiware Identity Manager option was selected (**`FIWARE`**), in order to
correctly execute the OAuth2 flow:

-   Idra Platform must be registered as an **Application** in the Fiware IdM, by
    specifying, in the registration form, following parameters:

    -   **URL**: **`http://IDRA_PORTAL_HOST/IdraPortal`**
    -   **CallbackURL**: **`http://IDRA_HOST/Idra/api/v1/administration/login`**

**Note**. Replace **`IDRA_PORTAL_HOST`** and **`IDRA_HOST`** with the actual
values, namely the Base URL where Idra Platform is deployed.

-   The User that wants to authenticate itself as Administrator in Idra, must be
    authorized and have the role matching with the one specified in the
    **`idm.admin.role.name`** configuration property.

**Note**. Please see the
[Fiware Identity Manager](https://fiware-idm.readthedocs.io/en/latest/api/#def-apiOAuth)
manual for further information about the registration process, user roles, and
**Oauth2** APIs.

###### Configuring Idra as OAuth2 Client for Fiware IdM authentication

The registration process, described above, provides **`Client Id`** and
**`Client Secret`**, which will be used by Idra platform to perform the Oauth2
flow as a Client. Modify the properties of following configuration files,
located in the deployed folders of Tomcat `webapps` folder., located in the
deployed folders of Tomcat `webapps` folder.

-   **`IdraPortal/WEB-INF/classes/configuration.properties`**:

    -   **`idm.client.id`**: **`Client Id`** provided by the Fiware IdM .
    -   **`idm.client.secret`**: **`Client Secret`** provided by the Fiware IdM.
    -   **`idm.redirecturi`**:**`http://IDRA_HOST/Idra/api/v1/administration/login`**,
        (same value of the **callbackURL** specified above in the IdM).
    -   **`idm.logout.callback`**: **`http://IDRA_PORTAL_HOST/IdraPortal`**,
        (same value of the **URL** specified above in the IdM).
    -   **`idm.protocol`**: Protocol of Fiware IdM instance (`http` or `https`)
    -   **`idm.host`**: Host of Fiware IdM instance. (**INCLUDE ALSO THE PORT,
        IF ANY**).

-   **`Idra/WEB-INF/classes/configuration.properties`**:

    -   **`idm.client.id`**, **`idm.client.secret`**, **`idm.redirecturi`**,
        **`idm.logout.callback`**, **`idm.protocol`**, **`idm.host`** with the
        **same** values specified above for Idra Portal.
    -   **`idm.fiware.version`**: The version of the Fiware IdM, namely Keyrock.
        Allowed values are **6** and **7**.
    -   **`idm.admin.role.name`**: Role name that User must have in the IDM to
        be authenticated as Idra Administrator. (default: **ADMIN**).

**Note**. Replace **`IDRA_PORTAL_HOST`** and **`IDRA_HOST`** with the actual
values, namely the **Base URL** where Idra Platform is deployed.

### Applying configuration

In order to apply all the configuration done previously, restart the Tomcat and
wait until the artifacts are redeployed.
