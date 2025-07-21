#
# Idra - Open Data Federation Platform
# Copyright (C) 2025 Engineering Ingegneria Informatica S.p.A.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

################################################
#
# This dockerFile builds the Docker Image from which can be created the related container.
# This container will install a Tomcat instance where will be deployed the WARs built from the official Idra repository

# The base image from which the image build starts
# The image is based on the source code present in the project's Idra folder

FROM maven:3.9.6-eclipse-temurin-21 AS build
MAINTAINER Engineering Ingegneria Informatica S.p.A.

WORKDIR /

RUN apt-get update && \
    apt-get install -y git curl

COPY Idra/pom.xml Idra/
COPY Idra/src/ Idra/src/

RUN cd Idra && mvn package

RUN git clone https://github.com/vishnubob/wait-for-it.git

FROM tomcat:8.5.100-jre21-temurin AS deploy

WORKDIR /
COPY --from=build /Idra/target/Idra.war /
COPY --from=build /wait-for-it/wait-for-it.sh /

RUN mv Idra.war /usr/local/tomcat/webapps
RUN chmod +x wait-for-it.sh

EXPOSE 8080

WORKDIR /

CMD ["catalina.sh", "run"]
