# QBT - Developing With QBT

This guide will help you make changes, build them, and submit your changes as a pull request.

This guide assumes you already have QBT installed and have run through the [Quick Start Guide](quick-start.html).

## Making Changes

You might be wondering, when you run a qbt build command, what exactly happens?  For example, look at the build command you ran in the quick start guide:

>     $ qbt build --package meta_tools.release --output requested,directory,/tmp/meta_tools.release-%p-%v

At this time, you have a repository called "meta" checked out, but all it contains is a manifest and a config file - two text files.  One has metadata about packages, repositories, and repository revisions.  The other tells QBT where to find said repositories remotely, and where to cache them locally.

The most obvious reading of what happens next is spot on.  When you run a build, QBT analyzes your package dependency graph, maps your packages to the repositories that contain them, fetches the repositories into a local repository cache, builds each package (in a temporary location), and takes the resulting artifacts and loads them into an artifact cache.  In order to make changes to the code before building, you need to create an "override", which is really just asking QBT to use a local checkout instead of a revision specified by your manifest.

### Getting an Override

To examine or change the source code for a given package, you need to create an override.  You can ask QBT to override a repository or a specific package, but in either case it figures out the relevant repository and overrides the entire thing.  Git does not allow you to check out just a part of a repository, but don't worry - when your packages build and are not overrides (as is likely to be the case on a build node), they are isolated from each other, so your builds cannot "reach out" of your package and have secret dependencies on each other, or on their relative paths, etc.  In this way, by building with no overrides you can confirm your packages are properly independent, but when you build with overrides your builds are fast because they can take place "in place".  Because overrides always occur at the repository granularity, these commands are equivalent (because the meta_tools.release package is in the `meta_tools` repository).

>     $ qbt getOverride --package meta_tools.release
>     $ qbt getOverride --repo meta_tools

If you use the recommended config settings, the `meta_tools` repository will be placed next to your meta directory.

### Building with or without Overrides

QBT always looks for overrides first, so when you have a repository overridden, the version of that repository in your manifest is ignored.  If you accepted the "nifty trick" in the recommended config file, you can temporarily disable building with overrides by setting the environemnt variable `NO_OVERRIDES=1`.

You can make a change to the code in your override and see the result by running a build.

>     $ echo "compile error" >> qbt/bin/main/src/qbt/QbtMain.java
>     $ qbt build --package qbt.bin.main
>     Actually building qbt.bin.main@7a5294b16d6693b29adbd8378c9a188631c4171c...
>     [qbt.bin.main@7a5294b16d6693b29adbd8378c9a188631c4171c] Linter complete without errors.
>     [qbt.bin.main@7a5294b16d6693b29adbd8378c9a188631c4171c] /home/cmyers/projects/1/qbt/bin/main/src/qbt/QbtMain.java:120: error: class, interface, or enum expected
>     [qbt.bin.main@7a5294b16d6693b29adbd8378c9a188631c4171c] compile error
>     [qbt.bin.main@7a5294b16d6693b29adbd8378c9a188631c4171c] ^
>     [qbt.bin.main@7a5294b16d6693b29adbd8378c9a188631c4171c] 1 error
>     [qbt.bin.main@7a5294b16d6693b29adbd8378c9a188631c4171c] :compileJava FAILED

If we edit that file again, remove the compile error, and insert a System.out.println instead, like this:

>     24     public static void main(String[] args) throws Exception {
>     25         System.out.println("I'm a little teapot!");
>     26         if(args.length < 1) {
>     27             args = new String[] {"help"};
>     28         }

Now, the build succeeds:

>     qbt build --package qbt.bin.main
>     Actually building qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00...
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] Linter complete without errors.
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :compileJava
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :processResources UP-TO-DATE
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :classes
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :compileTestJava UP-TO-DATE
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :processTestResources UP-TO-DATE
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :testClasses UP-TO-DATE
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :test UP-TO-DATE
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :check UP-TO-DATE
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :jar
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :simplePublishJars
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :sourcesJar
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :simplePublishSources
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] :simplePublish
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00]
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] BUILD SUCCESSFUL
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00]
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] Total time: 2.342 secs
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00]
>     [qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00] This build could be faster, please consider using the Gradle Daemon: https://docs.gradle.org/2.7/userguide/gradle_daemon.html
>     Built requested package qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00
>     Verified requested package qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00qbt build --package qbt.bin.main

If we wanted to run tests as well, we would add in the `--verify` flag.

>     qbt build --package qbt.bin.main --verify
>     Built requested package qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00
>     Actually building qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb...
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] Linter complete without errors.
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :compileJava UP-TO-DATE
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :processResources UP-TO-DATE
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :classes UP-TO-DATE
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :compileTestJava
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :processTestResources UP-TO-DATE
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :testClasses
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :test
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :check
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :jar
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :simplePublishJars
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :sourcesJar
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :simplePublishSources
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] :simplePublish
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb]
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] BUILD SUCCESSFUL
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb]
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] Total time: 2.538 secs
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb]
>     [qbt.bin.test@b0df6c0cf39132672064d5eace797ad3492adcdb] This build could be faster, please consider using the Gradle Daemon: https://docs.gradle.org/2.7/userguide/gradle_daemon.html
>     Verified requested package qbt.bin.main@c07eca30a34da75242774d6d3d6fe390dd3a7d00

Notice that qbt.bin/main built instantly - it did not print out "Actually building qbt.bin.main".  This is because the package was already built and a cache hit.  Because we asked only for a specific package initially, we didn't get a build of `qbt.bin.test` and no tests were run.  In QBT, tests are always in their own package so they can have classpath isolation.  The tests only ran when we included the --verify flag, which tells QBT to include verify dependencies.

We can now run the newly built artifacts directly by asking qbt to `runArtifact`.  This is a qbt feature that makes the test/code cycle very tight.  In a single command, we request that QBT builds an artifact and runs it (but like before, if it is already built, the version in the cache is used).  Another interesting benefit of `qbt runArtifact` is the binary used is copied from the cache to a temporary location, so you know it can never get out of date or corrupt.  No more wondering "maybe if I do a build clean, it will work".  With QBT, <b>every</b> build is clean.  Always.

With qbt.bin.main, there is one more detail that adds complexity here - there is no runnablebinary in this package!  The actual QBT binary is located in the release package, which uses the build tool `wrapper_generator` to create a runnable script, a "wrapper" around the main jar and its classpath.  Therefore, that is the package we have to request to run.  Becuase this package depends upon qbt.bin.main, it will get our new version of the code automatically.

>     $ qbt runArtifact --package qbt.release bin/qbt help
>     I'm a little teapot!
>     Available common commands:
>     ...[snip]

You can now edit the printed string in `QbtMain.java`, and rerun the `qbt runArtifact` command, and see your change take immediate effect.  Furthermore, if you disable overrides, you will see the original version is run instead.

>     $ NO_OVERRIDES=1 qbt runArtifact --package qbt.release bin/qbt help
>     Available common commands:
>     ...[snip]

Switching back and forth between the overriden version and the stock version doesn't incur any build time, because both versions are in the cache.  The cache is content-based and blazing fast.

### IDE Integrations

QBT has a special command designed to facilitate IDE integrations called "development protocols".  In short, a package can "opt in" to supporting various development protocols by providing a script that generates whatever metadata is needed.  There is a build tool already present in the main QBT manifest called `eclipse_gen` which does this for eclipse project files, and all QBT's java packages opt in to `eclipse_gen`.  If you are working in java, you can follow that same pattern.  If you would like to add support for any other IDE, you should follow that example.

To generate eclispe project files for your overrides, simply invoke the `qbt devProto` command.

>     $ qbt devProto --proto eclipse-gen --overrides

Our particular implemnetation of eclipse generation is very smart - if your project depends upon other projects that are also overrides, it will generate eclipse project files where the one project depends upon the other.  If it has dependencies which are not overridden, it will get built versions of those artifacts.  Additionally, devProto works even if certain package builds fail to compile, so you can build your IDE integration files even while things are broken.  The case where this is not possible is if you have broken packages that are not overridden, of course, because then their artifacts cannot be provided, so if you get failures try overriding any broken packages.

### About Packages and Artifacts

QBT was designed from a fundamentally different perspective than Maven, Gradle, and the many other build systems around.  Similarly to how Google's Bazel.io works (though this design was created and implemented before Bazel.io was open-sourced), QBT does not really produce persistent "artifacts" which are then depended upon by other packages.  Every artifact built by QBT is built "from source", unless it is a cache hit.  The cache is the closest thing QBT has to a "Maven Central".  It is possible for users (and especially useful for enterprise users) to configure a remote cache so that each uesr does not have to build each package from source the first time, but by default this is how QBT operates.

As a developer, you will frequently need artifacts.  If you just want to run the results of the build, it is better to use `qbt runArtifact`, especially since it ensures all your dependencies are present.  Eventually, however, you will have to produce a "distribution", or pass some artifacts to a QA person for testing.  Getting artifacts out of QBT is easy.  Here is how we produce a release of QBT.

>     $ qbt build --package meta_tools.release --output requested,directory,/home/cmyers/opt/%p-%v

This takes the artifacts of the `meta_tools.release` package, and places it in `/home/cmyers/opt/%p-%v` where `%p` is the name of the package and `%v` is the Cumulative Version of the package (a sha1 which includes the tree of the package and all of its dependencies, in addition to environment and other inputs).

Packages are the most granular unit for just about everything in QBT.  The smallest thing you can depend upon is one package artifact.  The smallest build you can perform is to build one package.  The smallest unit of classpath isolation is one package.  For this reason, you should consider carefully how you break up your code into packages.  Almost all java libraries should consist of at least 2 packages, "main" and "test", and java applications will probably have 3 or 4 - "main", "test", "bin", "release" - much like qbt.

When QBT builds a package that is not an override, it copies the package tree to a temporary location.  Packages in overridden repositories are built in place, but otherwise the build runs the same.  QBT then sets `$INPUT_ARTIFACTS_DIR` to point to a temporary location with all of the package's dependencies copied in.  It sets `$OUTPUT_ARTIFACTS_DIR` to point to a temporary, empty location where the package should deposit its results.  Finally, it invokes the `qbt-make` script.  This script can do whatever you want.  It should use tools in the package's dependencies to perform a build, consume other dependencies in the process, and produce artifacts in the `$OUTPUT_ARTIFACTS_DIR` location, and exit 0 for success, or non-zero for failure.  Checkout the complete manual for further details of this process.

Notice that meta_tools has a "release" package.  This is because the package `meta_tools.main` contains a jar we might wish to depend upon directly, so its artifacts are just the jar it builds.  It is best practice to create "release" packages which take the main jar(s) and all of the dependencies and put them together into a distribution.  They might also create a wrapper script (using, for example, `wrapper_generator`) to more easily run the artifact, and include other data like documentation, a license file, etc.

Here is the `qbt-make` file for the `meta_tools.main` package

>     #!/bin/bash
>     
>     eval export JAVA_HOME=\$JAVA_${QBT_ENV_JDK:-1_8}_HOME
>     
>     set -e
>     
>     if [ -d src ]; then "$INPUT_ARTIFACTS_DIR"/weak/qbt_fringe.linter.release/strong/qbt_fringe.linter.release/bin/linter -d src; fi
>     if [ -d test ]; then "$INPUT_ARTIFACTS_DIR"/weak/qbt_fringe.linter.release/strong/qbt_fringe.linter.release/bin/linter -d test; fi
>     "$INPUT_ARTIFACTS_DIR"/weak/3p.gradle/strong/3p.gradle/bin/gradle --stacktrace check simplePublish

Almost all Java packages look like this.  It uses Gradle to build the src or test directory, then runs the `simplePublish` target (which, if you consult the `build.gradle`, simply publishes to `$OUTPUT_ARTIFACTS_DIR/jars`).  The `build.gradle` also makes sure that the build pulls in the dependnecy jars from `$INPUT_ARTIFACTS_DIR`, which QBT sets up for you during the build, and nowhere else.  See how build tools are invoked from the dependencies as well?  Everything is versioned.

If we look at a release package like `meta_tools.release`, however, we see this.

>     #!/bin/bash
>     
>     eval export JAVA_HOME=\$JAVA_${QBT_ENV_JDK:-1_8}_HOME
>     
>     set -e
>     
>     $INPUT_ARTIFACTS_DIR/weak/qbt_fringe.wrapper_generator.release/strong/qbt_fringe.wrapper_generator.release/bin/wrapper_generator $INPUT_ARTIFACTS_DIR/weak/meta_tools.main/strong $OUTPUT_ARTIFACTS_DIR \
>         qbt qbt.QbtMain \
>     

The release package just uses the wrapper_generator to place the main jar, all of its dependencies, and a runnable script in the output directory.  If we wanted to, we could also copy documentation and license files into the output here, making an artifact which is ready to distribute.

Besides requesting the artifacts in a directory, you can request the tarball directly.  That looks like this:

>     $ qbt build --package meta_tools.release --output requested,tarball,/home/cmyers/opt/%p-%v.tar.gz

Because of the `%p` and `%v` parameters, you can list multiple packages, or even other package selection arguments like `--overrides` or `--repo` to get the artifacts of a large number of packages.

TODO: document packageGroovy to get all packages that end in `.release`?

### Creating a Commit

A good comparison to keep in mind is that a QBT workspace is a bit like a git repository with many "git submodules", except that QBT manages the submodules instead of git.  If you have ever worked with git submodules, you might be imagining a complex and unpleasant process where you have to make a commit in a submodule, then make a second commit in the parent repository, then ensure both commits get pushed at the same time otherwise the parent commit will be broken.  QBT handles all of this for you.  To make a commit, use `qbt status`,and `qbt commit`, and `qbt sdiff`.

>     $ qbt status -v
>     [qbt] dirty
>     [qbt] [status]  M bin/main/src/qbt/QbtMain.java
>     $ qbt commit -a -m "Explaining QBT's teapot nature"
>     [qbt] Committed e44dd25b5fd27384a92cf6c399a8b6a8a837240f
>     [meta] Committed a4d5e630e2d8b029d734729e6e4d8e934e1fc39d
>     $ qbt sdiff HEAD~ HEAD --log-diff
>     [qbt] commit > e44dd25b5fd27384a92cf6c399a8b6a8a837240f
>     [qbt] Author: Carl Myers <cmyers@cmyers.org>
>     [qbt] Date:   Thu Jan 28 08:06:20 2016 -0800
>     [qbt]
>     [qbt]     Explaining QBT's teapot nature
>     [qbt]
>     [qbt] diff --git qbt/HEAD/a/bin/main/src/qbt/QbtMain.java qbt/HEAD/b/bin/main/src/qbt/QbtMain.java
>     [qbt] index 2cabbc5..c1bb125 100644
>     [qbt] --- qbt/HEAD/a/bin/main/src/qbt/QbtMain.java
>     [qbt] +++ qbt/HEAD/b/bin/main/src/qbt/QbtMain.java
>     [qbt] @@ -22,6 +22,7 @@ public final class QbtMain {
>     [qbt]      }
>     [qbt]
>     [qbt]      public static void main(String[] args) throws Exception {
>     [qbt] +        System.out.println("I'm a little teapot!");
>     [qbt]          if(args.length < 1) {
>     [qbt]              args = new String[] {"help"};
>     [qbt]          }

If my change had impacted several different repositories, it would have looked the same except `qbt status` would have printed out multiple repositories that were dirty, and `qbt commit` would have made a commit in each satellite repository plus a master commit in meta.  `qbt sdiff` would have printed out each commit in the sattelites, along with their diff.

Just like you can specify a specific file to commit using the expanded form of git commit: `git commit -- Foo.java`, you can instruct `qbt commit` to only commit certain repositories.  You might have several dirty repositories checked out but want to split your changes into two commits.  You can do this:

>     $ qbt status
>     [qbt] dirty
>     [misc1] dirty
>     $ qbt commit --repo misc1 -a -m "Added API to shared library"
>     [misc1] Committed 4f24ec5684b0d65a9421024e9218d9b37719ed1a
>     [meta] Committed f9dd40dda4b9ef1070c25168b4075f1bb9cbad3d
>     $ qbt commit --repo qbt -a -m "Updating to use new API"
>     [qbt] Committed e44dd25b5fd27384a92cf6c399a8b6a8a837240f
>     [meta] Committed a4d5e630e2d8b029d734729e6e4d8e934e1fc39d

Furthermore, you can use the git index just like normal to split of changes within a repository.  By default, `qbt commit` only commits changes in the sattelites which are staged in the index using `git add`.  Just like `git commit` / `git add`, you can pass `qbt commit` the `-a` flag to tell it to commit all tracked changes, and `-A` to commit all files even incuding untracked files.  Be careful, running `qbt commit -A` with no `--repo` args is an easy way to unintentionally commit files in side repos.  To help avoid this, it is very important to keep your repositories "clean" by using `.gitignore` files in your repositories.  Be sure to ignore temporary files, eclipse generated artifacts, etc.

### Fork the Planet

To demonstrate the complete development cycle, you need somewhere to push your commits to.  This requires some one-time setup which this section will now outline.

In a normal git repository, in order to make a pull request, you first create your own personal "fork" of the repo, so you can push your changes there and request they be pulled.  Since QBT is all about cross-package and cross-repository consistency, you must instead fork the entire "repository set" - a given meta repository plus one of each of its sattelite repositories.  A repository set is everything necessary to build anything in the meta's manifest file.

First, you will need to create a fork of the meta repository.  After that, you will also need a place to store your "pins".  The easiest way to do this is to create an organization in github, so the package repositories don't make a "mess" in your github account.  If your github username is "newuser", we recommend using "NewUserQbt" as your organization, but that's just the convention we've been using, you are free to call it whatever you want (or litter your own github account with pin remotes, if you really want, but beware of name collisions!).

Log into Github and select "new organization" from the menu in the top right.  Let's say you created a Github organization called "NewUserQbt".  You will probably want to create a single repo in there called "meta", and if you want to submit pull requests back to others, you should probably fork this repo from someone else in the qbt meta network, such as [Amling](http://github.com/AmlingQbt/meta).

If you haven't already, you need to also generate a Github API token.  You can use standard SSH authentication for pushing and pulling, but if you want QBT to be able to create a repository for you, you will need an API token.  Go to [Create new Token](https://github.com/settings/tokens/new) and ensure your token has the `public_repo` permission.  Store it in a file in your home directory (like `.qbt-github-token`) and add it to your config file like the example above describes (see: `gitub_api_token`).  Alternatively, you could manually create a repository in your target prefix for each repository in the qbt-manifest file, or instruct QBT to put all pins in the same repo in your config file.  Since the string `%r` is replaced with the name of the repo, if you change `/%r.git` into `/pins.git` all repo pins will just go to the same place.

> NOTE: it has been observed that if you create your API token before you create the organization, it might not work (pushPins may get 401s).  If you experience problems, try generating a new token after creating your organization.

If you haven't already, you will need to create a "pin remote" entry in your config file, using the GithubQbtRemote pattern (see the examples for cmyers and amling, but be sure to include your token as well if you want repositories to be autovivified).

Because the `meta` repository is the metadata only, to store your code you need to store both a branch which contains meta, and the "package pins".  You can take the package pins you fetched earlier and push them now.

You will probably want to keep the "qbt remote name" and the "git remote name" the same, so you might need to rename the origin if you cloned meta from amling.  You might do something like this in your meta repository:

>     $ git remote rename origin amling
>     $ git remote add origin ssh://git@github.com/NewUserQbt/meta.git

You can then push your changes for a pull request.

>     $ git push origin HEAD:master
>     $ qbt pushPins origin --all

The first line pushes the `qbt-manifest` itself, and the second line pushes all your pins (and if your remote is a GithubQbtRemote, it will automatically create any repos that are missing).  In the future, if you leave off "--all", by default it will only push pins for your current overrides, but it never hurts to do `--all`.

You now have a complete fork of both metadata and package pins ready for making a pull request.  Note that when you make a pull request, github will only see the changes to `qbt-manifest`.  If you haven't followed the conventions we have outlined here, make sure you include in your pull request description where to fetch pins from.  Nobody can code review the the 1-line change in qbt-manifest that is just a sha1 changing from an old value to a new value, they need the satellite history as well!

### Receiving Pull Requests

So, what does it look like to receive a pull reuqest?  Let's go through that workflow as well.  After I receive a pull request, I fetch your branch of meta, check it out, add your pin remote to my config file, and run `qbt fetchPins` to get your pins.  If you don't check out the remote meta first, QBT doesn't know what pins it might need to fetch (for example, someone might have added a repository your manifest doesn't yet contain).  If I wanted to fetch your pins without checking out your meta, I could do this as a shortcut:

>     $ qbt fetchPins REMOTE --manifest <(git show REMOTE_BRANCH:qbt-manifest) -J --all --logLevel=DEBUG

This is a very handy trick if you want to just fetch a branch and merge it in to HEAD without looking first, maybe because you already looked at it on a different machine, or maybe because you are fetching a branch also written by you.

I can then do a build, examine the output, look at the diff, etc.  Then I can choose to accept your pull request or not.  Here is the complete workflow:

>     $ cd /path/to/meta
>     $ git fetch cmyers
>     $ git checkout some-remote-branch
>     $ qbt fetchPins cmyers --all
>     $ qbt updateOverrides --all # this tells git to make my overrides equal to the versions in the manifest
>     All update(s) successful
>     $ qbt sdiff origin/master HEAD # examine the code diff before building, for safety
>     $ git diff --no-ext-diff origin/master HEAD # examine the metadata diff - see what dependencies changes, what packages were added, etc
>     $ qbt build --all # this only builds things that aren't cache hits, as usual

For another option for viewing diffs, check out the section below about the "QBT diff driver".

If that `qbt build --all` command succeeds, I know for certain your change has not broken anything in my "software lifestream".  After examining the code, I decide your change is safe.  I can now merge your change and push it to the repo, closing the pull request.

So what if I wanted to review a pull request visually, like using the github website?  There is not currently an easy way to do this.  We have considered many different options for code review, but most of the core devs of QBT prefer to review changes locally anyways ("there is no such thing as review without confirming the build works anyways").  I think we would be open to adding a command to metatools, or a layer above it, that submitted a series of "faked up" pull requests in github to make it easier for people to comment on and accept those changes.  Alternatively, it could use an open source tool like reviewboard and submit reviews into an instance of that.  If you are interested in contributing a solution to this problem, please contact us.
 
### Plumbing:  updateManifest

Sometimes `qbt commit` is not granular enough.  Maybe you need to use the index and make several commits, or maybe you end up in a bad state because you accidentally committed things and you need to fix it.

You can make whatever commits you want directly in your overrides, then run `updateManifest` to update your manifest and create the pins necessary.

Our previous example, using `updateManifest` instead of `qbt commit`, would have looked like this:

>     $ qbt status -v
>     [qbt] dirty
>     [qbt] [status]  M bin/main/src/qbt/QbtMain.java
>     $ cd qbt && git commit -a -m"Explaining QBT's teapot nature"
>     $ cd ..
>     $ qbt updateManifest --repo qbt
>     Updated repo qbt from 89e0d238542f91d8f950b3375e44ba5382c318fa to e44dd25b5fd27384a92cf6c399a8b6a8a837240f...
>     All update(s) successful, writing manifest.
>     $ cd meta && git add qbt-manifest && git commit -m"Explaining QBT's teapot nature"
>     $ qbt sdiff HEAD~ HEAD --log-diff
>     [qbt] commit > e44dd25b5fd27384a92cf6c399a8b6a8a837240f
>     [qbt] Author: Carl Myers <cmyers@cmyers.org>
>     [qbt] Date:   Thu Jan 28 08:06:20 2016 -0800
>     [qbt]
>     [qbt]     Explaining QBT's teapot nature
>     [qbt]
>     [qbt] diff --git qbt/HEAD/a/bin/main/src/qbt/QbtMain.java qbt/HEAD/b/bin/main/src/qbt/QbtMain.java
>     [qbt] index 2cabbc5..c1bb125 100644
>     [qbt] --- qbt/HEAD/a/bin/main/src/qbt/QbtMain.java
>     [qbt] +++ qbt/HEAD/b/bin/main/src/qbt/QbtMain.java
>     [qbt] @@ -22,6 +22,7 @@ public final class QbtMain {
>     [qbt]      }
>     [qbt]
>     [qbt]      public static void main(String[] args) throws Exception {
>     [qbt] +        System.out.println("I'm a little teapot!");
>     [qbt]          if(args.length < 1) {
>     [qbt]              args = new String[] {"help"};
>     [qbt]          }

Like before, we end with pushing our meta and our pins.

>     $ git push origin HEAD:some-branch
>     $ qbt pushPins origin --all

It's worth noting that even though we created all the commits by hand, there was one very important extra step in there - `updateManifest` does not just insert the correct sha1s into the qbt-manifest file.  It also takes the sha1s from the sattelite repositories an inserts their "pins" into your "pin cache" so that `pushPins` can find them.  This step is necessary.

### The QBT Merge Driver and Diff Driver

You may have realized a huge problem with how QBT works... it breaks merge, cherry-pick and rebase in the meta repository!  Many development workflows require merges, rebases, or cherry-picks.  How can you do a merge whose conflict looks like this?

>     <<<<<<< LHS
>     version: "d7debf4dd4efaa8894baef7a8761891aa60093b8"
>     ||||||| MHS
>     version: "72c3de77068ead410d05367085b623c0942028d4"
>     =======
>     version: "6821aaa3e1b0d0a00677ea6c7b20ad61ad3d5db9"
>     >>>>>>> RHS

Now that you've seen what the conflict block looks like, perhaps the answer is more clear.  If you are doing a merge of a manifest, and the version of a repo changed on both sides of the merge, then you merge together their sha1 in the sattelite repository and use that new sha1 in the resulting manifest.  If that sounds like a royal pain, have no fear, because QBT has a built in git merge-driver to do it for you!

What is a git merge driver you might ask?  Git has a built-in facility for defining an external merge tool to use for certain files, whenever they change (whether there is a conflict or not).  This is great for files that are in a binary format, but when converted to some other format, merging them is possible.  This also works for the `qbt-manifest` file.  To configure the QBT merge driver, you should add the following to either your `meta/.git/config` file or your `~/.gitconfig` file, depending on whether you want it to be for all checkouts of meta or just the one.

>     [merge]
>         conflictStyle = diff3
>     [merge "qbt-manifest"]
>     	driver = qbt mergeDriver %A %O %B
>     [diff "qbt-manifest"]
>         command = qbt sdiffDriver  

The first block instructs git to use "diff3 style" conflict headers.  The default diff view only shows LHS and RHS, seeing MHS is *required* for correctness in general cases, and even when it is not strictly requried it can make conflicts easier to resolve (and you will need to do so in sattelites, on occasion).

The second block instructs git that there is a "merge driver" called `qbt-manifest` which should be invoked by running `qbt mergeDriver %A %O %B` (which is substituted in with paths to temporary files holding each side of the merge).  The merge driver will <b>only</b> run on files which have the "git attribute" saying they opt in to using that merge driver if available.  Check out the `.gitattributes` file in your meta repo, it looks like this:

>      /qbt-manifest merge=qbt-manifest diff=qbt-manifest

This is saying that the qbt-manifest file should be diffed using these tools, if they are available.  It would be a "remote execution vulnerability" if this file was the only thing needed to make git run this command, however, which is why you must "opt in" to it by adding to your git configuration.  Because you run your own copy of QBT, you can be sure nobody is running code you don't approve of on your machine.

It is important to note that the merge driver will actually look at what command you are running in meta and use that to inform what it does (rebase, merge, cherry-pick).  For `git pull`, however, it cannot know if you are doing a merge or a rebase pull.  For this reason you should probably never do a `git pull` in the meta repository, but for more details see the manual entry on `qbt mergeDriver`.

Finally, the diff driver is an optional bit you might like.  If you include that as well, running `git diff` in meta will actually print out a diff of each of your sattelites, similar to `qbt sdiff`.  Unfortunately, the output currently ignores any diffs in the manifest that are not in sattelites (like changing dependencies, etc), so you will still need to occasionally run "regular diff".  You can disable the diff driver temporarily by running `git diff --no-ext-diff`.  This can make code reviews much easier!

Here is some sample output with the diff driver:

>     $ git diff HEAD~ HEAD
>     diff --git org.cmyers.public/HEAD/a/qbt/qbtbuildtool.com/docroot/index.html org.cmyers.public/HEAD/b/qbt/qbtbuildtool.com/docroot/index.html
>     index 2d8792e..1e3c543 100644
>     --- org.cmyers.public/HEAD/a/qbt/qbtbuildtool.com/docroot/index.html
>     +++ org.cmyers.public/HEAD/b/qbt/qbtbuildtool.com/docroot/index.html
>     @@ -60,8 +60,8 @@
>            <div class="jumbotron">
>              <h1>QBT Build Tool</h1>
>              <p>See QBT manifest which contains the source for QBT here: <a href="https://github.com/AmlingQbt/meta/">https://github.com/AmlingQbt/meta/</a></p>
>     -        <p>Download a binary pre pre pre release of QBT to get started: <a href="qbt-release.tar.gz">qbt-release.tar.gz</a></p>
>     -        <p>Current QBT versionDigest: <!--#include file="qbt.versionDigest" --></p>
>     +        <p>Download a binary beta release of QBT and meta_tools to get started: <a href="meta_tools.release.tar.gz">meta_tools.release.tar.gz</a></p>
>     +        <p>Current QBT meta_tools.release CumulativeVersion: <!--#include file="meta_tools.release.digest" --></p>
>              <p><a href="docs/html/index.html">Current Documentation</a></p>
>            </div>
>      
>     diff --git org.cmyers.public/HEAD/a/qbt/qbtbuildtool.com/qbt-make org.cmyers.public/HEAD/b/qbt/qbtbuildtool.com/qbt-make
>     index 3f54aeb..d6f144b 100755
>     --- org.cmyers.public/HEAD/a/qbt/qbtbuildtool.com/qbt-make
>     +++ org.cmyers.public/HEAD/b/qbt/qbtbuildtool.com/qbt-make
>     @@ -3,6 +3,7 @@
>      set -e
>      
>      cp -rp docroot/* $OUTPUT_ARTIFACTS_DIR/
>     -(cd $INPUT_ARTIFACTS_DIR/weak/meta_tools.release/strong/meta_tools.release && tar -czf $OUTPUT_ARTIFACTS_DIR/qbt-release.tar.gz *)
>     +(cd $INPUT_ARTIFACTS_DIR/weak/meta_tools.release/strong/meta_tools.release && tar -czf $OUTPUT_ARTIFACTS_DIR/meta_tools.release.tar.gz *)
>     +cp $INPUT_ARTIFACTS_DIR/weak/meta_tools.release/strong/meta_tools.release/qbt.versionDigest $OUTPUT_ARTIFACTS_DIR/meta_tools.release.digest
>      cp -rp $INPUT_ARTIFACTS_DIR/strong/qbt.docs/docs $OUTPUT_ARTIFACTS_DIR/

With the QBT merge driver in place, you can merge, rebase, and cherry-pick in meta and it will "just work".  Whenever a revision (or dependency, or any other manifest metadata) is changed on just one side or the other, it will accept the change and re-generate a canonical `qbt-manifest` file.  Whenever the change happens on both sides, the merge driver will try to merge the two sides in the sattelite.  If those changes conflict, it will generate the conflict headers and drop you into a shell to fix it.  If this happens, there is even a QBT command designed to help you "do the right thing".  After the merge driver drops you to a shell, run `qbt resolveManifestConflicts`, it will help you resolve the conflict in an interactive way.

### Summary 

You know know everything necessary to use QBT for normal development.  Like git, QBT is a complex tool, and you will be well served by thinking of it as a continuous learning process.  This is just the start of your journey to reliable, fast, reproducible builds!

    vi: ft=markdown
