# QBT - Command `mergeDriver`

## Usage

Invoke the merge driver on a set of manifests (LHS, MHS, RHS)

>     $ qbt mergeDriver lhs-qbt-manifest rhs-qbt-manifest mhs-qbt-manifest

Configure the merge driver to be used by git

>     $ git config --global merge.qbt-manifest.driver 'qbt mergeDriver %A %O %B'

This requires qbt to be on your path, and your `$JAVA_HOME` properly set.

## Description

`qbt mergeDriver` is the main entrypoint for using qbt to assist git in resolving conflicts in the manifest file when doing cherry-picks, rebases, and merges.

It is expected users will practically never run this command directly.

For more details about setting up the merge driver, see the [Developing With QBT](development-guide.html).

    vi: ft=markdown
