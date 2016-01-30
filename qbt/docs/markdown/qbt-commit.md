# QBT - Command `commit`

## Usage

Create a new commit of the changes currently staged in all the sattelite repositories

>     $ qbt commit -m"some commit message"

Ammend the last commit with new changes currently staged in all the sattelite repositories, and changing its commit message to "some commit message"

>     $ qbt commit --amend -m"some commit message"

Ammend the last commit with new changes currently staged in all the sattelite repositories, without changing its commit message

>     $ qbt commit --amend

Create a new commit of the changes currently staged and unstaged in all the sattelite repositories (but not untracked files)

>     $ qbt commit -a -m"some commit message"

Create a new commit of the changes currently in all the sattelite repositories (even including deletes/untracked files)

>     $ qbt commit -A -m"some commit message"

Create a new commit of the changes currently staged in just the `meta_tools` repo

>     $ qbt commit -m"some commit message" --repo meta_tools

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt commit` is a shortcut for creating commits in a QBT workspace.  Normally you would have to create commits in sattelite repositories, update the manifest (which creates pins), stage the manifest changes, and commit the changes to the manifest.  This command can do all of that in a single command (which is almost always what you want).

If you need more granular control of your commit, see [updateManifest](qbt-update-manifest.html).

For an extended discussion of the various commit states, see the [Extended Tutorial](tutorial.html) section called "Crafting QBT Commits" for an extended discussion about what `qbt commit` does, and how to do it by hand.

    vi: ft=markdown
