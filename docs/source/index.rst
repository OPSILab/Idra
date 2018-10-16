Idra - Open Data Federation Platform
====================================

Idra is a web application able to federate existing Open Data Management Systems (ODMS) based on different technologies providing a unique access point to search and discover open datasets coming from heterogeneous sources. Idra uniforms representation of collected open datasets, thanks to the adoption of international standards (DCAT-AP) and provides a set of RESTful APIs to be used by third party applications. 
 
Idra supports natively ODMS based on CKAN, DKAN, Socrata, Orion Context Broker and many other technologies: Idra provides also a set of APIs to federate ODMS not natively supported. In addition, it is possible to federate generic open data portals, that don't expose API, using the web scraping functionality or providing a dump file of the datasets in DCAT-AP format. Moreover Idra provides a SPARQL endpoint in order to perform queries on 5 stars RDF linked open data collected from federated ODMS and allows to easily create charts based on federated open datasets (through `DatalEt-Ecosystem Provider (DEEP) <https://github.com/routetopa/deep-components>`_)
 
Idra is an open source software developed by `Engineering Ingegneria Informatica SpA <http://www.eng.it>`_ inside the EU founded project `FESTIVAL <http://www.festival-project.eu/>`_

Content
=======

.. toctree::
   :maxdepth: 2
   
   environment/architecture.md
   manuals/administration
   manuals/enduser.md

