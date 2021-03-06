EPTS Reports Module
==========================

Description
-----------
PEPFAR reports for EPTS project in Mozambique

Prerequisites
-------------

1. [Install Java and Maven](https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation)
2. Install git
   - `sudo apt-get install git`
3. To setup the OpenMRS SDK, you just need to open up a terminal/console and enter:
   - `mvn org.openmrs.maven.plugins:openmrs-sdk-maven-plugin:setup-sdk`

Building from Source
--------------------
You will need to have Java 1.6+ and Maven 2.x+ installed.  Use the command 'mvn package' to
compile and package the module.  The .omod file will be in the omod/target folder.

Alternatively you can add the snippet provided in the [Creating Modules](https://wiki.openmrs.org/x/cAEr) page to your
omod/pom.xml and use the mvn command:

    mvn package -P deploy-web -D deploy.path="../../openmrs-1.8.x/webapp/src/main/webapp"

It will allow you to deploy any changes to your web
resources such as jsp or js files without re-installing the module. The deploy path says
where OpenMRS is deployed.

Installation
------------
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

If uploads are not allowed from the web (changable via a runtime property), you can drop the omod
into the ~/.OpenMRS/modules folder.  (Where ~/.OpenMRS is assumed to be the Application
Data Directory that the running openmrs is currently using.)  After putting the file in there
simply restart OpenMRS/tomcat and the module will be loaded and started.

Reports Implemented
-------------------

## MER Reports

|Report             |Indicators Used                                  |
|-------------------|-------------------------------------------------|
|*MER_Quarterly*    |TX_CURR, TX_NEW, TX_PVLS                         |

## MER Indicators

|Indicators |Description
|-----------|-----------
|*TX_PVLS*  |Percentage of ART patients with a viral load result documented in the medical record and/or laboratory information systems (LIS) within the past 12 months with a suppressed viral load (<1000 copies/ml).
|*TX_CURR*  |Number of adults and children currently receiving antiretroviral therapy (ART).
|*TX_NEW*   |Number of adults and children newly enrolled on antiretroviral therapy (ART).
