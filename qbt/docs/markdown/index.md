# QBT - QBT Build Tool

### {Fast, Correct, Scalable, Powerful}.keepall()

This is the documentation for QBT, a build tool which specializes in dependency management and stitching repositories together.

## Why QBT?

Here is what makes QBT great!

* Consistency - QBT is designed to to used as part of a system that guarantees software is always consistent and builds successfully.  It can help large organizations composed of many individual teams coordinate large changes across massive codebases without breaking or blocking anybody.
* Flexibility - QBT was built on the same powerful ideas as Git, and uses many similar idioms.  Like Git, it is first and formost a toolkit which puts the power and complexity in your hands, and with which you can do pretty much anything.
* Correctness - QBT helps you build a "software lifestream" of consistent, tested, reproducible, technology-agnostic software.
* Performance Scalability - QBT is designed to support massive dependency graphs.  Every build occurs in parallel and incrementally builds only what has changed, at a package granularity.
* Social Scalability - QBT is the only build tool designed to scale across trusted and untrusted organizations.  When your software depends upon software written by people you don't know, you need QBT!
* Trust - Because QBT builds everything (including itself) from source, using tree hashes for consistency, you can trust anything you build with it.  Though the easiest way to get started with QBT is to use a binary distribution, if you want to build QBT itself from source, let us know, we can help.
* Repeatability - Because QBT manages your dependencies and places them in your environment for you, and because QBT uses git tree hashes of packages and their dependencies to calculate CumulativeVersions, you know your builds are repeatable and representative of exactly what is in source control.
* Reliability - Because QBT hands control over to your build's qbt-make script in a transparent way, you can be confident your build is as reliable as the code you write, no more, no less.  You choose how your code is built, QBT just enforces your dependencies.

## How do I get QBT?  What are the needed dependencies?

QBT is small!  The distribution of meta_tools is only 12MB, and all you need to get started is that, a JDK8, and Git installed.

Python is also required to be on your path as the wrapper scripts that invoke qbt are written in python, but they should work with system python and no additional dependencies.

You can download a binary release of QBT from the [QBT Website](http://qbtbuildtool.com).

## How stable is QBT?  Can I use it in production?

There are still a few outstanding questions with QBT, particularly in how organizations will share code externally with eachother.  QBT's documentation is also very immature still.  Aside from that, we believe QBT is ready for use inside an enterprise or for building personal projects.

## What platforms does QBT work with?

Presently QBT is only well-tested on GNU/Linux.  It should be very easy to make QBT run on any UNIX-like operating system, such as OSX.  There are not presently plans to add support for windows, and windows users should probably plan on running QBT in a VM, however because QBT is written in Java it is possible it could work on windows some day.

## Get Started

* [Install QBT](install.html)
* [Quick Start Guide](quick-start.html)
* [Development Guide](development-guide.html)

## Comprehensive Documentation

* [Table of Contents](contents.html)

    vi: ft=markdown
