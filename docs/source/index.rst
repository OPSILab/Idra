Idra - Open Data Federation Platform
====================================

Idra is a web application able to federate
existing Open Data Management Systems (ODMS) based on different
technologies; in this way Idra provides a unique access point to search
and discover open data sets coming from the different federated ODMS.
Idra uniforms representation of collected Open Data Set, thanks to the
adoption of international standards (DCAT-AP) and provides a set of APIs to
develop third party applications. Idra supports natively ODMS based on
CKAN, DKAN and Socrata and provides a set of APIs to federate ODMSs not
natively supported; these ODMSs have to implement and expose them.
In addition, it is possible to federate a generic Web Portal, either by using the Web Scraping functionality or by uploading a dump of the datasets in DCAT-AP format.
Moreover, Idra provides a SPARQL endpoint in order to perform queries on 5
stars RDF linked open data collected from federated ODMSs.

Content
=======

.. toctree::
   :maxdepth: 2
   
   environment/architecture.md
   manuals/administration
   manuals/enduser.md

