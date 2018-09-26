# Deployment

This page shows the deployment procedure of Idra.

## Artefacts

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