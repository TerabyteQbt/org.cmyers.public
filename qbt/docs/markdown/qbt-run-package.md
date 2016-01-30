# QBT - Command `runPackage`

## Usage

Rename the `docs` directory to `documentation` in all packages in the `org.cmyers.public` repo, with maximum parallelization

>     $ qbt runPackage --repo org.cmyers.public -J --shell "mv docs documentation && sed -i 's/docs/documentation/g' qbt-make"

Drop into a shell whose environemnt is set up and ready to run qbt-make by hand for the package `qbt.app.main` (this is often useful when you need to quickly edit a package then run its build - or if there is a way to compile just a portion of a package if you can invoke the command directly).

>     $ qbt runPackage --package qbt.app.main --interactive-shell
>     $ mkdir /tmp/out
>     $ export OUTPUT_ARTIFACTS_DIR=/tmp/out
>     $ ./qbt-make
>     ...

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt runPackage` is the go-to command for manually running builds.  It can run a command, or drop you into a shell, in a given "package".  Just like a build would do, it copies the tree of the package to a temporary location, and sets up `$INPUT_ARTIFACTS_DIR` to contain the package's dependencies.  It does not, however, set up an `$OUTPUT_ARTIFACTS_DIR` for you so you must do that by hand if you want to run the `qbt-make` file directly (since it usually assumes that exists).

This is very useful for debugging, running partial builds, or examining intermediate build output.  If you are hoping to run git commands, see [runOverrides](qbt-run-overrides.html).  If you are hoping to run package artifacts, see [runArtifact](qbt-run-artifact.html).

For more details about `$INPUT_ARTIFACTS_DIR` and `$OUTPUT_ARTIFACTS_DIR`, see the [Development Guide](development-guide.html).

    vi: ft=markdown
