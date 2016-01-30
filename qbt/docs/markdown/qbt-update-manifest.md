# QBT - Command `updateManifest`

## Usage

Update the manifest file in the working tree to the commit pointed to by HEAD in each sattelite

>     $ qbt updateManifest

Update the manifest file in the working tree to the commit pointed to by HEAD for just the `meta_tools` repository

>     $ qbt updateManifest --repo meta_tools

Update the manifest file to the commit pointed to by HEAD in the `meta_tools` repo even if the repository is currently dirty (has uncommitted changes)

>     $ qbt updateManifest --repo meta_tools --allow-dirty

Update the manifest file to the commit pointed to by HEAD in the `meta_tools` repo even if the change would not be a fast-forward

>     $ qbt updateManifest --repo meta_tools --allow-non-ff

Upgrade the manifest to the newest version (this is the best way to get manifests updated when a new manifest format comes out)

>     $ qbt updateManifest --upgrade

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt updateManifest` used to be a key command used in creating commits in a QBT workspace, but has been almost completely superceeded by `qbt commit`.  Occasionally you might want to manually go through the steps of creating a commit so you can split a change up, or change dependencies in the manifest, etc., so those are the situations where you would instead use `updateManifest`.

See also the [commit](qbt-commit.html) command.

For detailed discussion of the various commit stages, see the [Extended Tutorial](tutorial.html).

    vi: ft=markdown
