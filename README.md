# Flow Atomsphere API Service 

This native Boomi Flowservice provides direct access to the Boomi Atomsphere and API Manager APIs. This enables no-code flow applications for use cases such as DevOps and operational dashboards.

Direct access to over 80 Data objects and Actions is provided. For more information on Boomi APIs please refer to here:

Boomi Atomsphere API: https://help.boomi.com/bundle/integration/page/r-atm-AtomSphere_API.html
Boomi API Manager API: https://help.boomi.com/bundle/api_management/page/r-api-API_Management_APIs.html

The Boomi Atomsphere API Flow Service enables direct access to the Boomi Integration and Boomi API Manager API. 
Overview

The Boomi Atomsphere API provides direct access to API Objects and API Actions. This service makes it possible to build no-code Boomi DevOps workflows and operations dashboards for Boomi environments. For more information please refer to the API documentation:

## Features
The Boomi Atomsphere service forms part of the following features within flow:

### Database
This service feature allows you to connect to a dozens of Boomi object types and perform save, load and delete operations. Filters and sorting are supported. Examples include querying process execution history, creating a deployment on an Atom or querying the Boomi audit log for administrative changes. Note not all object fields are filterable. Using non-supported fields in condition will result in an error message when executing the flow. For more information regarding the capabilities of an API object, please refer to the help documentation specified in the Overview section of this document.

### Message
This service feature allows you to connect to Boomi API actions. Examples include actions for executing a process or canceling a process execution

### Identity
This service feature allows you to use Boomi Atomsphere as an identity system within flow. This supports building applications where users can log in using their Boomi Atomsphere username and password.

## Known limitations
* Unsupported API Object Types
 * API Manager Plan Usage object
* Unsupported API Actions
 * API Manager Add Plans to Deployments
 * API Manager Remove Plans from Deployments
 * API Manager Send Changes to Authentication Source
* Pagination limitations - The underlying API paginates rows 100 at a time. It uses “token” pagination whereas Flow only supports offset pagination. 
* Sorting limitations - The underlying API does not support sorting. When sorting is enabled, a maximum of 5000 rows are queried and then sorted before returning a page to Flow.
* Maximum return rows - The Data service allows returning a maximum 5000 rows. If your object contains more objects, implement filters to limit the returned data set.

# Installing the Atomsphere API Service
To use the Boomi Atomsphere API service, you will first need to install the service into your flow tenant.
Before you begin, Users of the service will need the appropriate privileges within the Boomi Platform. At minimum, the API Access privilege is required to use the service. For example, to query the Audit Log API object, the View Audit Logs is required.

For more information regarding Boomi user and permission management, please refer to: https://help.boomi.com/bundle/atomsphere_platform/page/r-atm-User_management.html

# Installing the Boomi Atomsphere API service
1. On the Home tab, select Services from the main menu.
1. Click Install Service.
1. Select the 'Boomi Atomsphere API' service from the Install Service drop-down menu.
1. Click Continue.
1. Enter a name for the service in the Name field, 'Boomi Atomsphere API service' for example.
1. Click Set Configuration Values.
1. Specify the Configuration values for your database configuration. See Boomi Atomsphere API Service Configuration Values for details on the required values for this service.
1. Click Continue.

If you have configured the service correctly, the Service Installed page is displayed. The Boomi Atomsphere API service is now installed.
Atomsphere API Service Configuration Values

Name | Type | Required | Description 
---- | ---- | -------- | -----------
Account | String | X | Specify the ID of your Boomi Atomsphere Account.
Use Identity Service Credentials | Boolean | X | Indicates whether to use the username and password entered when logging with the Atomsphere API Identity service. If the Identity service is not used, set this value to $False and specify the username and password in the fields below.
Username | String |  | Optional: If the Identity service is not used, you must set the Atomsphere API username. This can be a standard platform credential or an API Token. For more info regarding API tokens please refer to: https://help.boomi.com/bundle/integration/page/int-AtomSphere_API_and_Partner_API_authentication.html
Password | String |  | Optional: If the Identity service is not used, you must set the Atomsphere API password or API token
Server Public Certificate |  |  | Specify a URL Server Certificate for server verification.
Note: Using SSL and a Server Public Certificate is recommended best practice when implementing the SQL service, particularly if the connection between the SQL database and the SQL service is not in your private network.


## Contributing

Contributions are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to [CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This SDK is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
