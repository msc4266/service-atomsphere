Boomi Flow Atomsphere API Service
=====================

This service allows you to integrate your Flows with the Boomi Atomsphere API enabling you to build DevOps flows for your Boomi Integrate environment.

The Atomsphere API is documented here: https://help.boomi.com/bundle/integration/page/r-atm-AtomSphere_API.html

The service includes Data Actions for the Atomsphere API objects such as Atom, Execution Record and all others. Message Actions are also included for actions such as ExecuteProcess.

There is also an Identity server to enable logging into your Flow using Boomi platform Account ID, Username and Password.

In order to use this service, users need to have the appropriate Boomi platform permissions.

#### Building 

To build the service, you will need to have Apache Ant, Maven 3 and Java 8.

#### Running

The service is a Jersey JAX-RS application, that by default is run under the Grizzly2 server on port 8080 (if you use 
the packaged JAR).


## Contributing

Contribution are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
