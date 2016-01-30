# QBT - Command `status`

## Usage

See the dirty/clean status of your current overrides (no output means all clean)

>     $ qbt status

See the full git status output of your current overrides (no output means all clean)

>     $ qbt status -v

See the git status output of just the `meta_tools` repository

>     $ qbt status -v --repo meta_tools

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt status` is the QBT analog for `git status`, and similarly tells you the state of changes in the index in each of your sattelites.  It is a good idea to always run `qbt status -v` before `qbt commit` to ensure you don't accidentally commit something you didn't mean to.

    vi: ft=markdown
