# QBT - Command `runArtifact`

## Usage

Build (if necessary) and then run the script "bin/qbt" in the `meta_tools.release` package, with the arguments "help -a"

>     $ qbt runArtifact --package meta_tools.release bin/qbt help -a

Examine a file in the artifacts of the `meta_tools.release` package (runs command in the artifacts dir, and with an absolute path rather than relative path)

>     $ qbt runArtifact --package meta_tools.release --artifactsDir --absolute /bin/cat qbt.versionDigest

Run link-checker on an executable file in artifacts

>     $ qbt runArtifact --package some.cpp.application --artifactsDir --absolute /bin/ldd bin/someApplication

Run the application, but only if it is already built, otherwise fail

>     $ qbt runArtifact --no-builds --package meta_tools.release bin/qbt help -a

Build (if necessary) and then run the script "bin/qbt" in the `meta_tools.release` package, with the arguments "help -a", using maximum parallelism for the build

>     $ qbt runArtifact -J --package meta_tools.release bin/qbt help -a

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt runArtifact` is for running artifacts directly out of the artifact cache.  It can be very convenient and make the test-build-test cycle faster. 

    vi: ft=markdown
