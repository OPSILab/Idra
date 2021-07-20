#
# Idra - Open Data Federation Platform
# Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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

# Set all needed environment variables
# ENV http_proxy='' \ 
    # https_proxy='' \
	# proxy_host='' \
	# proxy_port='' \
	# proxy_username='' \
	# proxy_password='' \
	# proxy_enable="f"
    
# RUN export http_proxy && export https_proxy    	

# Update the environment and install various utilities used for installation
RUN         apk update && \
            apk add git curl

# Install NodeJS 		
RUN 		apk add --update nodejs nodejs-npm


#Install Bower
RUN			npm install -g bower

# Set proxy settings
# RUN git config --global http.proxy $http_proxy &&\
			# git config --global https.proxy $https_proxy &&\
			# npm config set proxy $http_proxy &&\
			# npm config set https-proxy $https_proxy

# RUN a='{"registry":"https://registry.bower.io","proxy":"' &&\
# b='","https-proxy" : "' &&\
# c='"}' && \
# echo $a$http_proxy$b$https_proxy$c | tee .bowerrc

ARG GITHUB_ACCOUNT=OPSILab
ARG GITHUB_REPOSITORY=Idra
ARG DOWNLOAD=v2.2.0
### Clone the official Idra GitHub repository ###
RUN echo "${DOWNLOAD} https://github.com/${GITHUB_ACCOUNT}/${GITHUB_REPOSITORY}"
RUN git clone --depth 1 --branch "${DOWNLOAD}" https://github.com/"${GITHUB_ACCOUNT}"/"${GITHUB_REPOSITORY}"
	#&& mv .bowerrc ./Idra/IdraPortal/src/main/webapp
    
RUN cd Idra && git checkout docker
# RUN mkdir /Idra
#ADD Idra /Idra/Idra
#ADD IdraPortal /Idra/IdraPortal
# RUN a='<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd"><localRepository>a/.m2/repository</localRepository><proxies><proxy><id>myproxy</id><active>' && \
# b='</active><protocol>http</protocol><host>' && \	  
# c='</host><port>' && \
# d='</port><username>' && \
# e='</username><password>' && \
# f='</password><nonProxyHosts>*.google.com|ibiblio.org</nonProxyHosts></proxy><proxy><id>myproxy1</id><active>' && \
# g='</active><protocol>https</protocol><host>' && \
# h='</password><nonProxyHosts>*.google.com|ibiblio.org</nonProxyHosts></proxy></proxies></settings>' && \
# echo $a$proxy_enable$b$proxy_host$c$proxy_port$d$proxy_username$e$proxy_password$f$proxy_enable$g$proxy_host$c$proxy_port$d$proxy_username$e$proxy_password$h | tee settings.xml && \		
# mkdir /root/.m2 && cp settings.xml /root/.m2


### Build Idra War package
RUN cd Idra/Idra && mvn package

### Build IdraPortal War package
RUN cd Idra/IdraPortal/src/main/webapp && bower install --allow-root
RUN cd /Idra/IdraPortal && mvn package

### Import script for waiting MySQL completes startup
RUN git clone https://github.com/vishnubob/wait-for-it.git


#### Pass built Idra.war and IdraPortal.war to the next build stage in /usr/local/tomcat/webapps container's folder
FROM        tomcat:8.0.50-jre8-alpine as deploy

WORKDIR /
COPY --from=build /Idra/Idra/target/Idra.war /
COPY --from=build /Idra/IdraPortal/target/IdraPortal.war /
COPY --from=build /wait-for-it/wait-for-it.sh /

RUN mv IdraPortal.war /usr/local/tomcat/webapps && mv Idra.war /usr/local/tomcat/webapps
RUN chmod +x wait-for-it.sh


# Set the port to expose. WARNING The "docker run" command, used to run a container from the image built from this DockerFile,
# MUST define the mapping with the host ports.
# (e.g. -p 8080:8080 option of "docker run" command maps the 8080 port of the container (exposed through the following EXPOSE directive) to the host's 8080 port )
EXPOSE ${IDRA_PORT:-8080}

# Set the working directory from which run the following commands
WORKDIR     /

# Start the Apache Tomcat server
CMD ["catalina.sh", "run"]