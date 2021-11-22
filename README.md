# SAP Commerce Cloud Toolkit
This CLI toolkit provides functionalities to create and monitor builds and deployments within the SAP Commerce Cloud.

## Setup
To setup your SAP CC CLI Toolkit please provide your:
- SAP Commerce Cloud Subscription Code
- SAP Commerce Cloud API token/key
- Define a maximum waiting time in minutes for builds (default: 30min)
- Define a maximum waiting time in minutes for deployments (default: 30min)
- Define polling/interval rate in seconds for builds (default: 5sec)
- Define polling/interval rate in seconds for deployments (default: 5sec)

using the file: **src/main/resources/application.properties**

```
toolkit.subscriptionCode = <insert your SAP CC subscription code here>
toolkit.apiKey = <insert your SAP CC API key here>

toolkit.build.maxWaitTime = <insert your maximum waiting time in minutes for builds here>
toolkit.build.sleepTime = <insert your polling/interval rate for build progress here>

toolkit.deploy.maxWaitTime = <insert your maximum waiting time in minutes for deployments here>
toolkit.deploy.sleepTime= <insert your polling/interval rate in seconds for build progress here>

toolkit.deploy.environment = <insert your preferred deploy environment d1,s1 here>
toolkit.deploy.dbUpdateMode = <insert your preferred database update mode for deployments here>
toolkit.deploy.strategy = <insert your preferred deployment strategy here>
```


## Usage
You can use this SAP CC CLI Toolkit by running following command:
``./gradlew bootRun --args='--toolkit.build=true,--toolkit.build.branch=develop,--toolkit.build.name=develop-20220101,--toolkit.deploy=true,--toolkit.deploy.system=d1,--toolkit.deploy.dbUpdateMode=UPDATE,--toolkit.deploy.strategy=ROLLING_UPDATE'``

### Supported command line arguments:
| Command Line | Allowed values | Description | Default Value |
| --- | --- | --- | --- |
| --toolkit.build |[true/false]|  Decide if build should be triggered | false |
| --toolkit.build.applicationCode | String | Name of the SAP Cloud application | \<empty> |
| --toolkit.build.branch | String | Name of the branch which should be build | develop |
| --toolkit.build.name | String | Name of the build | develop-\<yyyy-MM-dd> |
| --toolkit.deploy | [true/false] | Decide if deployment should be triggered | false |
| --toolkit.deploy.buildCode | String | If provided, this given build will be deployed. Otherwise the newly created build will be deployed | \<empty> |
| --toolkit.deploy.environment | d1, s1 | System environment which should be deployed | d1 |
| --toolkit.deploy.dbUpdateMode | UPDATE, NONE | Database update mode | NONE |
| --toolkit.deploy.strategy | ROLLING_UPDATE, RECREATE | Deployment strategy (with downtime or not) | ROLLING_UPDATE |


## Examples
### Create a new build without deployment
``./gradlew bootRun --args='--toolkit.build=true'``
### Create a new build and deploy this newly build
``./gradlew bootRun --args='--toolkit.build=true,--toolkit.deploy=true'``
### Start a new deployment for a given buildCode
``./gradlew bootRun --args='--toolkit.deploy=true,--toolkit.deploy.buildCode=20211119.6'``
