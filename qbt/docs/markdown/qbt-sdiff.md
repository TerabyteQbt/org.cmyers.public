# QBT - Command `sdiff`

## Usage

See the log between two meta commits

>     $ qbt sdiff HEAD~ HEAD

See the log and diff between two meta commits

>     $ qbt sdiff HEAD~ HEAD --log-diff

See the log between two meta commits with an extra argument like --numstat

>     $ qbt sdiff HEAD~ HEAD --extra-arg="--numstat"

See the log between the manifest in the working tree versus the last commit's manifest

>     $ qbt sdiff HEAD .

See the log between the HEAD of the currently overridden sattelites and the last commit's manifest

>     $ qbt sdiff HEAD SAT

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt sdiff` is the command for diffing manifest files (and seeing the *real* diff, instead of just sha1 changed from A to B).

For details about how to set up the diff driver, see the [Development Guide](development-guide.html).
For details about the 6 different states a change can be in, see [Extended Tutorial](tutorial.html) section entitled "Crafting QBT Commits".

    vi: ft=markdown
