![](https://img.shields.io/maven-central/v/org.jprocesses/jProcesses.svg)
![](https://img.shields.io/github/license/profesorfalken/jProcesses.svg)

# jProcesses
Get crossplatform processes details with Java

## Installation ##

To install jProcesses you can add the dependecy to your software project management tool: http://mvnrepository.com/artifact/org.jprocesses/jProcesses/0.2

For example, for Maven you have just to add to your pom.xml: 

      <dependency>
	         <groupId>org.jprocesses</groupId>
	         <artifactId>jProcesses</artifactId>
         	<version>0.2</version>
      </dependency>


Instead, you can direct download the JAR file and add it to your classpath. 
http://central.maven.org/maven2/org/jprocesses/jProcesses/0.2/jProcesses-0.2.jar

## Basic Usage ##

#### Get processes details ####

```java
    List<ProcessesInfo> processesList = JProcesses.getProcessList();
    
    for (final ProcessesInfo processInfo : processesList) {
        System.out.println("Process PID: " + processInfo.getPid());
        System.out.println("Process Name: " + processInfo.getName());
        System.out.println("Process Time: " + processInfo.getTime());
        System.out.println("------------------");
    }
```

## More info ##

Webpage: http://www.jprocesses.org
