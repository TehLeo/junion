## Install/Usage Overview

**You can use JUnion:**

* as source translator: junionc
* as a compiler plugin for javac 1.8
* as Eclipse plugin, in Netbeans
* as part of Ant, Maven, Gradle build

#### Source Translator

You can compile java source with junion with the following command:

```
java -jar junionc1.0.jar -classpath lib/junion1.0.jar -version 1.8 -sourcepath src -outputpath out
```
where:
* -classpath - javac classpath eg: lib1.jar\:lib2.jar
* -version - source version string, eg 1.8, 9, 10
* -sourcepath - source folder(s), eg: src, or src1\:src2
* -outputpath - folder to output compiled java source

#### Javac plugin

You can compile JUnion project directly with javac 1.8, by adding a command line argument: -Xplugin\:junion. Make sure all the referenced libraries are on the classpath.

#### Eclipse plugin

To use JUnion within Eclipse IDE, follow the steps:
1. Open Eclipse
2. Help -> Install New Software
3. Enter eclipse update site: https://tehleo.github.io/junion/update
4. Select JUnion plugin and continue with the installation.

1. Create new Java Project or Open existing one
2. Add project runtime library: junion\<version\>.jar
3. Create new file in the project named ".junion"
4. In .junion property file, set property compileLibs= path to junionc\<version\>.jar
5. Instead of step 4. you can add library junionc\<version\>.jar directly to your classpath. However it is not a runtime dependency.
6. Save the file, this should automatically create the folder ".generated_src_junion" (If you do not see files beginning with a dot, your package explorer has a filter enabled)
7. In project properties **-&gt; Java Build Path -&gt; Sources -&gt; Add Folder** and add **.generated_src_junion**
8. **Window -&gt; Preferences -&gt; Java -&gt; Compiler -&gt; Error/Warnings -&gt; Deprecated or Restricted API** Set **Forbidden Reference** to **Warning**.

Alternatively watch the Eclipse Setup Tutorial:

[![Eclipse Installation Tutorial](http://img.youtube.com/vi/e-W-d016g3Y/0.jpg)](http://www.youtube.com/watch?v=e-W-d016g3Y "Eclipse Installation Tutorial")

#### Netbeans Support

To use JUnion within Netbean IDE, follow the steps:
1. Add all the libraries to the project.
2. **Project Properties -&gt; Build -&gt; Compiling -&gt; Disable Compile on Save** and enter "-Xplugin\:junion" in **Additional Compiler Options** 

#### Gradle

Gradle build file:
```
apply plugin: 'java'

repositories {
    mavenCentral()
}
configurations {
    junion
}
dependencies {
    compile 'com.github.tehleo:junion:1.1.1'
    junion 'com.github.tehleo:junionc:1.1.1'
}
task junionTask(type: JavaExec) {
    classpath configurations.junion
    classpath += sourceSets.main.runtimeClasspath
    main = 'theleo.jstruct.plugin.SourceCompiler'
    args = ['-classpath', sourceSets.main.runtimeClasspath.getAsPath(),
            '-version', '1.8',
            '-sourcepath', files(sourceSets.main.java.srcDirs).getAsPath(),
            '-outputpath', file('build/generated').getPath()
    ]
   	sourceSets.main.java.srcDirs = ['build/generated']
}

build.dependsOn junionTask
```

#### Maven

Maven build file:
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>

	<groupId>tehleo.maventest</groupId>
	<artifactId>test</artifactId>
	<version>1.0-SNAPSHOT</version>
	<packaging>jar</packaging>

	<properties>
		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	 </properties>

 	 <name>Hello Structs</name>
 
	<dependencies>
		<dependency>
			<groupId>com.github.tehleo</groupId>
			<artifactId>junion</artifactId>					
			<version>1.1.1</version>
		</dependency>
	</dependencies>

	<build>
		<sourceDirectory>${basedir}/target/generated-sources</sourceDirectory>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-dependency-plugin</artifactId>
				<version>3.1.0</version>
				<executions>
					<execution>
						<id>build-classpath</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>build-classpath</goal>
						</goals>
						<configuration>
							<outputProperty>classpath-string</outputProperty>
						</configuration>
					</execution>
				</executions>
			</plugin>

			<plugin>
			<groupId>org.codehaus.mojo</groupId>
			<artifactId>exec-maven-plugin</artifactId>
			<version>1.6.0</version>
			<executions>
				<execution>
				<phase>generate-sources</phase>
				<goals>
					<goal>java</goal>
				</goals>
				</execution>
			</executions>
			<configuration>
				<includePluginDependencies>true</includePluginDependencies>
				<mainClass>theleo.jstruct.plugin.SourceCompiler</mainClass>
				<sourceRoot>${basedir}/target/generated-sources</sourceRoot>
				<arguments>
					<argument>-noSystemExitOnSuccess</argument>
					<argument>-classpath</argument>
					<argument>${classpath-string}</argument>
					<argument>-version</argument>
					<argument>1.8</argument>
					<argument>-sourcepath</argument>
					<argument>${basedir}/src/main</argument>
					<argument>-outputpath</argument>
					<argument>${basedir}/target/generated-sources</argument>
				</arguments>
			</configuration>
			<dependencies>
				<dependency>
				<groupId>com.github.tehleo</groupId>
				<artifactId>junionc</artifactId>
				<version>1.1.1</version>
				</dependency>
			</dependencies>
			</plugin>
		</plugins>
	</build>
</project>
```


