# Project to learn spring cloud features

# Build
To build the project go to `project.basedir` and execute this command:
```bash
mvn clean install

```

## Steps to start the environment
1) start eureka-server
2) start configuration-server
3) start people
4) start aggregator
5) start gateway


## Test 
```bash

curl http://localhost:8080/people/peoples/1234
curl http://localhost:8080/aggregator/agg/peoples/age/1234

#the below curl should throw an error
curl http://localhost:8080/foos/asdf


```

