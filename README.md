![](https://img.shields.io/maven-central/v/org.jprocesses/jProcesses.svg)
![](https://img.shields.io/github/license/profesorfalken/jProcesses.svg)
![](https://travis-ci.org/profesorfalken/jProcesses.svg)

# jProcesses
Get crossplatform processes details with Java

## Installation ##

To install jProcesses you can add the dependecy to your software project management tool: http://mvnrepository.com/artifact/org.jprocesses/jProcesses/1.6.4

For example, for Maven you have just to add to your pom.xml: 

      <dependency>
	         <groupId>org.jprocesses</groupId>
	         <artifactId>jProcesses</artifactId>
         	<version>1.6.4</version>
      </dependency>


Instead, you can direct download the JAR file and add it to your classpath. 
http://central.maven.org/maven2/org/jprocesses/jProcesses/1.6.4/jProcesses-1.6.4.jar

The only dependency you will need to add to the classpath is [WMI4Java](https://repo1.maven.org/maven2/com/profesorfalken/WMI4Java). You can download de JAR file [here](https://repo1.maven.org/maven2/com/profesorfalken/WMI4Java/1.6.1/WMI4Java-1.6.1.jar). 


## Basic Usage ##

#### Get processes details ####

```java
    List<ProcessInfo> processesList = JProcesses.getProcessList();
    
    for (final ProcessInfo processInfo : processesList) {
        System.out.println("Process PID: " + processInfo.getPid());
        System.out.println("Process Name: " + processInfo.getName());
        System.out.println("Process Time: " + processInfo.getTime());
        System.out.println("User: " + processInfo.getUser());
        System.out.println("Virtual Memory: " + processInfo.getVirtualMemory());
        System.out.println("Physical Memory: " + processInfo.getPhysicalMemory());
        System.out.println("CPU usage: " + processInfo.getCpuUsage());
        System.out.println("Start Time: " + processInfo.getStartTime());
        System.out.println("Priority: " + processInfo.getPriority());
        System.out.println("Full command: " + processInfo.getCommand());
        System.out.println("------------------");
    }
```

#### Kill process by PID ####

```java
    boolean success = JProcesses.killProcess(3844).isSuccess();
```

#### Change process Priority ####

Unix/Mac:

```java
    boolean ok = JProcesses.changePriority(3844, 5).isSuccess();
```

Windows:

```java
    boolean ok = JProcesses.changePriority(3844, WindowsPriority.HIGH).isSuccess();
```

## More info ##

Webpage: http://www.jprocesses.org


## Special thanks ##

@jkuharev: for his help to make jProcess work on Mac 

@Gobliins: for fixing executeCommand hang with lots of process using ProcessBuilder

@janhoy: for his contribution that fix long date parsing with locales different from english (Norwegian in his case)
