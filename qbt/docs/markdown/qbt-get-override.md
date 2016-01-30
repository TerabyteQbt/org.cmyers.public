# QBT - Command `getOverride`

## Usage

Create an override for the `meta_tools` repository

>     $ qbt getOverride --repo meta_tools

Create an override for the repository that contains the `meta_tools.release` package

>     $ qbt getOverride --package meta_tools.release

Create an override for all repositories in your manifest

>     $ qbt getOverride --all

TODO: document groovyRepos?

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt getOverride` is the main way to examine or change code in sattelite repositories, since the meta repository contains metadata only.

For details on how Overrides work, see [Development Guide](development-guide.html).

    vi: ft=markdown
