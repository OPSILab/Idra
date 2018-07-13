# Deployment

This page shows the deployment procedure of Idra.

## Artefacts

These are the artefacts that must be installed in order to run Idra:

-   Idra.war

-   IdraPortal.war

-   rdf4j-workbench.war & rdf4j-sesame.war  (you can get both [here](http://www.eclipse.org/downloads/download.php?file=/rdf4j/eclipse-rdf4j-2.2.1-sdk.zip) , into "war" folder)

-   idra_db.sql

## Database creation

Idra relies on a MySQL database to store all the application data and
collected Open Datasets.

So before deploying the application, it is necessary to create a new
database, by importing in the MySQL server the provided SQL dump file:

-   **idra_db.sql**

This dump already contains the statement that creates the
“**idra_db**” DB automatically. In addition it creates an
administration user with the following credentials:

**username: admin**

**password: admin**

**Note**. To change the administrator password login in the Idra Portal with the previous credentials then go to the **Administration -> Manage Configurations -> Update Password** section.

## WARs deployment

Move all the WAR artifacts to the “webapps” folder of Tomcat
installation, start it up and wait until they are deployed.

## RDF repository creation

Once the Tomcat server started, go with browser to the URL
**“localhost:8080/rdf4j-workbench”**

Note. Change the port number according to the configuration of
server.xml file of Tomcat “conf” folder (default 8080)

Through the RDF4J GUI, select “new repository” on the left menu, then
create a new repository of type **“Native Java Store”** called **“Idra”**.

## Configuration

Once all the WAR files are deployed and the server has started, modify
the following configuration files, located in the deployed folders of
Tomcat “webapps” folder.

-   IdraPortal/WEB-INF/classes/

    -   In **configuration.properties** file, change the following properties:
		- Base url part of **ADMIN\_SERVICES\_BASE\_URL** property with the **PUBLIC** domain where
			is exposed the runtime environment. (Example:
			[*https://idra.opsilab.it/Idra/api/v1/administration*](https://idra.opsilab.it/Idra/api/v1/administration))
		- Base url part of **CLIENT\_SERVICES\_BASE\_URL** property with the **PUBLIC** domain where
			is exposed the runtime environment. (Example:
			[*https://idra.opsilab.it/Idra/api/v1/client*](https://idra.opsilab.it/Idra/api/v1/client))
-   Idra/WEB-INF/classes/

    -   In **configuration.properties** file, change the following
        properties:

        -   **DB\_HOST, DB\_USERNAME, DB\_PASSWORD** with the actual
            parameters of the MySQL server installation.

        -   **http.proxyHost, http.proxyPort,
            http.proxyUser, http.proxyPassword** with the proxy
            parameters, leave blank if none. Change **http.proxyEnabled** to **true**              if the previous proxy parameters are provided.

        -   **odmsDumpFilePath** and **dumpFilePath** with the folder path where to                 save the DCAT-AP dump files. **NOTE** The path **MUST** end with "\\" or              "/". 
        -   **sesameRepositoryName** must have the same value of the
            newly created RDF repository.
        -   **enableRdf** to **true**, in order to enable RDF retrieval, configured with the following parameters, according to the Tomcat configuration, as described in the “**RDF repository creation**” step:
             -   **sesameServerURI** with the URL where to find the "repositories" endpoint of RDF4J.  Example: 
             `http\\://localhost\:8080/rdf4j-server/repositories/`
             -  **sesameEndPoint** with the URL where to find the "query" endpoint.                Example:
             `http\://localhost\:8080/rdf4j-workbench/repositories/Idra/query`
   
    -   In **hibernate.properties** file, change the following
        properties:
        
        -   **hibernate.connection.url**, **hibernate.connection.username,
            hibernate.connection.password** with the actual parameters
            of the MySQL server installation.