<p align="center">
<img width="200" height="200" src="https://www.gravatar.com/avatar/78a3bb96d2bdda688ff42cd070a5e06d?s=200">
</p>

# Idra - Open Data Federation Platform

[![](https://nexus.lab.fiware.org/repository/raw/public/badges/chapters/data-publication.svg)](https://www.fiware.org/developers/catalogue/)
[![License badge](https://img.shields.io/github/license/OPSILab/Idra.svg)](https://opensource.org/licenses/AGPL-3.0)
[![Docker Pulls](https://img.shields.io/docker/pulls/idraopendata/idra.svg)](https://hub.docker.com/r/idraopendata/idra/)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Frobcalla%2FIdra.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Frobcalla%2FIdra?ref=badge_shield)
[![](https://img.shields.io/badge/tag-fiware-orange.svg?logo=stackoverflow)](http://stackoverflow.com/questions/tagged/fiware)
<br>
[![Documentation badge](https://img.shields.io/readthedocs/idra.svg)](https://idra.readthedocs.io/en/latest/)
![Status](https://nexus.lab.fiware.org/static/badges/statuses/idra.svg)

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
data portals, that don't expose API, using the web scraping functionality or
providing a dump file of the datasets in
[DCAT-AP](https://joinup.ec.europa.eu/solution/dcat-application-profile-data-portals-europe)
format. Furthermore, Idra provides a
[SPARQL](https://www.w3.org/TR/sparql11-query/) endpoint in order to perform
queries on 5 stars RDF
[linked open data](https://dvcs.w3.org/hg/gld/raw-file/default/glossary/index.html)
collected from federated ODMS and allows to easily create charts based on
federated open datasets (through DatalEt-Ecosystem Provider
[DEEP](https://github.com/routetopa/deep-components) )

Idra is an open source software developed by
[Engineering Ingegneria Informatica SpA](http://www.eng.it) inside the EU
founded project [FESTIVAL](http://www.festival-project.eu/). This project is
part of [FIWARE](https://www.fiware.org/). For more information check the FIWARE
Catalogue entry for
[Data Publication](https://github.com/Fiware/catalogue/tree/master/data-publication). The roadmap of this FIWARE GE is described [here](./roadmap.md)

## Content

-   [Install](#install)
-   [Usage](#usage)
-   [API](#api)
-   [Idra Sandbox](#idra-sandbox)
-   [Support / Contact / Contribution](#support)
-   [License](#license)

## Install

The instruction to install or use the administration functionalities of Idra can
be found at the corresponding section of
[Read The Docs](https://idra.readthedocs.io/en/latest/admin/index.html).

## Usage

The User Guide for Idra can be found at the corresponding section of
[Read The Docs](https://idra.readthedocs.io/en/latest/user/index.html).

## API

API Reference Documentation (**Apiary**):

-   [`https://idraopendata.docs.apiary.io`](https://idraopendata.docs.apiary.io)

## Idra Sandbox

A demo instance of Idra - Open Data Federation Platform is available at the
following link:

-   [`https://idra-sandbox.eng.it`](https://idra-sandbox.eng.it)

<a name="support"></a>

## Support / Contact / Contribution

Any feedback on this documentation is highly welcome, including bugs, typos and
suggestions. You can use GitHub [issues](https://github.com/OPSILab/Idra/issues)
to provide feedback.

-   Idra support: [_idra@eng.it_](mailto:idra@eng.it)

##### Contacts

-   Martino Maggio: [_martino.maggio@eng.it_](mailto:martino.maggio@eng.it)
-   Giuseppe Ciulla: [_giuseppe.ciulla@eng.it_](mailto:giuseppe.ciulla@eng.it)

---

## License

Idra - Open Data Federation Platform © 2018 Engineering Ingegneria Informatica
S.p.A.

This program is free software: you can redistribute it and/or modify it under
the terms of the GNU Affero General Public License as published by the Free
Software Foundation, either version 3 of the License, or at your option) any
later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along
with this program. If not, see <http://www.gnu.org/licenses/>.


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Frobcalla%2FIdra.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Frobcalla%2FIdra?ref=badge_large)