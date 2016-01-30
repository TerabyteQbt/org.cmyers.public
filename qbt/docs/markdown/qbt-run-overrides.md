# QBT - Command `runOverrides`

## Usage

Run the command `git status` in each overridden repository

>     $ qbt runOverrides --overrides git status

Run the (interactive) command `git add -p` in each overridden repository

>     $ qbt runOverrides --overrides --interactive git add -p

Drop into a shell in each override

>     $ qbt runOverrides --overrides --interactive-shell

Run the `sloccount` tool in each override using maximum parallelism

>     $ qbt runOverrides --overrides -J sloccount

Find all commits in all overrides that include a diff that matches REGEX

>     $ qbt runOverrides --overrides git log -G'REGEX'

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt runOverrides` is a handy way of running commands in the actual git repos, rather than in the packages or artifacts.  Before `qbt status`, the first example above was a common invocation.  As the last example above shows, `qbt runOverrides` still has a lot of value to deliver.

    vi: ft=markdown
