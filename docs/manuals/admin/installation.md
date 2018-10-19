# Installing Idra

This section covers the steps needed to properly install Idra. 
The Idra platform is a Java EE application that can be installed in the following ways:

* [War packaging](install_war.md)
* [Docker containerized environment](install_docker.md)

The following sections describe each installation method in detail.
## Requirements

Idra has the following requirements that must be correctly installed and
configured

| Framework                                                                        |Version          |Licence                                                               |
|----------------------------------------------------------------------------------|-----------------|----------------------------------------------------------------------|
|[Java SE Development Kit](http://docs.oracle.com/javase/8/docs/technotes/guides/install/install\_overview.html) |8.0 |Oracle Binary Code License         |
|[Apache Tomcat](https://tomcat.apache.org/tomcat-8.5-doc/setup.html) |8.5 |Apache License v.2.0 |
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
|[Apache Jena ARQ](https://jena.apache.org/documentation/query/)| 3.3.0| Apache License 2.0|
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
