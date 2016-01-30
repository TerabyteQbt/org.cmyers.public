# QBT - Command `squashCommits`

## Usage

For each repository in your manifest, do a log between the revision at HEAD and the revision at HEAD~, and if the diff contains more than one commit in that sattelite, rewrite that change as a single commit, then replace the manifest commit with an equivalent commit that has that squashed commit.

>     $ qbt squashCommits

Operate only on the `meta_tools` repository

>     $ qbt squashCommits --repo meta_tools

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt squashCommits` is a handy shortcut for a task you might want to do if you ever accidentally run a `git commit` or `qbt commit` when you meant to put in `--amend` but forgot.  It is also useful if you are taking an extended history and squashing it into a simplified history.

For example, to squash the last three commits in meta, you could do this:

>     $ git checkout HEAD~3
>     $ git checkout HEAD@{1} -- qbt-manifest
>     $ git add . && git commit

You now have a single commit with all the changes in those 3 commits, however, in your sattelites there are still potentially multiple commits represented.  You can then squash them by running

>     $ qbt squashCommits

For more details about how the various commit stages work, see the [Extended Tutorial](tutorial.html).

    vi: ft=markdown
