# QBT - Common Options

## Logging Options

All QBT commands support logging options.

>     [--logFormat ARG] : Set log format
>     [--logLevel ARG] : Set log level, e.g.  DEBUG (sets root), my.Class=INFO (sets specific category)
>     [--logProperties ARG] : Configuring logging from properties


Set the log level to DEBUG

>     $ qbt build --logLevel=DEBUG

Set the log level to DEBUG for the class qbt.mains.PushPins

>     $ qbt build --logLevel=qbt.mains.PushPins=DEBUG

Set the logging format (TODO: example formats)

>     $ qbt build --logFormat=<SLF4J log formats>

Configure logging using a logging properties file

>     $ qbt build --logProperties=/path/to/log.properties

## Help options

Any command will print out extended help if given the `--help` argument

>     $ qbt build --help

## Manifest/Config options

All commands which require config or manifest (this is pretty much everything) can have a path specified directly on the command line instead of trying to auto detect them.

>     $ qbt build --manifest=/path/to/qbt-manifest --config=/path/to/qbt-config

One particularly useful trick is to provide a manifest via a pipe if the manifest you want does not exist on disk.  For example, you might want the manifest from a version or two ago, or from a branch you just fetched but haven't merged in yet.

>     $ qbt fetchPins origin --manifest <(git show HEAD~:qbt-manifest) -J --all --logLevel=DEBUG

## Parallelization Options

Almost all commands take parallelization options.

The default parallelization is twice the number of CPUs detected.

Build using only 1 thread at a time (no parallelization) - both of these are equivalent

>     $ qbt build --package meta_tools.release -j1
>     $ qbt build --package meta_tools.release --parallelism=1

Build using infinite parallelization (as many threads as possible) - both of these are equivalent

>     $ qbt build --package meta_tools.release -J
>     $ qbt build --package meta_tools.release --infinite-parallelism

## QBT Environment Options

TODO: write about `--qbtEnv`.

    vi: ft=markdown
