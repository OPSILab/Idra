# Introduction

This document describes how to install and launch the Idra platform very easily
using [Docker](https://www.docker.com/). There are several ways to accomplish
this:

1. **Docker-Compose**: Use docker-compose to the whole stack as several
   containers, including Idra itself, MySQL and RDF4J.
2. **Docker container**: Use docker image to run only the Idra container.
   Provide MySQL and RDF4J separately.

Idra [GitHub](https://github.com/OPSILab/Idra.git) repository provides both
**DockerFile** and **docker-compose** files, in order to use both methods.
However, the recommended way is to use Docker Compose, which allows you to start
an Idra container, based on the official
[Docker Hub image](https://hub.docker.com/r/idraopendata/idra/).

## Prerequisites

First method requires to install **Docker Compose**
([see the guide](https://docs.docker.com/compose/install/#install-compose)).

Second method requires to install only **Docker Engine**
([see the guide](https://docs.docker.com/install/)).

## Using Docker Compose

Docker Compose allows to run and link an Idra container (running on a Tomcat
Alpine container) to MySQL and RDF4J containers very easily. In order to
accomplish this, execute the following steps.

-   Open a command prompt and execute the following command to clone the source
    code from the Idra [GitHub](https://github.com/OPSILab/Idra.git) repository:

```bash
git clone https://github.com/OPSILab/Idra.git
```

Move into **`Idra/docker`** folder:

```bash
cd Idra/docker
```

In this folder there is the **`docker-compose.yml`**; before launching it, we
have to configure both networking and environment variables properly, as
described below.

-   (ONLY AFTER COMPLETING FOLLOWING SECTION) Run the docker-compose file with:

```bash
docker-compose up
```

The containers will be automatically started and attached to the created
`idra_main` network (see below).

---

## Deployment & configuration

### Docker networking

Idra and its related containers (MySQL and RDF4j) will run in the same network,
that MUST be created before running the platform. The containers will be
attached to this network and each one will have its own assigned IP and
hostname, internal to the network. In particular, the container hostname will be
equal to the name given in the “services” section of docker-compose file. Thus,
each container can look up the hostname of the others.

-   Create the **`idra_main`** network by typing the following command:

```bash
docker network create idra_main
```

You can check the created network, where all the containers will be attached to,
with:

```bash
docker network ls
```

Once the application was started, you can check IPs assigned to running
containers, with:

```bash
docker inspect network idra_main.
```

**NOTE**

As the network is a bridge, each port exposed by containers (e.g. 8080), will be
mapped and also reachable in the machine where Docker was installed. Thus, if
the machine is publicly and directly exposed, also these ports will be
reachable, unless they were closed.

### Configuration through environment variables

The configuration through properties file, as done in the
[WAR packaging installation](install_war.md#configuration), should be overridden
by changing the corresponding environment variables, in the environment section
of provided _`docker-compose.yml`_ file. In this way, docker-compose will inject
in the Idra container the environment variables declared in its environment
section, which will override the default values in corresponding properties
files.

For instance, the **`idra.db.username`** property can be set, with the same
name, with:

```
idra.db.user=root
```

### Database creation

Idra relies on a MySQL database to store all the application data and collected
Open Datasets.

The compose defines a dedicated container, with specific mounted volumes:

```yaml
volumes:
	- ./opt/idra/mysql-data:/var/lib/mysql
	- ./idra_db.sql:/docker-entrypoint-initdb.d/idra_db.sql
```

The volumes define the following:

-   **DB Data folder**: the host folder `./opt/idra/mysql-data` will contain DB
    data, even if the container will be destroyed. (If the folder does not
    exists, a new one will be created under the root of the cloned folder).
-   **Database initialization script**: the SQL script, provided in the
    repository folder, that will create and initialize the `idra_db` DB.
    (Generally, do not touch this.) In addition, it creates an administration
    user with the following credentials:

        	- **`username: admin`**
        	- **`password: admin`**

**Note**. To change the administrator password login in the Idra Portal with the
previous credentials then go to the **Administration -> Manage Configurations ->
Update Password** section.

### RDF repository creation

Idra relies in a RDF Triple Store (RDF4J) to store all the RDF retrieved from
federated ODMS catalogues. The compose defines a dedicated container, with
specific mounted volumes:

```yaml
volumes:
    - ./opt/idra/rdf4j-data:/opt/rdf4j/data
```

The volume defines the following:

-   **RDF4J Data folder**: the host folder `./opt/idra/rdf4j-data` will contain
    the RDF4J data, even if the container will be destroyed. If the folder does
    not exists, a new one will be created under the root of the cloned folder).

Unlike the MySQL case, currently, you have to manually create a new RDF4J
repository:

-   Once the Idra server started (after launching docker-compose), go with
    browser to the RDF4J URL: `localhost:8080/rdf4j-workbench`

Note. Change the port number according to the configuration done (if changed) in
the `ports` section of docker-compose file.

-   Through the RDF4J GUI, select **“new repository”** on the left menu, then
    create a new repository of type `Native Java Store` called `Idra`.

### Applying configuration

**Start it up**

Once all the environment configurations are done, we can run:

```bash
docker-compose up
```

As a result of this command, there is Idra listening on port 8080 (default in
ports section of docker-compose.yml) on localhost.

---

## Using Docker standalone container

This method use a Docker image to run only a container running Idra platform. It
is up to you to provide MySQL and RDF4J instances. These may be running on
localhost, other host on your network, another container, or anywhere you have
network access to. Link them accordingly, see the Docker
[link](https://docs.docker.com/network/links/) and
[bridge networks](https://docs.docker.com/network/bridge/) guides.

The Idra Docker image can be:

-   pulled from official
    [Docker Hub](https://hub.docker.com/r/idraopendata/idra/) :

```bash
docker pull idraopendata/idra
```

-   manually built with (go to `/Idra/docker` folder):

```bash
docker build -t idraopendata/idra .
```

Once the Idra image was pulled or created, launch a container based on it:

```bash
docker run idraopendata/idra
```

**Note.** In order to see how to override environment variables and then to
configure Idra according to
[Configuration Section](#configuration-through-environment-variables), see the
[Docker run reference](https://docs.docker.com/engine/reference/run/#overriding-dockerfile-image-defaults).
