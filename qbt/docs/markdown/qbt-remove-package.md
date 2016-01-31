# QBT - Command `removePackage`

## Usage

Remove a package from your manifest file

>     $ qbt removePackage --package meta_tools.release

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt removePackage` removes a package from a manifest file.  This is generally only done if you add a package incorrectly, or if a package becomes deprecated.  Note that if any packages depend upon the package you are removing, they will no longer build correctly.  You should always check the manifest for other references to the package, or attempt a `qbt build --all` after removing a package.

    vi: ft=markdown
