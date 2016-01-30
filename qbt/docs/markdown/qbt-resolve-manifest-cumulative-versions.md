# QBT - Command `resolveManifestCumulativeVersions`

## Usage

Get the CumulativeVersion (CV) of the meta_tools.release package

>     $ qbt resolveManifestCumulativeVersions --package meta_tools.release

Get the CumulativeVersion (CV) of the meta_tools.release package with a 1.9 JDK

>     $ JDK="1_9" qbt resolveManifestCumulativeVersions --package meta_tools.release --qbtEnv JDK="1_9"

Figure out which commits of meta have a given CV for a particular package

>     $ for i in $(git rev-list HEAD); do qbt resolveManifestCumulativeVersions --package meta_tools.release --manifest <(git show ${i}:qbt-manifest) | grep -q 6717d5033ea45da82cec7e952b9d9dfc237cdc42 && echo "Revision $i matches"; done
>     Revision 3072eb492d4322451608aaa5fe5f3e03ffa1fa23 matches
>     Revision 5c73ee875f8130d2eae565905daa5d78b9c1d1b9 matches
>     Revision ed96b0e923160e1ad0447f8a1a82dc3c7aefcfce matches
>     ....

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt resolveManifestCumulativeVersions` will calculate and print CVs for packages.  With enough brute force, it can be used to go in either direction (from a meta commit to the CV of a package in that manifest, or from a CV to a list of possible commits in meta that might have produced it).  It is important to note however that the latter of these two does not yield a unique result.  There are usually many commits for which a package has the same CV and one can never reliably tell which of these the artifact came from.  We can only tell that, unless someone is doing something stupid, it really really shouldn't matter which one of those it came from (because that is, by definition, what it means for the CV to be unchanged).

    vi: ft=markdown
