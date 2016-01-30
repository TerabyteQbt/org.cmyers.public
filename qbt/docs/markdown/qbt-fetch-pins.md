# QBT - Command `fetchPins`

## Usage

Fetch all pins from the pin remote configured as `origin` in your `qbt-config` file, using maximum parallelism

>     $ qbt fetchPins origin --all -J

Fetch all pins from the given pin specification (which may or may not include %r, etc)

>     $ qbt fetchPins ssh://git@github.com/NewUserQbt/%r.git --all

Fetch the manifest then the related pins for a new branch you were asked to code review, without checking anything out (nifty trick!)

>     $ git fetch ssh://git@github.com/NewUserQbt/meta.git refs/heads/code-review
>     $ qbt fetchPins ssh://git@github.com/NewUserQbt/%r.git --manifest <(git show FETCH_HEAD:qbt-manifest) --all

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt fetchPins` is one of only two qbt commands that hit the network ([pushPins](qbt-push-pins.html) is the other).  This command fetches all commits in sattelite repositories necessary to build the current manifest into your pin caches.

After running `qbt fetchPins origin --all` with a given `qbt-manifest` file, it should be possible to build every package in that manifest without relying upon the network at all.  Furthermore, this should hold true for all previous versions in history as well, as long as you don't allow non-fast-forward commits in sattelite repositories.

For details on how pins and pin remotes work, see the [Extended Tutorial](tutorial.html) section called "About Pins and Pin Remotes".

    vi: ft=markdown
