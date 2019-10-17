# BeautifyTvShowDirectory

## Compilation instructions

Compilation will be done by Maven which will install all of the dependencies.
To compile, just execute:

`mvn install`

and to run:

`java -classpath bin/ main.java.BeautifyTvShow $rootTvDirectory` (replace `$rootTvDirectory` with the specific directory you want to beautify)

This is still a work in progress but hopefully in the end, the original file (e.g. *Doctor.Who.S01E05.World.War.Three.1080p.Dts.H265-d3g.mkv*) will be located in in the following structure: 

*Doctor Who/Season 01/Doctor Who S01E05 - World War Three.mkv*
