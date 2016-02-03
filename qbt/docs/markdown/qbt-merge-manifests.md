# QBT - Command `mergeManifests`

## Usage

Perform a three-way merge of `qbt-manifest` files

>     $ qbt mergeManifests --lhs lhs-qbt-manifest --rhs rhs-qbt-manfiest --mhs mhs-qbt-manifest --lhsName LHS --rhsName RHS --mhsName MHS


See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt mergeManifests` is a tool you can invoke to manually merge arbitrary `qbt-manifest` files.  Generally, users do not invoke mergeManifests directly but rather set up the [mergeDriver](qbt-merge-driver.html) as outlined in the [Developing With QBT](development-guide.html).

    vi: ft=markdown
