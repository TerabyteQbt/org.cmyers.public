# QBT - Command `resolveManifestConflicts`

## Usage

Resolve manifest conflicts after the merge driver drops you to a shell with conflicts

>     $ qbt resolveManifestConflicts

Don't insist on resolving in dependency order

>     $ qbt resolveManifestConflicts --noDeps

Use a particular strategy (in this case `ours`) for resolution in sattelites.

>     $ qbt resolveManifestConflicts --strategy=ours

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

When you do a merge, rebase, or cherry-pik in a meta repository, you are all but guaranteed to have conflicts in your `qbt-manifest` file.  This is why QBT provides a merge driver.  With the merge driver in place, you only get conflicts when there is an actual textural conflict in a sattelite repository.  In that rare case, you must resolve the conflicts in the sattelites.  This command exists to help you do that.

For details about the merge driver and conflict resolution, see the [Development Guide](development-guide.html).

    vi: ft=markdown
