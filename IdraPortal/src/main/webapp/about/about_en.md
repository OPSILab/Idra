### What is Idra?

Idra is a web application able to federate existing Open Data Management Systems
(ODMS) based on different technologies providing a unique access point to search
and discover open datasets coming from heterogeneous sources. Idra uniforms
representation of collected open datasets, thanks to the adoption of
international standards (DCAT-AP) and provides a set of RESTful APIs to be used
by third party applications.

Idra supports natively ODMS based on [CKAN](https://ckan.org/),
[DKAN](https://getdkan.org/), [Socrata](https://socrata.com/), Orion Context
Broker
([NGSI v2](https://swagger.lab.fiware.org/?url=https://raw.githubusercontent.com/Fiware/specifications/master/OpenAPI/ngsiv2/ngsiv2-openapi.json))
and many other technologies: Idra provides also a set of APIs to federate ODMS
not natively supported. In addition, it is possible to federate generic open
data portals, that don't expose an API, using the web scraping functionality or
providing a dump file of the datasets in
[DCAT-AP](https://joinup.ec.europa.eu/solution/dcat-application-profile-data-portals-europe)
format. Moreover Idra provides a [SPARQL](https://www.w3.org/TR/sparql11-query/)
endpoint in order to perform queries on 5 stars RDF
[linked open data](https://dvcs.w3.org/hg/gld/raw-file/default/glossary/index.html)
collected from federated ODMS and allows to easily create charts based on
federated open datasets.

### Why use Idra?

One of the most relevant problems that limits to adoption and usage of open data
is related to technology and standard fragmentation: every entity that wants to
provide open data (e.g. Public Administration) uses different platforms and
formats for their publication. This issue represents a barrier for the user that
wants to search for open data, and for third-party applications that are not
able to simply access and reuse it. Idra is an open source platform that
provides a unique access point to open datasets coming from heterogeneous ODMS
(Open Data Management System), such as portals provided by Municipalities and
PAs.

The adoption and usage of Idra can have direct benefits for different
stakeholders:

#### Benefits for the Public Administrations:

-   Standardise open data exposing it through open API and standard models
    (DCAT_AP) compliant with European guidelines. The API could be harvested by
    other open data federation portals (e.g. European Data portal)
-   Provide to the citizen, with minimum effort, new functionalities not
    existent in their native portals (e.g. SPARQL queries, graphical
    visualisation of datasets etc)
-   Improve visibility because their data are part of a federation potentially
    accessible by a larger number of users

#### Benefits for the citizens

-   Use a single access point for the open data of different entities avoiding
    problems related to find and use heterogeneous open data portals
-   Access to advanced search and visualisation functionalities not existent in
    the original portals

#### Benefits for companies

-   Access to a large number of open datasets in a unique and standard way
    together with data visualisation and analytics functionalities for different
    scopes (e.g. Statistics, sector-specific data research, data reuse to build
    innovative and added-value application)

The **Idra** project is part of [FIWARE](https://fiware.org/).