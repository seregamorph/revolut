Test task
=================

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between
internal users/accounts.

Explicit requirements:
* keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is invoked by another
internal system/service)
* use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the requirement #1
* the datastore should run in-memory for the sake of this test
* the final result should be executable as a standalone Java program
* demonstrate with tests that the API works as expected

Implicit requirements:
* the code produced by you is expected to be of good quality
* there are no detailed requirements, use common sense

Please put your work on github or bitbucket.

Key issues
------------
SINAP is a recursive acronym that means "SINAP is not a processing".
This name is not my idea, one of a QIWI hackathon projects was called so.

* Simple, reliable, explicit
* Fast
* self-documenting source code
* Prevent payment duplicates
* diagnostics
* minimize dependencies
* immutable pattern is preffered

SINAP is like an ACID database. That means, there is no dirty reads, either api client sees condition before transaction,
either after. So, transaction has only single final status: SUCCESS (ALREADY_SUCCESS for repeated request), any other
trouble (like not enough balance) should rollback any changes if it has place. There is no intermediate payment status.
After any failure the payment request can be repeated with the same id. E.g., awaiting for balance (of course, there
must be some time intervals between tries).

By design, it is client responsibilty to:
* request one transaction always with the same transactionId
* retry payment requests until get one of ResponseCode

Technology stack
------------
I'd prefer to implement task on jetty-embedded/jetty-standalone/tomcat + spring-di + spring-webmvc +
spring-web(RestTemplate).
But condition says: no spring, standalone.
The chosen technologies:
* Java 8
* Netty + basic http server
* Apache http client (for tests)
* Jackson
* Maven

The class structure got not so complicated, I desided not to use alternate DI (like Guice).

Project structure
------------
* sinap-api is a module with json declarations of the protocol. It can be exported to a public project for
integration purposes.
* sinap-netty is a basic http server with lifecycle and helpers
* sinap-payment is a dummy in-memory processing core
* sinap-server is a bootstrap and distribution module

Protocol
------------
External service protocol receives major units amount (e.g. ("12.34", USD) means 12 dollar 34 cent).
Internal amount is stored as minor units (fraction digits of currency should be strictly immutable since declaration).
E.g. (1234, USD) means 12 dollar 34 cent.
Payment id is of UUID format. This is a for horizontal scaling simplification (in future), data analyze (split data
to distributed nodes), etc. purposes.
At the same time it uses central-based ideology of accounts with integer ids and locking.

DummyAccountStorage
------------
DummyAccountStorage simulates acid database with transaction, as described in "Key issues" section.

Project building
------------
```mvn clean package```

Project running
------------
```java -Dorg.slf4j.simpleLogger.defaultLogLevel=TRACE -jar revolut-sinap-server.jar```

TODO list
------------
* config
* netty buffers pooling allocators
* two-phase payments (hold + withdraw)
* Currency convertation
* support wildcards instead of regex/pattern in DispatchHttpHandlerBuilder
* Money class (Currency, amount). Martin Fowler

Conclusion
------------
I tried to make it simple, but sometimes it looks a little bit overegineered. Just wanted to show some tricks and
experience. The concurrency optimizations in DummyAccountStorage are not so effective, because logic inside of synch-blocks
is too trivial and non-blocking.
The target jar with dependencies is only 3.3 MB.
