# QBT - Command `addRepository`

## Usage

Add a new repository to the manifest file (and push its first pin to your pin cache).

>     $ git init new_repo
>     $ echo "foo" > new_repo/foo.txt
>     $ git -C foo add foo.txt
>     $ git -C foo commit -m"initial commit"
>     $ qbt addRepository --repo new_repo

Add a new tip of a repository to the manifest file (and push its pin to your pin cache if needed).

>     $ qbt addRepository --repo new_repo^{FROZEN}

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt addRepository` is the easiest way to add a new repository to your manifest file.  Make sure you `git init` your repository where the override would go, and that your repository has at least one commit which is pointed to by HEAD.  You can then run the `qbt addRepository` command.

The command will both add the repository to your manifest file, and push the first pin to your pin cache, which used to be very annoying to do manually.

    vi: ft=markdown
