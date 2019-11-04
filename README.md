# BeautifyTvShowDirectory

## Compilation instructions

Compilation will be done by Maven which will install all of the dependencies.
To get anexecutable JAR with the dependencies in the CLI, execute:

`mvn clean compile assembly:single`

and to run:

`java -jar target/BeautifyTvShowDirectory-0.0.1-SNAPSHOT.jar $rootTvDirectory` (replace `$rootTvDirectory` with the specific directory you want to beautify or with `test/` which is a test directory that contains two sample files to beautify).

**To get it to work on eclipse**, on the *Run Configurations* window, have `clean install exec:java1` as the goals

**_NOTE:_** When executing from the Eclipse IDE, the root directory to beautify will always be the *test/* directory unless modified in the [pom.xml](https://github.com/MichelGeorgesNajarian/BeautifyTvShowDirectory/blob/master/pom.xml) line 38.

This is still a work in progress but hopefully in the end, the original file (e.g. *Doctor.Who.S01E05.World.War.Three.1080p.Dts.H265-d3g.mkv*) will be located in in the following structure:

*Doctor Who/Season 01/Doctor Who S01E05 - World War Three.mkv*


**_ANOTHER IMPORTANT NOTE:_** The program is using The Moie DB's APIs. In order for you to use this program, you would need to generate your own API key, create a file named *sensitive.properties* and place it in the same directory as the [applications.properties](https://github.com/MichelGeorgesNajarian/BeautifyTvShowDirectory/tree/master/src/main/resources) file.

In the *sensitive.properties* file, there should only be a single line (so far):
`api.key=$YOUR_API_KEY_HERE`
