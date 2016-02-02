# QBT - Getting Started

This guide will help you run your first build using QBT, and demonstrate some of QBT's flexibility.

This guide assumes you already have QBT installed.  If you haven't yet obtained a binary copy of QBT, see the [Installation Guide](install.html).

## Obtain a copy of meta

To build anything with QBT, you need a "qbt-manifest" file.  This file tells QBT what packages exist, what repositories they live in, what dependencies they have, and what revision of the repository to use.  When using QBT in the recommended way (with metatools), it is further assumed that the manifest file is checked into a git repository for versioning.  You can think of the meta repository like a git repository with many "git submodules", except instead of using git to manage the submodules, you use QBT and metatools.

Create a directory to make your "workspace".  This directory will contain all your qbt repositories.

>     $ mkdir ~/workspace
>     $ cd ~/workspace

You can obtain the meta used to build QBT and use that as an example.  Fetch it into your workspace

>     $ git clone https://github.com/AmlingQbt/meta.git

## Fetch a sample config file

Configuring QBT for first-time use can be somewhat lengthy process.  This is partly because QBT is still beta software, and partly because QBT is written with so few assumptions, and to be so open-ended and configurable, it can't do much until you decide how you want it to operate.

If you are satisfied with the recommended configurations, however, you can copy a config file and be up-and-running almost immediately.  It is recommended you read the sample configuration file as it has extensive comments explaining what knobs you might want to tweak.  The full documentation further explains what all of that means in greater depth.

>     $ cd meta
>     $ wget http://qbtbuildtool.com/docs/examples/qbt-config.sample.txt
>     $ mv docs/qbt-config.sample.txt qbt-config

As you can see, your `qbt-config` file stays next to your qbt-manifest, but is not checked in because it almost always contains local settings.  If you are configuring QBT for use at an enterprise, you may wish to check in a config file, but for most uses of QBT that is not recommended.

## Fetch pins

You have fetched the "meta" repository, but you don't yet have any pins.  You needed to set up your config file first, but now that it is in place, you need to finish fetching the code.  To do this, you can run fetchPins.

>     $ qbt fetchPins amling --all

This will fetch all the histories in the repositories mentioned in your qbt-manifest file.

## Set up your workspace

You will want to create a `.qbt-meta-location` file in the root of the workspace, so qbt can always find the manifest and config file.

>     $ cd ~/workspace
>     $ echo "meta" > .qbt-meta-location

Now QBT commands should transparently find your manifest and config file from workspace and any directory under it.

## Ensure JDK is configured properly

The default meta uses JDK versioning, so you will also have to set the environment variable JAVA_1_8_HOME.  Since your JAVA_HOME already points to a JDK8 from following the directions on the install page, you can do this:

>     $ export JAVA_1_8_HOME=$JAVA_HOME

## Perform your build

You are now ready to perform a build.  You can build your own version of QBT by doing the following:

>     $ qbt build --package meta_tools.release --output requested,directory,/tmp/meta_tools.release-%p-%v
>     $ /tmp/meta_tools/release-*/bin/qbt help

If that second line outputs the help for QBT, then congratulations, you just used qbt to build qbt!

## Configure merge driver and diff driver

If you are going to be doing cherry-picks, rebases, or merges in the meta repository, you will want to add the following to your git configuration (either in `meta/.git/config` or in `~/.gitconfig`).

>     [merge]
>         conflictStyle = diff3
>     [merge "qbt-manifest"]
>     	driver = qbt mergeDriver %A %O %B

Optionally, you may also want to add this if you want `git diff` in meta to show differences in sattelite repositories.

>     [diff "qbt-manifest"]
>         command = qbt sdiffDriver  

Both of these require that the QBT wrapper script and a python interpreter be on your path and that JAVA_HOME be set to a JDK8.

For more in-depth discussion of what is going on here, see the section in the [Development Guide](development-guide.html) about the merge driver and diff driver.

## Summary

If you are ready to learn how to use QBT to make changes and submit pull requests, continue on to the [Development Guide](development-guide.html).

    vi: ft=markdown
