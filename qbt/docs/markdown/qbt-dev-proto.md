# QBT - Command `devProto`

## Usage

Generate the Eclipse IDE Integration files for all overrides

>     $ qbt devProto --proto eclipse-gen --overrides

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt devProto` runs the supplied development protocol.  Packages can "opt in" to various protocols, so it is the package that "knows what to do".  Development Protocols are specified in the `.qbt-dev-proto` directory in the root of the package.  This directory contains two files, named `proto.exec` and `proto.inputs` (if the protocol was named `proto`).  So for eclipse-gen, we have the following files in each eclipse-gen supporting package:

In the file `.qbt-dev-proto/eclipse-gen.exec` (which is executable):
>     #!/bin/bash
>     
>     set -e
>     
>     rm -rf .src.gen
>     ./gen.pl
>     
>     $INPUT_DEV_PROTO_DIR/extra/qbt_fringe.eclipse_gen.simple.release/strong/qbt_fringe.eclipse_gen.simple.release/bin/eclipse_gen.simple

And in the file `.qbt-dev-proto/eclipse-gen.inputs`
>     return [
>         meta_tools.devproto.DevProtoInputs.PROTO,
>         meta_tools.devproto.DevProtoInputs.extra('qbt_fringe.eclipse_gen.simple.release'),
>     ];

The above example is slightly non-standard, because it was taken from the package `qbt.app.main`, which uses generated source.  For this reason, the exec script must regenerate the generated source, *then* run the eclipse_gen script.

The inputs file specifies what "dependencies" the dev protocol requires, in this instance, it requires the eclipse_gen tool.

    vi: ft=markdown
