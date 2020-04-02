#
# Idra - Open Data Federation Platform
# Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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


FROM        maven:3.5-jdk-8-alpine as build
MAINTAINER Engineering Ingegneria Informatica S.p.A.

WORKDIR /
    
# RUN export http_proxy && export https_proxy    	

# Update the environment and install various utilities used for installation
RUN         apk update && \
            apk add git curl

# Install NodeJS 		
RUN 		apk add --update nodejs nodejs-npm

#Install Bower
RUN			npm install -g bower

### Clone the official Idra GitHub repository ###
#RUN			git clone https://github.com/OPSILab/Idra.git #&& mv .bowerrc ./Idra/IdraPortal/src/main/webapp

COPY . .
    
### Build Idra War package
RUN cd Idra && mvn package

### Build IdraPortal War package
RUN cd IdraPortal/src/main/webapp && bower install --allow-root
RUN cd /IdraPortal && mvn package

### Import script for waiting MySQL completes startup
RUN git clone https://github.com/vishnubob/wait-for-it.git


#### Pass built Idra.war and IdraPortal.war to the next build stage in /usr/local/tomcat/webapps container's folder
FROM        tomcat:8.0.50-jre8-alpine as deploy

WORKDIR /
COPY --from=build /Idra/target/Idra.war /
COPY --from=build /IdraPortal/target/IdraPortal.war /
COPY --from=build /wait-for-it/wait-for-it.sh /

RUN mv IdraPortal.war /usr/local/tomcat/webapps && mv Idra.war /usr/local/tomcat/webapps
RUN chmod +x wait-for-it.sh


# Set the port to expose. WARNING The "docker run" command, used to run a container from the image built from this DockerFile,
# MUST define the mapping with the host ports.
# (e.g. -p 8080:8080 option of "docker run" command maps the 8080 port of the container (exposed through the following EXPOSE directive) to the host's 8080 port )
EXPOSE 8080

# Set the working directory from which run the following commands
WORKDIR     /

# Start the Apache Tomcat server
CMD ["catalina.sh", "run"]