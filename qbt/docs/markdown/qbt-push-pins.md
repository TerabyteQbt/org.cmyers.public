# QBT - Command `pushPins`

## Usage

Push all pins to the pin remote configured as `origin` in your `qbt-config` file, using maximum parallelism

>     $ qbt pushPins origin --all -J

Push all pins to the given pin specification (which may or may not include %r, etc)

>     $ qbt pushPins ssh://git@github.com/NewUserQbt/%r.git --all

Push a manifest and related pins for a manifest file you don't currently have checked out (nifty trick!)

>     $ git push origin some-user-branch:refs/heads/code-review-me
>     $ qbt pushPins origin --manifest <(git show some-user-branch:qbt-manifest) --all

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt pushPins` is one of only two qbt commands that hit the network ([fetchPins](qbt-fetch-pins.html) is the other).  This command pushes all commits in sattelite repositories necessary to build the current manifest into your pin remote.

After running `qbt pushPins origin --all` with a given `qbt-manifest` file, all pins needed to build all packages in that manifest should be present in the pin remote (so others can fetch them plus your manifest, and build your code).  Furthermore, this should hold true for all previous versions in history as well, as long as you don't allow non-fast-forward commits in sattelite repositories.

For details on how pins and pin remotes work, see the [Extended Tutorial](tutorial.html) section called "About Pins and Pin Remotes".

    vi: ft=markdown
