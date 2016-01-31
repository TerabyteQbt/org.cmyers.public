# QBT - Command `addPackage`

## Usage

Add a new package to the manifest file

>     $ qbt addPackage --repo meta_tools --package meta_tools.awesome --prefix awesome --qbtEnv JDK --buildType normal

Add a new package to the manifest file with dependencies

>     $ qbt addPackage --repo meta_tools --package meta_tools.awesome --prefix awesome --normalDependency 3p.gradle:Weak --normalDependency meta_tools.main:Strong

Add a new package to the manifest file with a verify dependency

>     $ qbt addPackage --repo meta_tools --package meta_tools.awesome --prefix awesome --verifyDependency meta_tools.test/test

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt addPackage` lets you easily add packages to the manifest file without having to edit the manifest by hand.

    vi: ft=markdown
