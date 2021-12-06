# SAP Commerce Cloud Toolkit
This CLI toolkit provides functionalities to create and monitor builds and deployments within the SAP Commerce Cloud.

## Setup
To set up your SAP CC CLI Toolkit please provide your:
- SAP Commerce Cloud Subscription Code
- SAP Commerce Cloud API token/key
- Define a maximum waiting time in minutes for builds (default: 30min)
- Define a maximum waiting time in minutes for deployments (default: 30min)
- Define polling/interval rate in seconds for builds (default: 5sec)
- Define polling/interval rate in seconds for deployments (default: 5sec)

using the file: **src/main/resources/application.properties**

```properties
toolkit.subscriptionCode = <insert your SAP CC subscription code here>
toolkit.apiKey = <insert your SAP CC API key here>

toolkit.build.maxWaitTime = <insert your maximum waiting time in minutes for builds here>
toolkit.build.sleepTime = <insert your polling/interval rate for build progress here>

toolkit.deploy.maxWaitTime = <insert your maximum waiting time in minutes for deployments here>
toolkit.deploy.sleepTime= <insert your polling/interval rate in seconds for build progress here>
```

## Building

Please us ethe default Spring boot gradle tasks to build this application:

```shell
./gradlew bootJar
```


## Usage
You can use this SAP CC CLI Toolkit by running following command (example):
```shell
java -jar build/libs/sapcc-toolkit-*.jar \
  --build \
  --branch develop \
  --name develop-20220101 \
  --deploy \
  --environment d1 \
  --updatemode UPDATE \
  --strategy ROLLING_UPDATE
```

Run `java -jar build/libs/sapcc-toolkit-*.jar -h` to print a usage explanation

## Examples

### Create a new build without deployment

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build
```
### Create a new release build without deployment

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build  --branch release/1.6.0  --name release-1.6.0
```

### Create a new develop (default) build and deploy this newly build on d1 without URS

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build  --deploy
```

### Start a new rolling deployment for a given build on d1 without URS

```shell
java -jar build/libs/sapcc-toolkit-*.jar --deploy  --buildcode 20211122.1
```

### Start a new rolling deployment for a given build on s1 with URS

```shell
java -jar build/libs/sapcc-toolkit-*.jar --deploy  --buildcode 20211122.1 --updatemode UPDATE --environment s1
```

## Publication

### Prerequisites
For publishing artifacts we use the [maven-publish](https://docs.gradle.org/current/userguide/publishing_maven.html) plugin. Since the Unic nexus repository is defined unter the name 'unicEcom' you need to set the variables `unicEcomUsername` and `unicEcomPassword`. This can be done in your `~/.gradle/gradle.properties` for example.

### Upload current publication

```./gradlew publish```
