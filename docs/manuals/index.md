# Idra - Open Data Federation Platform

[![](https://nexus.lab.fiware.org/repository/raw/public/badges/chapters/data-publication.svg)](https://www.fiware.org/developers/catalogue/)
[![](https://img.shields.io/badge/tag-fiware-orange.svg?logo=stackoverflow)](http://stackoverflow.com/questions/tagged/fiware)

Idra is a web application able to federate existing Open Data Management Systems
(ODMS) based on different technologies providing a unique access point to search
and discover open datasets coming from heterogeneous sources. Idra uniforms
representation of collected open datasets, thanks to the adoption of
international standards
([DCAT-AP](https://joinup.ec.europa.eu/solution/dcat-application-profile-data-portals-europe))
and provides a set of RESTful APIs to be used by third party applications.

Idra supports natively ODMS based on [CKAN](https://ckan.org/),
[DKAN](https://getdkan.org/), [Socrata](https://socrata.com/), Orion Context
Broker
([NGSI v2](https://swagger.lab.fiware.org/?url=https://raw.githubusercontent.com/Fiware/specifications/master/OpenAPI/ngsiv2/ngsiv2-openapi.json))
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
[DEEP](https://github.com/routetopa/deep-components))

Idra is an open source software developed by
[Engineering Ingegneria Informatica SpA](http://www.eng.it) inside the EU
founded project [FESTIVAL](http://www.festival-project.eu/)
