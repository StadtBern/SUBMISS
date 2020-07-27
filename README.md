# Submiss

e-Procurement system for managing tenders

## Getting Started

Submiss is a web-based software solution of the Procurement Office (City of Bern) for recording, organizing and processing all procurement procedures of the City of Bern.
It offers role-based access to the project and company data for internal employees, as well as access to the company part by external third parties.

Submiss includes the following:
* Digital execution (step by step) of the tender procedures: Open, Selective, Invitation, Negotiation and Competition
* Documentation of the process steps
* Generation and printing of all process relevant documents and contracts
* Company directory with master data and proof management
* Access to company data for third parties
* Generation of various reports
* Task list and audit logs
* Master data and user administration

## Installation

### Prerequisites

* JDK 1.8.x
* Maven 3.x
* Karaf 4.0.9
* Node.js 4.4.7 LTS
* Gulp 3.9.0
* Bower
* MariaDB 10.x

###Development Environment Setup
This section provides instructions on how to setup your development environment.

#### Security Integration
Integration with a SAML identity provider is necessary. OpenAM is used as example for SAML Idp. 
Installation and Configuration of the Identity Provider is not content of this readme.

Create a file `c:\etc\submiss\submiss-sp.properties` with the following content:
> Make sure you replace `nsurname` with the first letter of your name and
your surname.

```
org.apache.ws.security.crypto.merlin.keystore.type=jks
org.apache.ws.security.crypto.merlin.keystore.password=YOUR_PASSWORD
org.apache.ws.security.crypto.merlin.keystore.alias=submiss_nsurname
org.apache.ws.security.crypto.merlin.keystore.file=c:/etc/submiss/keystore.jks
```

Open a CLI to `c:\etc\submiss` and create your keystore:
> 1. Make sure you replace `nsurname` with the first letter of your name and
your surname.
> 2. Use 'YOUR_PASSWORD' as your keystore password when requested.
> 3. When requested for 'first and last name' enter your real ones.
> 4. When requested for 'name of organisational unit' enter `submiss`.

```
keytool -genkey -alias submiss_nsurname -keyalg RSA -keysize 2048 -keystore keystore.jks
```

Export your certificate:
> Make sure you replace `nsurname` with the first letter of your name and
your surname.

```
keytool -export -keystore keystore.jks -alias submiss_nsurname -file submiss_nsurname.cer
```

Import your certificate `.cer` file into OpenAM's keystore.

Import the certificate of the IdP into your keystore:

1. Copy the file to `c:\etc\submiss`.
2. Open a CLI to `c:\etc\submiss` and execute:

```
keytool -import -file cert.pem -alias idm-int -keystore keystore.jks
```

#### Installation on Karaf
In case your `settings.xml` is not in one of the standard locations
(e.g. `${user.home}/.m2/settings.xml`) then you need to edit
`org.ops4j.pax.url.mvn.cfg` in line 37 to add your path for your
Maven `settings.xml` file.

##### Configure the managed datasource
```
  config:edit org.ops4j.datasource-managed
  config:property-set user submiss
  config:property-set password YOUR_PASSWORD
  config:property-set url jdbc:mariadb://DATABASE_SERVER:PORT/YOUR_DATABASE_NAME?autoReconnect=true
  config:property-set dataSourceName qlack2-ds
  config:property-set osgi.jdbc.driver.name mariadb
  config:property-set pool dbcp2
  config:property-set xa true
  config:property-set jdbc.pool.testOnBorrow true
  config:property-set jdbc.factory.validationQuery 'select 1'
  config:property-set jdbc.pool.testWhileIdle true
  config:property-set jdbc.factory.validationQueryTimeout 15
  config:update
```

##### Configure the non-managed datasource
```
config:edit org.ops4j.datasource-nonmanaged
config:property-set user submiss
config:property-set password YOUR_PASSWORD
config:property-set url jdbc:mariadb://DATABASE_SERVER:PORT/YOUR_DATABASE_NAME?autoReconnect=true
config:property-set dataSourceName qlack2-ds-non-managed
config:property-set osgi.jdbc.driver.name mariadb
config:property-set pool dbcp2
config:property-set jdbc.pool.testOnBorrow true
config:property-set jdbc.factory.validationQuery 'select 1'
config:property-set jdbc.pool.testWhileIdle true
config:property-set jdbc.factory.validationQueryTimeout 15
config:update
```
##### Configure Liquibase to use the SUBMISS datasource
```
config:edit com.eurodyn.qlack2.util.liquibase
config:property-set datasource qlack2-ds-non-managed
config:update
```

##### Configure Cache
```
config:edit com.eurodyn.qlack2.fuse.caching
config:property-set active true
config:property-set maxEntries 0
config:property-set expiryTime 1800000
config:update
```

##### Configure API
```
config:edit ch.bern.submiss.web.api
config:property-set sso.signaturePropertiesFile /c:/etc/submiss/submiss-sp.properties
config:property-set sso.idpServiceAddress http://IDP_NAME:IDP_PORT/openam1300/SSOPOST/metaAlias/idp
config:property-set sso.serviceAddress submiss_nsurname_gulp
config:property-set sso.logoutServiceAddress http://HOSTNAME:PORT/api/sso/logout
config:property-set sso.assertionConsumerServiceAddress http://HOSTNAME:PORT/api/sso/racs
config:property-set sso.skipSignatureInMetadata true
config:property-set sso.signatureUsername submiss_nsurname
config:update
```

##### Disable transaction recovery
Transaction recovery is not supported by MariaDB's JDBC drivers, so it
should be turned off.
```
config:edit org.apache.aries.transaction
config:property-set aries.transaction.recoverable false
config:update
```

#### Quick Build

```
mvn clean install -DskipTests
```

#### Install Submiss Karaf features repository
	feature:repo-add mvn:ch.bern.submiss/submiss-karaf-features/LATEST/xml/features

#### Install DB connectivity feature and automatic Liquibase update
	feature:install submiss-database

#### Install dependencies
	feature:install submiss-deps
	
#### Install Document related dependencies (Jasper, Apache Poi, Aspose) 
	feature:install submiss-docs

#### Install the back-end feature (without the static web content)
	feature:install submiss-back-end
	
#### Install additional bundles
    bundle:install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.xalan/2.7.1_4
    bundle:install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.bcel/5.2_4

#### Running and developing for the front-end

    # Goto the SUBMISS web-ui folder
    cd submiss-web-ui

    # Get up to date with dependencies
    npm install
    bower install

    # Start the embedded live-reload web server
    gulp clean & gulp serve --proxy=http://YOUR_LAST_NAME:8181

    
### Production Environment Setup
This section provides instructions on how to setup your production environment.

Create a new folder. e.g. etc/submiss in order to create same configuration files with same properties as previously described under the Development Environment Setup.    
Finally, following files should be present:

 - submiss-sp.properties
 - ch.bern.submiss.web.api
 - com.eurodyn.qlack2.fuse.caching
 - com.eurodyn.qlack2.util.liquibase
 - org.ops4j.datasource-nonmanaged
 - org.ops4j.datasource-managed


To build the project under your production environment, go to the root of the application and execute::
```
mvn clean install -DskipTests
```

Under submiss-dist/target you will find the Application as .tar.gz-File, that includes Karaf, so setting it up is quick and easy.

* Create a folder in which the file should be extracted
```
sudo mkdir submiss
```

* Extract the file 
```
tar zxvf submiss-dist-1.0.0.tar.gz -C submiss --strip 1
```

* Navigate to submiss/bin and execute:
```
./client
```
This command will start Karaf.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Quick Build

* JDK 1.8.x
* Maven 3.x

```
mvn clean install -DskipTests
```

## Contributing Guidelines

Please read the [contributing](CONTRIBUTING.md) file for the process of submitting pull requests to us.

## Code of Conduct

One healthy social atmosphere is very important to us, wherefore we rate our Code of Conduct high.
 For details check the [Code of Conduct](CODE_OF_CONDUCT.md) file.

## Authors

* **European Dynamics S.A.** - *Initial work* - [eurodyn](https://github.com/eurodyn)

See also the list of [contributors](https://github.com/StadtBern/SUBMISS/graphs/contributors)
 who participated in this project.

## License

This project is licensed under the European Union Public Licence - see the [license](LICENSE.md) file for details.