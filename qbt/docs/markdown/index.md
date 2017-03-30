# QBT - QBT Build Tool

### {Fast, Correct, Scalable, Powerful}.keepall()

This is the documentation for QBT, a build tool which specializes in dependency management and stitching repositories together.

# FAQ

Here are some frequently asked questions about QBT

## Why Use QBT?

Here is what makes QBT great!

* Consistency - QBT is designed to to used as part of a system that guarantees software is always consistent and builds successfully.  It can help large organizations composed of many individual teams coordinate large changes across massive codebases without breaking or blocking anybody.
* Flexibility - QBT was built on the same powerful ideas as Git, and uses many similar idioms.  Like Git, it is first and formost a toolkit which puts the power and complexity in your hands, and with which you can do pretty much anything.
* Correctness - QBT helps you build a "software lifestream" of consistent, tested, reproducible, technology-agnostic software.
* Performance Scalability - QBT is designed to support massive dependency graphs.  Every build occurs in parallel and incrementally builds only what has changed, at a package granularity.
* Social Scalability - QBT is the only build tool designed to scale across trusted and untrusted organizations.  When your software depends upon software written by people you don't know, you need QBT!
* Trust - Because QBT builds everything (including itself) from source, using tree hashes for consistency, you can trust anything you build with it.  Though the easiest way to get started with QBT is to use a binary distribution, if you want to build QBT itself from source, let us know, we can help.
* Repeatability - Because QBT manages your dependencies and places them in your environment for you, and because QBT uses git tree hashes of packages and their dependencies to calculate CumulativeVersions, you know your builds are repeatable and representative of exactly what is in source control.
* Reliability - Because QBT hands control over to your build's qbt-make script in a transparent way, you can be confident your build is as reliable as the code you write, no more, no less.  You choose how your code is built, QBT just enforces your dependencies.

## What is QBT's License?

QBT is released under the [Unlicense](http://unlicense.org).  A locally hosted copy of the license is available [here](UNLICENSE.html).  This license is comparable to releasing to the public domain and gives everyone unfettered usage of QBT for whatever purpose desired.  The main reason the unlicense is used rather than just placing QBT directly in the public domain is (due to legal insanity), it is importaint to disclaim certain implied warranty, and in some legal realms public domain does not exist.  The author's intent here is to spend as close to zero time units screwing with licenses while explicitly disclaiming and any all copyright monopoly interest.  Please use QBT for whatever you want, including curing cancer and murdering babies, and maybe even murdering babies as a service (MBaaS).

## How do I get QBT?  What are the needed dependencies?

QBT is small!  The distribution of `meta_tools` is only 12MB, and all you need to get started is that, a JDK8, and Git installed.

Python is also required to be on your path as the wrapper scripts that invoke qbt are written in python, but they should work with system python and no additional dependencies.

You can download a binary release of QBT from the [QBT Website](http://qbtbuildtool.com).

## What if I don't trust you and I want to build my own QBT?

You, my friend, are our sort of crazy.  Unfortunately, in its current state, QBT is not super easy to build without QBT (although nothing precludes us from producing a bootstrap script).  If you just want to produce a copy of QBT which you are certain contains only artisinal jars you lovingly downloaded from maven central yourself, plus the source code, you can do the following:

* For each repo and sha1 r, sha in `qbt_manifest`, `git init $r && cd $r && git fetch https://github.com/AmlingQbt/$r.git refs/qbt-pins/$sha && git checkout FETCH_HEAD`
* Produce a classpath which contains every maven central dependency needed - see `mc/.config` in the `3p` repo for a list of dependencies from which you could produce a transitive closure of dependencies.  You will also see data in `mc/.cache` that contains the version fetched and the jars downloaded, you may elect to validate these jars by comparison to mavencentral instead.
* using the dependencies in the `qbt_manifest`, or in a `qbt.versionTree` file (included in the distribution), built each "package" from the ground up by invoking javac with the classpath, then adding the resultant classfiles to the classpath.
* once you have everything compiled and presumably shoved into a jar, you should be able to invoke the QBT main class, `qbt.QbtMain`, setting the classpath as before and setting `JAVA_HOME` and `JAVA_1_8_HOME` to a 1.8 jdk.  From here, you can fetch and build a "real" qbt using your janky but verified qbt.

## How stable is QBT?  Can I use it in production?

There are still a few outstanding questions with QBT, particularly in how organizations will share code externally with eachother.  QBT's documentation is also very immature still.  Aside from that, we believe QBT is ready for use inside an enterprise or for building personal projects.

## What platforms does QBT work with?

Presently QBT is only well-tested on GNU/Linux.  It should be very easy to make QBT run on any UNIX-like operating system, such as OSX.  There are not presently plans to add support for windows, and windows users should probably plan on running QBT in a VM, however because QBT is written in Java it is possible it could work on windows some day.

## Why did you crazy people write a build system in this day and age?

There is a reason so many companies have written their own (internal, proprietary) build system.  The reality of massive enterprise scale development is that many independent teams work across many separate repositories and create dependencies which span technological stacks and spheres of influence, but at the end of the day, a company wants to release tested, consistent versions of software to its customers (whether that means deployments of services or "shrinkwrap" style releases).

Existing solutions for building software are not, generally, able to span different technology stacks (how do you make a ruby gem that depends upon a python script, or a java program that depends upon a C library?  How do you develop a framework that produces client APIs in java, python, ruby, and C/C++?), all because they are too coupled to the language they were designed to serve.

Existing solutions for building software are also not, generally, able to stitch together consistent views of software across many repositories, because they are not coupled *enough* to their source control system.

QBT is not exactly a build tool, so much as it is a dependency management and repository stitching framework in which you may use whatever other build tools you desire.  QBT is fairly coupled to the Git source control system, though it could probably be adapted to any equivalent DVCS (such as Mercurial).  QBT is completely unoppinionated, however, as to what a build actually means.  You have some inputs on the filesystem, a script runs, and it puts its outputs somewhere else on the filesystem.  QBT does not know or care if you are building shared object libraries, dynamic executables, wheels, eggs, gems, debs, rpms, tarballs, crystals, whispers, dreams, or magic spells, but if your `qbt_manifest` says that one magic spell depends upon another, it will dutifully put its output artifacts on the filesystem so the other magic spell's build process can find them.

If you currently have every line of code you wrote and every line of code you depend upon checked into a single massive repository, and you have a single effecient process which, from a checkout of that repository, can produce deterministic build outputs, then QBT can't really help you (unless your build lacks incrementalism or parallelism, and you want to add that - it could help with that).

Since the above statement is true for almost nobody I've ever heard of or talked to, most large organizations could benefit from QBT.

## Get Started

* [Install QBT](install.html)
* [Quick Start Guide](quick-start.html)
* [Developing With QBT](development-guide.html)

## Comprehensive Documentation

* [Extended Tutorial](tutorial.html)
* [Table of Contents](contents.html)

    vi: ft=markdown
