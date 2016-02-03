# QBT - Command `build`

## Usage

Build the `meta_tools.release` package

>     $ qbt build --package meta_tools.release

Build all packages in the repo `meta_tools`

>     $ qbt build --repo meta_tools

Build all packages in the manifest with maximum parallelism and log level set to debug

>     $ qbt build --all -J --logLevel=DEBUG

Build the `meta_tools.release` package and place its artifacts in a tarball located in `/tmp/meta_tools.release-CV.tar.gz` where "CV" is the cumulative version.

>     $ qbt build --package meta_tools.release --output requested,tarball,/tmp/%p-%v.tar.gz

Build the `meta_tools.release` package and place its artifacts in a directory located in `/tmp/out`.

>     $ qbt build --package meta_tools.release --output requested,directory,/tmp/out

Build place the `meta_tools.release` package's artifacts in a tarball located in `/tmp/meta_tools.release-CV.tar.gz` but require it to be a cache hit - if the package is not already built, do not build it, instead fail the build.  The `--no-builds` argument can be used with any build command to ensure nothing builds and instead just verify certain packages are present in the cache.

>     $ qbt build --package meta_tools.release --output requested,tarball,/tmp/%p-%v.tar.gz --no-builds

Build all packages contained in all overrides

>     $ qbt build --overrides

Build the package `misc1.commons.concurrent.main` and all packages that depend upon it, directly or transitively

>     $ qbt build --package misc1.commons.concurrent.main --outward

The commands `--groovyPackages` and `--verifyGroovy` let you specify arbitrarily complex code snippets that describe what packages to build.

For example, you can do packages which are inwards of the repository "3p" like this:

>     $ qbt build --groovyPackages 'inward(r("3p"))'

Useful functions include: `inward`, `outward`, `overrides`, `p("pkg_name")`, `r("repo_name")`



>     $ qbt build --groovyPackage '[]'

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt build` is the command for issuing QBT builds.  Builds do not always occur as a result of this command, however, as artifacts may be detected in the cache.  Builds will only occur if there are no cached artifacts for a particular package at its current CV.

    vi: ft=markdown
