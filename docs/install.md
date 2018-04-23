## Install/Usage Overview

**You can use JUnion:**

* as source translator: junionc
* as a compiler plugin for javac 1.8
* as Eclipse plugin

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
1. Install JUnion plugin from: https://tehleo.github.io/junion/update
2. Create new Java Project or Open existing one
3. Add project runtime library: junion\<version\>.jar
4. Create new file in the project named ".junion"
5. In .junion property file, set property compileLibs= path to junionc\<version\>.jar
6. Instead of step 5. you can add library junionc\<version\>.jar directly to your classpath. However it is not a runtime dependency.
7. Save the file, this should automatically create the folder ".generated_src_junion" (If you do not see files beginning with a dot, your package explorer has a filter enabled)
8. In project properties **-&gt; Java Build Path -&gt; Sources -&gt; Add Folder** and add **.generated_src_junion**
9. **Window -&gt; Preferences -&gt; Java -&gt; Compiler -&gt; Error/Warnings -&gt; Deprecated or Restricted API** Set **Forbidden Reference** to **Warning**.

Alternatively watch the Eclipse Setup Tutorial:

#### Netbeans Support

To use JUnion within Netbean IDE, follow the steps:
1. Add all the libraries to the project.
2. **Project Properties -&gt; Build -&gt; Compiling -&gt; Disable Compile on Save** and enter "-Xplugin\:junion" in **Additional Compiler Options** 

#### Ant support and other build scripts

You can use the source compiler to compile your sources.

```
java -jar junionc1.0.jar -classpath lib/junion1.0.jar -version 1.8 -sourcepath src -outputpath out
```




