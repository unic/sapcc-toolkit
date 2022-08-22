# SAP Commerce Cloud Toolkit
This CLI toolkit provides functionalities to create and monitor builds and deployments within the SAP Commerce Cloud.

## Setup

To set up your SAP CC CLI Toolkit please provide your:

- SAP Commerce Cloud Subscription Code*
- SAP Commerce Cloud API token/key*
- Define a maximum waiting time in minutes for builds (default: 30min)
- Define a maximum waiting time in minutes for deployments (default: 30min)
- Define polling/interval rate in seconds for builds (default: 5sec)
- Define polling/interval rate in seconds for deployments (default: 5sec)
- Enable notification service for MS Teams webhook (default: false)
- Define MS Teams webhook URL (default: empty)

*mandatory

using the file: `<workdir>/config/application.properties`

```properties
toolkit.subscriptionCode = <insert your SAP CC subscription code here>
toolkit.apiKey = <insert your SAP CC API key here>

toolkit.build.maxWaitTime = <insert your maximum waiting time in minutes for builds here>
toolkit.build.sleepTime = <insert your polling/interval rate for build progress here>

toolkit.deploy.maxWaitTime = <insert your maximum waiting time in minutes for deployments here>
toolkit.deploy.sleepTime= <insert your polling/interval rate in seconds for build progress here>

toolkit.apiConnectTimeout = <insert your maximum connect timeout for all API connections>
toolkit.apiReadTimeout = <insert your maximum read timeout for all API connections>

notification.teams.enabled = true/false
notification.teams.webhook.url = <insert your teams webhook url>
```

## Building

Please use the default Spring boot gradle tasks to build this application:

```shell
./gradlew bootJar
```


## Usage
You can list all current flags and parameters by running the jar with the --help flag:
```shell
java -jar build/libs/sapcc-toolkit-*.jar --help

[...]

usage: sapcc-toolkit
 -a,--applicationcode <arg>   application code
 -b,--build                   Execute build
 -c,--buildcode <arg>         Code of build to deploy
 -d,--deploy                  Execute deployment
 -e,--environment <arg>       environment for deployment
 -h,--help                    print usage help
 -n,--name <arg>              build name
 -p,--pidfile <arg>           process id file
 -r,--branch <arg>            branch to be build
 -s,--strategy <arg>          deployment strategy
 -u,--updatemode <arg>        database update mode for deployment
 -y,--async                   Don't monitor progress
 -t,--skipBuildTimeouts       Skip build timeouts during build progress monitoring
```

## Examples

### Create a new build of the develop branch (without deployment)

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build
```

### Create a new build of the develop branch but don't wait for it to finish

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build --async
```

### Create a new release build without deployment

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build  --branch release/1.6.0  --name release-1.6.0
```

### Create a new develop (default) build and deploy this newly build on d1 without URS

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build  --deploy
```

### Create a new develop (default) build and deploy this newly build on d1 without URS and skip build timeouts

```shell
java -jar build/libs/sapcc-toolkit-*.jar --build  --deploy  --skipBuildTimeouts
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
