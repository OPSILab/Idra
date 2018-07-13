Installation
============

This section covers the steps needed to properly install Idra

Requirements
------------

Idra has the following requirements that must be correctly installed and configured

+-------------------------------------------------------------------------------------------------------------------+------------------+--------------------------------------------------------------------------------------------+
|                                          Framework                                                                |      Version     |                 License                                                                    |
+===================================================================================================================+==================+============================================================================================+
| `Java SE Development Kit <http://docs.oracle.com/javase/8/docs/technotes/guides/install/install\_overview.html>`_ |    8.0           |   Oracle Binary Code License                                                               |
+-------------------------------------------------------------------------------------------------------------------+------------------+--------------------------------------------------------------------------------------------+
| `Apache Tomcat <https://tomcat.apache.org/tomcat-8.0-doc/setup.html>`_                                            |  8.0             | Apache License v.2.0                                                                       |
+-------------------------------------------------------------------------------------------------------------------+------------------+--------------------------------------------------------------------------------------------+
| `MySQL <https://dev.mysql.com/doc/refman/5.7/en/>`_                                                               |  5.7.5 Community |   GNU General Public License Version 2.0                                                   |
+-------------------------------------------------------------------------------------------------------------------+------------------+--------------------------------------------------------------------------------------------+
| `RDF4J Server <http://rdf4j.org/download/>`_                                                                      |   2.2.1          |  `EDL 1.0 (Eclipse Distribution License) <https://eclipse.org/org/documents/edl-v10.php>`_ |
+-------------------------------------------------------------------------------------------------------------------+------------------+--------------------------------------------------------------------------------------------+
| `RDF4J Workbench <http://rdf4j.org/download/)>`_                                                                  |   2.2.1          |  `EDL 1.0 (Eclipse Distribution License) <https://eclipse.org/org/documents/edl-v10.php>`_ |
+-------------------------------------------------------------------------------------------------------------------+------------------+--------------------------------------------------------------------------------------------+

Libraries
---------

Idra is based on the following software libraries and frameworks.

+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|Framework                                                                                                    |Version      |Licence                                        |
+=============================================================================================================+=============+===============================================+
|`Apache SOLR-Lucene (SOLR Core) <http://lucene.apache.org/solr/>`_                                           |6.6.0        |Apache License                                 |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Apache Http Client <https://hc.apache.org/httpcomponents-client-ga/index.html>`_                            |4.5.2        |Apache License                                 |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Apache Http Core <https://hc.apache.org/httpcomponents-core-ga/index.html>`_                                |4.5.2        |Apache License                                 |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Mysql connector (Community Release) <https://www.mysql.it/products/connector/>`_                            |5.1.39       |GPL 2.0 (GNU General Public License Version)   |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Hibernate <http://hibernate.org/>`_                                                                         |5.2.10.Final |LGPL 2.1 (GNU Lesser General Public License)   |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Hikari <https://github.com/brettwooldridge/HikariCP>`_                                                      |2.6.1        |Apache License 2.0                             |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Log4j <http://logging.apache.org/log4j/2.x/>`_                                                              |2.7          |Apache License 2.0                             |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`CKANClient-J <https://github.com/okfn/CKANClient-J>`_                                                       |1.7          |AGPL 3.0 (GNU Affero General Public License)   |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`RDF4J-Runtime <http://rdf4j.org/download/>`_                                                                | 2.2.1       |EDL 1.0 (Eclipse Distribution License)         |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`AngularJS <https://angularjs.org/>`_                                                                        | 1.5.9       |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Angular-UI - bootstrap-ui <https://angular-ui.github.io/>`_                                                 |0.13.3       |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Bootstrap <http://getbootstrap.com/>`_                                                                      |3.3.2        |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Bootstrap-Material <http://fezvrasta.github.io/bootstrap-material-design/>`_                                |3            |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Smart-table <http://lorenzofox3.github.io/smart-table-website/>`_                                           |2.1.3        |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`ngImageCrop <https://github.com/alexk111/ngImgCrop>`_                                                       |0.3.2        |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`spin.js <http://fgnass.github.io/spin.js/>`_                                                                |2.3.2        |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`angular-zeroclipboard <https://github.com/lisposter/angular-zeroclipboard>`_                                |0.8.0        |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`angular-xeditable <https://github.com/vitalets/angular-xeditable>`_                                         |0.1.8        |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`angular-pagination <https://github.com/michaelbromley/angularUtils/tree/master/src/directives/pagination>`_ |0.11.0       |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Ace Editor <https://ace.c9.io>`_                                                                            |1.2.0        |BSD                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+
|`Angular-UI - ace-ui <https://angular-ui.github.io/>`_                                                       |0.2.3        |MIT                                            |
+-------------------------------------------------------------------------------------------------------------+-------------+-----------------------------------------------+


Prerequisites
-------------

The following tools should be properly installed on your computer:

-   `Git <https://git-scm.com/downloads>`_

-   `NodeJs (with NPM) <https://nodejs.org/en/download/>`_

-   `Bower <https://bower.io/\#install-bower>`_

-   `Maven <https://maven.apache.org/download.cgi>`_

Proxy configurations
^^^^^^^^^^^^^^^^^^^^

In order to use the different tools behind a proxy please execute the
following commands (*username* and *password* are your credential,
*proxyhost* is the host name or the IP address of the proxy and
*proxyport* is the TCP port of the proxy):

-	**Git**: open a command prompt and execute:
		.. code-block:: console
		
			$ git config --global http.proxy http://username:password@proxyhost:proxyport
			$ git config --global https.proxy http://username:password@proxyhost:proxyport

-	**Npm**: open a command prompt and execute:
		.. code-block:: console
		
			$ npm config set proxy http://username:password@proxyhost:proxyport
			$ npm config set https-proxy http://username:password@proxyhost:proxyport

-	**Bower**: change the current directory to the one that contains the
	“*bower.json*” file and create/edit the “*.bowerrc*” file and add
	the proxy configuration:
		.. code-block:: json
		
			{
				"proxy" : "http://username:password@proxyhost:proxyport",
				"https-proxy" : "http://username:password@proxyhost:proxyport"
			}

-   **Maven**: edit the file “*Path\_Of\_Maven/conf/settings.xml*” and
    add to the “*<proxies>*” section the proper configuration
    following the example provided in the same file (please refer to
    maven guide https://maven.apache.org/guides/mini/guide-proxies.html)

Create WAR packages
-------------------

Open a command prompt and Execute the following command to clone the
repository:
	.. code-block:: console
	
		$ git clone https://github.com/OPSILab/Idra.git
		$ cd Idra

In this folder you will find two subfolders:

-   **Idra**: this folder contains the server side
    application of Idra

-   **IdraPortal:** this folder contains the client side application
    of Idra

*Idra.war*
^^^^^^^^^^
Move in Idra folder: 
	.. code-block:: console
	
		$ cd Idra
		$ mvn package

**Note**. Execute this command in a network without proxy because of jitpack dependency.

*IdraPortal.war*
^^^^^^^^^^^^^^^^
Move in IdraPortal folder: 
	.. code-block:: console

		$ cd IdraPortal
		$ cd /src/main/webapp
		$ bower install
		$ cd ../../..
		$ mvn package

