# QBT - Command `updateOverrides`

## Usage

Update all overrides to the version specified in the current manifest file

>     $ qbt updateOverrides

Attempt to update all overrides to the version specified in the current manifest file, even if they are currently dirty

>     $ qbt updateOverrides --allow-dirty

Update the `meta_tools` repository to the version specified in the current manifest file

>     $ qbt updateOverrides --repo meta_tools

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt updateOverrides` is how to reset your "extended working tree", also known as sattelite repos or overrides, to match the versions specified in your currently checked out manifest.  Any time you check out a new revision or branch in your meta repository, you will probably need to run `qbt updateOverrides`, or your overrides will contain random versions that do not match the rest of your manifest.

For an extended discussion of overrides, see the [Development Guide](development-guide.html).
For an extended discussion of the various stages a change can be in, see the [Extended Tutorial](tutorial.html).

    vi: ft=markdown
