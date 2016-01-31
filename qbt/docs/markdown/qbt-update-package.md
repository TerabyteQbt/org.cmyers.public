
# QBT - Command `updatePackage`

## Usage

Update an existing package's metadata in the manifest file

Relocate a package to a new prefix

>     $ qbt updatePackage --package meta_tools.release --prefix awesome/release

Add a qbtEnv key to a package

>     $ qbt updatePackage --package meta_tools.release --qbtEnv JDK

Add a dependency to a package

>     $ qbt updatePackage --package meta_tools.awesome --normalDependency 3p.gradle:Weak

Remove a dependency from a package

>     $ qbt updatePackage --package meta_tools.awesome --removeFields --normalDependency 3p.gradle:Weak

Remove a qbtEnv key to a package

>     $ qbt updatePackage --package meta_tools.release --removeFields --qbtEnv JDK

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt updatePackage` lets you easily modify package metadata in the manifest without having to edit the manifest by hand.

    vi: ft=markdown
