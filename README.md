![](https://img.shields.io/maven-central/v/org.jprocesses/jProcesses.svg)
[![CI](https://github.com/profesorfalken/jProcesses/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/profesorfalken/jProcesses/actions/workflows/build.yml)
[![Javadoc](https://javadoc.io/badge2/org.jprocesses/jProcesses/javadoc.svg)](https://javadoc.io/doc/org.jprocesses/jProcesses)
![](https://img.shields.io/badge/Java-8%2B-orange)
![](https://img.shields.io/github/license/profesorfalken/jProcesses.svg)

# jProcesses
Get crossplatform processes details with Java

## Requirements ##

* Java 8 or newer at runtime (the library is compiled to Java 8 bytecode).
* Windows, Linux or macOS.
* WMI4Java is the only compile dependency on Windows. When using Maven or
  Gradle it is resolved automatically; if you add the JAR manually, also add
  [WMI4Java](https://repo1.maven.org/maven2/com/profesorfalken/WMI4Java/1.7.0/WMI4Java-1.7.0.jar)
  and its `jPowerShell` 3.2.0 transitive dependency.

## Installation ##

To install jProcesses you can add the dependency to your software project
management tool: https://mvnrepository.com/artifact/org.jprocesses/jProcesses

For example, for Maven you have just to add to your pom.xml:

```xml
<dependency>
    <groupId>org.jprocesses</groupId>
    <artifactId>jProcesses</artifactId>
    <version>1.7.0</version>
</dependency>
```

Instead, you can directly download the JAR file and add it to your classpath:
https://repo1.maven.org/maven2/org/jprocesses/jProcesses/1.7.0/jProcesses-1.7.0.jar

## What's new in 1.7.0 ##

* Upgraded to WMI4Java 1.7.0 and jPowerShell 3.2.0.
* More robust parsing of `ps` and WMI output, including long values, multiple
  objects and platform-specific date formats.
* Correct Windows CPU-time normalization and cleanup of per-query data.
* PID validation: non-positive PIDs now throw `IllegalArgumentException`.
* Configurable command timeouts and output charset.
* `ProcessInfo` is now `Serializable` and
  `JProcesses.getProcess(pid, fastMode)` is available.
* Java 8, Central Portal publishing and Windows CI.

## Basic Usage ##

### Get processes details ###

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

### Search by name ###

```java
List<ProcessInfo> javaProcesses = JProcesses.getProcessList("java");
```

The name is matched exactly (case-insensitive on Windows). On Unix, `ps`
reports the executable name, so the value is usually the short name (`java`,
not the full path).

### Get a single process ###

```java
ProcessInfo process = JProcesses.getProcess(3844);
if (process != null) {
    System.out.println(process.getCommand());
}
```

`getProcess` returns `null` when the process does not exist.

### Kill process by PID ###

```java
boolean success = JProcesses.killProcess(3844).isSuccess();
```

PIDs must be positive. Passing `0` or a negative value throws
`IllegalArgumentException`; this is intentional because on POSIX systems
negative PIDs have special meanings (`kill -1` signals every process the user
is allowed to signal).

### Change process priority ###

Unix/Mac (nice value, from -20 to 19):

```java
boolean ok = JProcesses.changePriority(3844, 5).isSuccess();
```

Windows:

```java
boolean ok = JProcesses.changePriority(3844, WindowsPriority.HIGH).isSuccess();
```

### Fast mode ###

`JProcesses.get().fastMode().listProcesses()` skips the extra WMI calls used to
obtain the owner and CPU usage on Windows. On Unix it has no effect.

## Notes ##

* Memory values are reported in KiB. CPU usage on Unix is the average over the
  process lifetime (`ps %cpu`); on Windows it is the current percentage.
* `ProcessInfo.getExtraData()` exposes platform specific values, such as
  `start_datetime` when the platform provides it.
* `getProcess` returns `null` when the PID does not exist. PIDs must be
  positive; passing `0` or a negative value throws
  `IllegalArgumentException`.
* Name matching is exact and case-sensitive on Unix; on Windows it is
  case-insensitive. On Unix, `ps` reports the executable name (`java`, not the
  full path).
* In Windows fast mode the owner and CPU usage are not retrieved.
* System commands are destroyed if they do not finish in 30 seconds. The
  timeout can be changed with the `jprocesses.commandTimeoutMillis` system
  property. Command output is decoded with the platform default charset; set
  `jprocesses.charset` (for example `UTF-8`) if your `ps` output uses another
  encoding.
* If your `ps` output uses a non-English date format, configure it with
  `ProcessesUtils.setCustomDateFormat("dd MMM yyyy HH:mm:ss")` and
  `ProcessesUtils.setCustomLocale(new Locale("no"))`.

## Building ##

```bash
mvn verify
```

Building requires JDK 11 or newer (the compiler uses `--release 8`). The
resulting JAR targets Java 8 and is tested on recent JDKs. The Windows parser
tests use a fake WMI provider, so they can run on any OS; the real WMI
integration tests run in the `windows-latest` CI job.

## Roadmap ##

The next milestone is **1.8.0**. See [ROADMAP.md](ROADMAP.md) for the planned
parent/child process support, process-tree termination, fast-mode user data,
remote credentials and API cleanup.

## More info ##

Webpage: http://www.jprocesses.org

## Special thanks ##

@jkuharev: for his help to make jProcess work on Mac

@Gobliins: for fixing executeCommand hang with lots of process using ProcessBuilder

@janhoy: for his contribution that fix long date parsing with locales different from english (Norwegian in his case)
