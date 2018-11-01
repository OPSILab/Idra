# Sanity Checks

In order to apply the previous changes, restart the Tomcat server. The Sanity
Checks are the steps that the Administrator will take to verify that the
installation is ready to be used and tested.

**Note**. Change the _`BASEPATH`_ value with the actual host and port where is
exposed the runtime environment (Tomcat).

## Catalogue Access Testing

Once the server restarted, go with browser to _`http://BASEPATH/IdraPortal`_

When the home page is showed, perform the following steps:

-   Check that the message "There are no federated catalogues" is showed.
-   Check that you can perform the Login as Administrator, in the appropriate
    section in the top bar.

## Platform API testing

-   Open a command prompt and execute:

```bash
curl http://BASEPATH/Idra/api/v1/administration/version
```

-   Check that you get the version number as output, along with other
    information about platform version and release timestamp
