# QBT - Command `removeRepository`

## Usage

Remove the `meta_tools` repository from your manifest

>     $ qbt removeRepository --repo meta_tools

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt removeRepository` removes a repository from a manifest file.  This is generally only done if you add a repository incorrectly, or if a repository becomes deprecated.  Past versions of the manifest will still contain the repository and continue to work correctly as long as the old pins remain avaiable.

    vi: ft=markdown
