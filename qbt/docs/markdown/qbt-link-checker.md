# QBT - Link Checker

## Usage

Run the link checker from inside a `qbt-make` script:

>    $INPUT_ARTIFACTS_DIR/weak/qbt_fringe.link_checker.release/strong/qbt_fringe.link_checker.release/bin/link_checker --qbtDefaults

Run the link checker on a jar outside a qbt build:

>     $ qbt runArtifact --package qbt_fringe.link_checker.release bin/link_checker --check somejar.jar --lib dependency1.jar --lib dependency2.jar --whitelistFrom 

Run the link checker on a jar outside a qbt build, whitelisting certain source/destination prefixes

>     $ qbt runArtifact --package qbt_fringe.link_checker.release bin/link_checker --check somejar.jar --lib dependency1.jar --lib dependency2.jar --whitelistTo org/slf4j/impl/StaticLoggerBinder --whitelistFrom org/somejar/MyStupidClass

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

The link checker is used to validate that "binary" packages (where we check in a jar and copy it into the artifacts as part of the build) correctly specify their dependencies.  It analyzes the bytecode and tries to ensure all referenced classes are available on the classpath.  Sometimes, a third-party jar is just a complete disaster and contains references to code that will never be present, from code that will (hopefully) never be run.  In the interest of not letting perfect be the enemy of good, you can whitelist those things, and still check that the other dependencies resolve.

    vi: ft=markdown
