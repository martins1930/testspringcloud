# Project to learn spring cloud features

# Start postgres server
```bash
cd infrastructure/database
bash build-postgres-image.bash
bash postgres-run.bash
```

# Build
To build the project go to `project.basedir` and execute this command:
```bash
mvn clean install

```

# Build docker image for eureka-server
After a successful maven build execute:
```bash
cd eureka-server
docker build -t martins1930/eureka-server .
```

# Run the eureka-server with docker
```bash
docker run -e SPRING_PROFILES_ACTIVE=docker --name eureka-server -v $PWD:/var/log/eureka-server martins1930/eureka-server 
```

## Steps to start the environment (don't use docker to start eureka-server)
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

