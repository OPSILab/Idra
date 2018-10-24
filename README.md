# <a name="top"></a> Idra - Open Data Federation Platform

[![License badge](https://img.shields.io/badge/license-AGPL--3.0-orange.svg)](https://opensource.org/licenses/AGPL-3.0)
[![Docker Pulls](https://img.shields.io/docker/pulls/idraopendata/idra.svg)](https://hub.docker.com/r/idraopendata/idra/)
<br/>
[![Read the Docs](https://img.shields.io/readthedocs/idra.svg)](https://idra.readthedocs.io/en/latest/)

* [Introduction](#introduction)
* [Overall description](#overall-description)
* [Installation and Administration](#installation-and-administration)
* [User Guide](#user-guide)
* [API Reference](#api-reference)
* [Idra Sandbox](#idra-sandbox)
* [Support / Contact / Contribution](#support)
* [Copying and License](#copying-and-license)
    
## Introduction
This is the repository code of **Idra - Open Data Federation Platform** .

Any feedback on this documentation is highly welcome, including bugs, typos and suggestions. You can use github [issues](https://github.com/OPSILab/Idra/issues) to provide feedback.

You can find the User Guide and the Installation & Administration Manual on [readthedocs.io](https://idra.readthedocs.io/en/latest/).

## Overall Description

**Idra** is a web application able to federate existing Open Data Management Systems (ODMS) based on different technologies providing a unique access point to search and discover open datasets coming from heterogeneous sources. Idra uniforms representation of collected open datasets, thanks to the adoption of international standards (DCAT-AP) and provides a set of RESTful APIs to be used by third party applications. 
 
Idra supports natively ODMS based on CKAN, DKAN, Socrata, Orion Context Broker and many other technologies: Idra provides also a set of APIs to federate ODMS not natively supported. In addition, it is possible to federate generic open data portals, that don't expose API, using the web scraping functionality or providing a dump file of the datasets in DCAT-AP format. Moreover Idra provides a SPARQL endpoint in order to perform queries on 5 stars RDF linked open data collected from federated ODMS and allows to easily create charts based on federated open datasets (through [DatalEt-Ecosystem Provider (DEEP)](https://github.com/routetopa/deep-components))
 
Idra is an open source software developed by [Engineering Ingegneria Informatica SpA](http://www.eng.it) inside the EU founded project [FESTIVAL](http://www.festival-project.eu/)

## Installation and Administration

The instruction to install or use the administration functionalities of Idra can be found at  the corresponding section of [Read The Docs](https://idra.readthedocs.io/en/latest/admin/index.html).

## User Guide

The User Guide for Idra can be found at the corresponding section of [Read The Docs](https://idra.readthedocs.io/en/latest/user/index.html).

## API Reference
API Reference Documentation (**Apiary**):
- [`https://idraopendata.docs.apiary.io`](https://idraopendata.docs.apiary.io)

## Idra Sandbox

A demo instance of Idra - Open Data Federation Platform is available at the following link: 
- [`https://idra-sandbox.opsilab.it`](https://idra-sandbox.opsilab.it)


## <a name="support"><a/> Support / Contact / Contribution

- Idra support: [*idra@eng.it*](mailto:idra@eng.it)
 
##### Contacts
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
