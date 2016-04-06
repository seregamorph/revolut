Test task
=================

Design and implement a RESTful API (including data model and the backing implementation) for money transfers
between internal users/accounts.
Explicit requirements:

* keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is invoked by another internal system/service)
* use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the requirement #1
* the datastore should run in-memory for the sake of this test
* the final result should be executable as a standalone Java program
* demonstrate with tests that the API works as expected

Implicit requirements:
* the code produced by you is expected to be of good quality.
* there are no detailed requirements, use common sense.

Please put your work on github or bitbucket.

Key issues
------------
sinap is a recursive acronym that means "SINAP is not a processing". This name is not my idea, one of a QIWI hackathon projects was called so.

* Simplicity, reliablility, explicitness.
* Prevent payment duplicates
* diagnostics
* externability
* stateless (REST)

Technology stack
------------
I'd prefer to implement task on jetty-embedded/jetty-standalone/tomcat + spring-di + spring-webmvc + spring-web(RestTemplate).
But condition says: no spring, standalone.
The chosen technologies:
* Netty + basic http server
* Guice
* Apache http client (for test)
* Jackson

Project structure
------------
* sinap-api is a module with json declarations of the protocol. It can be exported to a public project for integration purposes.
* sinap-netty is a basic http server with lifecycle and helpers
* sinap-payment is a dummy in-memory processing core
* sinap-server is distribution module

TODO list
------------
* maven wrapper
