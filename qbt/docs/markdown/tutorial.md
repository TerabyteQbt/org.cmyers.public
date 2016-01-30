# QBT - Tutorial

This document covers similar content to the [Quick Start Guide](quick-start.html) and the [Development Guide](development-guide.html) except in far greater detail.

## Explaining the `qbt-manifest`

To build anything with QBT, you need a "qbt-manifest" file.  This file tells QBT what packages exist, what repositories they live in, what dependencies they have, and what revision of the repository to use.  When using QBT in the recommended way (with metatools), it is further assumed that the manifest file is checked into a git repository for versioning.  You can think of the meta repository like a git repository with many "git submodules", except instead of using git to manage the submodules, you use QBT and metatools.

The `qbt-manifest` file format is versioned and newer versions of QBT can read older manifest files, but in doing so they will upgrade it to the newest version of the manifest.  For this reason, you probably want to keep QBT as up-to-date as possible, but it is most important that all collaborators on a given manifest file use the same version of QBT.

As of this writing, the qbt-manifest format is a json-like format that looks like this:

>     @2
>     {
>         "misc1": {
>             "packages": {
>                 "misc1.commons.algo.main": {
>                     "metadata": {
>                         "archIndependent": true,
>                         "prefix": "commons.algo/main",
>                         "qbtEnv": {
>                             "JDK": 1
>                         }
>                     },
>                     "normalDeps": {
>                         "3p.gradle": "Weak,HEAD",
>                         "mc.com.google.guava.guava": "Strong,HEAD",
>                         "qbt_fringe.linter.release": "Weak,HEAD"
>                     },
>                     "verifyDeps": {
>                         "misc1.commons.algo.test/test": 1
>                     }
>                 },
>                 "misc1.commons.algo.test": {
>                     "metadata": {
>                         "archIndependent": true,
>                         "prefix": "commons.algo/test",
>                         "qbtEnv": {
>                             "JDK": 1
>                         }
>                     },
>                     "normalDeps": {
>                         "3p.gradle": "Weak,HEAD",
>                         "mc.junit.junit": "Strong,HEAD",
>                         "misc1.commons.algo.main": "Strong,HEAD",
>                         "qbt_fringe.linter.release": "Weak,HEAD"
>                     }
>                 },
>             "version": "4f24ec5684b0d65a9421024e9218d9b37719ed1a"
>         },
>         "some_other_repo": {
>         ...
>         }
>     }

Here we see the manifest version is "2".  The QBT used to read this manifest must support at least version 2.  This snippet of manifest defines a repository called `misc1` which contains many packages, only two are included for brevity.  `misc1.commons.algo.main` and `misc1.commons.algo.test`.  Package names are a global namespace and need not start with the repo name - this makes moving packages between repositories easy - but since it is a global namespace, it is best to use longer, descriptive prefixes whenever possible (i.e. `org.mycompany.myproject.main`).  The main package lives in the `commons.algo/main` directory of the repository, and requires a JDK.  To learn more about how JDK versioning works, see [Build Environments](build-environemnts.html).

This main package has several dependencies.  It depends upon the `3p.gradle` and `qbt_fringe.linter.release` packages as "Weak" dependencies.  A "Weak" dependency is typically used for compile time tools dependencies, it is not necessary that this package use the exact same version of gradle or the linter as all of its dependencies.  This package also depends upon the `mc.com.google.guava.guava` package, however, and this is a "Strong" dependency.  This means that the entire transitive closure of strong dependencies must all use the same version of guava in building.  To learn more about this, see [Advanced Dependency Management](advanced-dependencies.html).

The main package also lists a "verifyDeps" section (which is optional).  Verify dependencies are edges on the graph which are only followed when a verify is requested, and may be cyclical.  They are ideal for saying "this package tests me", as is the case in this example.  Notice that the test package depends upon the main package (because how can you test code you don't have access to?), however, the main package also lists a verify dependency on the test package.  This powerful language for relaying dependency between two packages lets QBT do some very smart and powerful things.

Notice also that the test package depends upon "mc.junit.junit", but the main package does not.  This is how test dependencies must be modeled.  Because each package is a classpath, and there is no classpath separation within packages, tests should be separated out into their own package.  This has many benefits.  First off, you do get classpath separation and know your test dependencies will not be linked or distributed with your production code.  Additionally, as soon as the main package completes, other packages that depend upon it can run while this package's tests are running (since they are in a separate package).  Finally, it is easy to specify which tests you want to run because they are just packages you can request (or not request) like any other.  An error that only manifests in the test package will not stop QBT from trying to continue to build other packages which depend upon main, which might let you see more errors.

Finally, at the end of the repository block, you see the "version", which is simply the git sha1 hash of the commit currently in the manifest.  This is the version updated by `qbt commit` and `qbt updateManifest`.

## Explaining the `qbt-config`

Below is a complete sample `qbt-config` file:

>     /* qbt-config.sample: This file is an example qbt-config file.
>      *
>      * The qbt-config file is a groovy file that returns a QbtConfig object when
>      * evaluated.  The QbtConfig object can be customized to accomplish a wide
>      * variety of things, but primarily it tells qbt where to store repo and
>      * artifact caches, and where to find pins
>      *
>      * Settings you may want to change are marked with //KNOB
>      * Settings you MUST set are marked with //TODO
>      */
>     import com.google.common.collect.ImmutableList;
>     import java.nio.file.Paths;
>     import qbt.artifactcacher.CompoundArtifactCacher;
>     import qbt.artifactcacher.LocalArtifactCacher;
>     import qbt.config.CompoundQbtRemoteFinder;
>     import qbt.config.FormatLocalRepoFinder;
>     import qbt.config.FormatQbtRemoteFinder;
>     import qbt.config.MapQbtRemoteFinder;
>     import qbt.config.QbtConfig;
>     import qbt.pins.SimpleLocalPinsRepo;
>     import qbt.remote.FormatQbtRemote;
>     import qbt.remote.GithubQbtRemote;
>     import qbt.vcs.VcsRegistry;
>     
>     
>     def dotQbt = Paths.get(System.getenv("HOME")).resolve(".qbt"); //KNOB: this is where QBT will store its cache.
>     def gitRemoteVcs = VcsRegistry.getRawRemoteVcs("git");
>     def gitLocalVcs = gitRemoteVcs.getLocalVcs();
>     //def token = new File('/home/cmyers/.github-api-token').text.trim(); //KNOB: if you want to use a GithubQbtRemote, fill this in with your github API token.  This is only needed to create repositories, push and pull use your ssh key.
>     
>     
>     return new QbtConfig( //KNOB: if you are setting QBT up for use in an enterprise, you can extend QbtConfig with MyCompanyQbtConfig that sets all the defaults, making this file effectively 1-2 lines.
>     
>     /* The FormatLocalRepoFinder tells QBT where to put your overrides (i.e. local
>      * checkouts of repositories you are working on).  This default will simply
>      * place them "next to" your checkout of meta, making the directory that
>      * contains the meta checkout effectively the root of your workspace.  For best
>      * results, you probably want to put a .qbt-meta-location file in that
>      * directory as well.
>      *
>      * Note:  Something fancy is going on here.  By setting the environment
>      * variable "NO_OVERRIDES=1", we can temporarily disable overrides.  This is a
>      * handy trick!
>      */ 
>          new FormatLocalRepoFinder(
>              gitLocalVcs,
>              workspaceRoot.resolve(System.getenv("NO_OVERRIDES") ? "/dev/null" : "../%r").toString(), //KNOB: where to put overrides
>          ),
>     
>     /* The SimpleLocalPinsRepo object tells QBT where to put your pins.  Pins are
>      * git refs for the various sattelite repositories referred to in your
>      * manifest.  They function as a cache of the repositories.  It is safe to use
>      * the same pin cache across all workspaces, because for any single pushPins
>      * only the pins in your qbt-manifest are sent.  If you wanted to, however, you
>      * could still use a per-workspace pins directory.  The default below makes a
>      * shared one in your .qbt directory.
>      */
>          new SimpleLocalPinsRepo(
>              gitRemoteVcs,
>              dotQbt.resolve("pins/v1"),
>          ),
>     
>     /* The QbtRemote interface tells QBT how to find pins in git remotes.  Here we
>      * demonstrate how to create a list of several remotes which can be used just
>      * like git remotes.
>      *
>      * CompoundQbtRemoteFinder: Takes a list of QbtRemotes, and tries each one in series
>      * MapQbtRemoteFinder: takes a map of "name" to QbtRemote impls
>      *
>      * FormatQbtRemote: Simplest remote, takes a git URL with "%r" substituted in for repository name (optionally).
>      *
>      * GithubQbtRemote: Similar to FormatQbtRemote but github specific, this impl can "autovivify" repositories as well, if they do not exist, on push.
>      *
>      */
>          new CompoundQbtRemoteFinder([
>              new MapQbtRemoteFinder([
>     /* //TODO: you must configure your own remote in order to pushPins anywhere.  You can use a FormatQbtRemote or a GithubQbtRemote as the examples below show.
>                  origin: new FormatQbtRemote(
>                      gitRemoteVcs,
>                      "ssh://git@git.example.com/somerepo/%r.git"
>                  ),
>     */
>                  cmyers: new GithubQbtRemote( //KNOB: you may want to remove these, they are here so you can pull from the QBT core devs.
>                     gitRemoteVcs,
>                     null,
>                     "TerabyteQbt",
>                 ),
>                  amling: new GithubQbtRemote(
>                     gitRemoteVcs,
>                     null,
>                     "AmlingQbt",
>                 ),
>              ]),
>              new FormatQbtRemoteFinder( //KNOB: this is optional, but having it lets you put a raw URL straight into the command like, e.g. "qbt fetchPins ssh://git@git.example.com/foo/%r.git"
>                  gitRemoteVcs,
>              ),
>          ]),
>     
>     /* The CompoundArtifactCacher lets you configure artifact caching.  This is how
>      * QBT stores artifacts after building them, and finds already-built artifacts
>      * to avoid rebuilding them.  There are ArtifactCacher implementations
>      * available which support remote (shared across machine) caches as well.
>      */
>          new CompoundArtifactCacher(
>              ImmutableList.of(
>                  new LocalArtifactCacher(
>                      dotQbt.resolve("artifacts/v1"), //KNOB: where to put the artifact cache
>                      5L * (1024 * 1024 * 1024) //KNOB: how big to let the artifact cache grow to (default: 5GB)
>                  ),
>              ),
>          ),
>      );

Your `qbt-config` file should generally not be checked into source control, because it often contains crednetials, local file paths, individual preferences, etc.  If you are setting up QBT for use at a large enterprise, you may wish to check in a global config file for people to use, but give them an option to easily override it.  This can be done easily because the `qbt-config` file is just Groovy, so you can write arbitrary code.

For example, this config file does nothing except include the contents of some other file:

>     import java.nio.file.Paths;
>     return evaluate(Paths.get(System.getenv("HOME")).resolve("qbtconfig").toFile())

> NOTE: there is a known issue with groovy where you cannot evaluate a file that contains characters like "." or "-" in its name.

In this way, you can build `qbt-config` files that include other files, or create files which are shared across workspaces.

## Building

QBT builds can be thought of as a simple cache lookup, where a cache miss causes a build to populate the cache.  By default, QBT uses parallelism equal to twice the number of CPUs present, and even after a package fails QBT will keep building until it cannot build anymore (i.e. until every package is built, or the only packages not built have dependencies which cannot build due to errors).  Here is some sample error output:

>     Verified requested package mc.com.google.guava.guava.lc@e64ab20ee8bf335b5d04eb7062e77b4f0f23f593
>     Verified requested package meta_tools.main@6a20c80befbe445097c4ced6a24901e3d2ed9112
>     Verified requested package misc1.third_party_tools.test@c03b1e275a87499e5cc50938607d650304eddeb4
>     Verified requested package mc.org.codehaus.groovy.groovy.all.lc@66dfcdaf2663759eaccc420ba99e4683c4e530d9
>     Built requested package qbt.release@7be50fbb733dfea31ea1462fbb3c0dad27699bad
>     Verified requested package qbt.release@7be50fbb733dfea31ea1462fbb3c0dad27699bad
>     Verified requested package mc.org.eclipse.jetty.jetty.xml.lc@8d4ab887690f7392ecbf9b46397eac33a1546595
>     Built requested package meta_tools.release@002e298f47dbdbef024769fa8cf47e5d0360eb6b
>     Verified requested package meta_tools.release@002e298f47dbdbef024769fa8cf47e5d0360eb6b
>     Actually building qbt.docs@860b5d33b5094375d85865913522dd121f00effc...
>     Built requested package misc1.third_party_tools.release@01e6ba0ffd3a0f9ed162507e86ffb033ddcb850b
>     Verified requested package misc1.third_party_tools.release@01e6ba0ffd3a0f9ed162507e86ffb033ddcb850b
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] using output dir: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/bak.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/bak.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/contents.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/contents.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/development-guide.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/development-guide.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/index.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/index.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/install.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/install.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/old-getting-started.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/old-getting-started.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/quick-start.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/quick-start.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/tutorial.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/tutorial.html
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Processing file: /home/cmyers/projects/1/org.cmyers.public/qbt/docs/markdown/why-qbt.md
>     [qbt.docs@860b5d33b5094375d85865913522dd121f00effc] Creating file: /tmp/wgpjRDcO/5920878843180692673/artifacts/docs/html/why-qbt.html
>     Built requested package qbt.docs@860b5d33b5094375d85865913522dd121f00effc
>     Verified requested package qbt.docs@860b5d33b5094375d85865913522dd121f00effc
>     Actually building qbt.qbtbuildtool.com@af5c6010b6630e32432cdbb132ac8f57892f1255...
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] Linter complete without errors.
>     Built requested package qbt.qbtbuildtool.com@af5c6010b6630e32432cdbb132ac8f57892f1255
>     Verified requested package qbt.qbtbuildtool.com@af5c6010b6630e32432cdbb132ac8f57892f1255
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] /home/cmyers/projects/1/org.cmyers.public/nappysak/main/src/org/cmyers/nappysak/layout/CircularTable.java:15: error: CircularTable is not abstract and does not override abstract method getNeighborBucketsAndWeights(Integer) in Layout
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] public class CircularTable extends Struct<CircularTable, CircularTable.Builder> implements Layout {
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]        ^
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] /home/cmyers/projects/1/org.cmyers.public/nappysak/main/src/org/cmyers/nappysak/layout/CircularTable.java:37: error: incompatible types: SimpleStructKey<CircularTable,ImmutableSalvagingMap<Integer,Maybe<PreferencedEntity>>> cannot be converted to SimpleStructKey<CircularTable,ImmutableSalvagingMap<String,Maybe<PreferencedEntity>>>
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]         b.add(BUCKET_MAP = new SimpleStructKey<CircularTable, ImmutableSalvagingMap<Integer, Maybe<PreferencedEntity>>>("bucketMap", ImmutableSalvagingMap.of()));
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]                            ^
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] /home/cmyers/projects/1/org.cmyers.public/nappysak/main/src/org/cmyers/nappysak/layout/CircularTable.java:53: error: method does not override or implement a method from a supertype
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     @Override
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     ^
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] /home/cmyers/projects/1/org.cmyers.public/nappysak/main/src/org/cmyers/nappysak/seater/SeaterEngine.java:32: error: type argument T#1 is not within bounds of type-variable T#2
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     private final UtilityFunction<Long, T> uf;
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]                                         ^
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]   where T#1,T#2,U are type-variables:
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     T#1 extends PreferencedEntity declared in class SeaterEngine
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     T#2 extends UtilityEntity<U> declared in interface UtilityFunction
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     U extends Object declared in interface UtilityFunction
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] /home/cmyers/projects/1/org.cmyers.public/nappysak/main/src/org/cmyers/nappysak/seater/SeaterEngine.java:41: error: type argument T#1 is not within bounds of type-variable T#2
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]         uf = new NeighborUtilityFunction<T>(false);
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]                                          ^
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]   where T#1,T#2 are type-variables:
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     T#1 extends PreferencedEntity declared in class SeaterEngine
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]     T#2 extends UtilityEntity<Long> declared in class NeighborUtilityFunction
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] 5 errors
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] :compileJava FAILED
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1] FAILURE: Build failed with an exception.
>     [org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1]
>     <SNIP>...
>     Exception in thread "main" java.lang.RuntimeException: Build for org.cmyers.nappysak.main@3dc64e085c5a6819ff67d5599a828c346495e9c1 returned non-zero: 1
>             at qbt.build.BuildUtils.runNormalBuild(BuildUtils.java:220)
>             at qbt.build.BuildUtils.runBuild(BuildUtils.java:142)
>             at qbt.build.PackageMapperHelper$1.runBuildFailable(PackageMapperHelper.java:79)
>             at qbt.mains.BuildPlumbing$1$1.map(BuildPlumbing.java:197)
>             at qbt.mains.BuildPlumbing$1$1.map(BuildPlumbing.java:192)
>             at qbt.recursive.rd.RecursiveDataComputationMapper.lambda$map$22(RecursiveDataComputationMapper.java:12)
>             at qbt.recursive.utils.RecursiveDataUtils.lambda$computationTreeMap$51(RecursiveDataUtils.java:44)
>             at misc1.commons.concurrent.ctree.ComputationTree.lambda$transform$10(ComputationTree.java:63)
>             at misc1.commons.concurrent.ctree.ComputationTreeComputer$Status.lambda$null$1(ComputationTreeComputer.java:62)
>             at misc1.commons.Result.newFromCallable(Result.java:145)
>             at misc1.commons.concurrent.ctree.ComputationTreeComputer$Status.lambda$checkStart$2(ComputationTreeComputer.java:57)
>             at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
>             at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
>             at java.lang.Thread.run(Thread.java:745)

Notice that the stack trace at the bottom is from QBT itself, explaining how the package failed to build (it returned non-zero, of course).  This message could be burried elsewhere in the output, if QBT was able to continue building.  You may want to just run the build again, since all the packages qbt can build are built, the error should not be at the end out of the output.  There may be multiple errors for different packages, if there is more than one failing package which can be reached.  What you want to do is take the name of the package from the stack trace (in this instance, `org.cmyers.nappysak.main`, and search the output for lines that start with that (i.e. `[org.cmyers.nappysak.main`).  All output of that build will be tagged with that by default (although you can customize the log output if you wish, see [Customizing Log Output](customizing-log-output.html)).

A build with no errors and all hits will simply look like this:

>     Built requested package mc.org.apache.velocity.velocity@3aba937724b5067c3da40214820f8b8901c66463
>     Verified requested package mc.org.apache.velocity.velocity@3aba937724b5067c3da40214820f8b8901c66463

Two lines will occur for every package, one saying it is built and one saying it is "verified".  Note that unless you specifically requested `--verify`, the verify step is a no-op.  The build itself will return 0 on success, non-zero on failure.

You can request which packages you want built using a variety of complex options.

>     NO_OVERRIDES=1 qbt build --all --verify

This is the standard "build everything, test everything" invocation.  This presumes you have the "NO_OVERRIDES=1" trick from the sample `qbt-config`.  If this build command succeeds, you know every package in your manifest compiles and successfully tests.

>     qbt build --all --verify

This is the same as before except if you have any overrides in your workspace, it will build them instead.  If this build command succeeds, you know every package in your workspace compiles and successfully tests, and hasn't broken anything else in your manifest (whether overridden or not).

>     qbt build --overrides --verify

This will build all packages contained in all repositories you have overridden, and also run their tests (even if those tests are not present in overrides).  This will not build other packages that depends upon your overridden packages, but which are not overridden themselves).  To do that, you'd want to instead run:

>     qbt build --overrides --outwards --verify

The `--outwards` option tells build to operate on the specified packages, and additionally include any packages which depend upon those packages directly or indirectly.  This is the standard "See if anything broke" invocation without having to build the entire manifest.

Finally, the most powerful way to select packages to operate on looks like this:

TODO: I have no clue how --groovyPackages works, and I can't for the life of me figure it out reading the code.  FML.

## Creating Repositories, Packages, and Dependencies

Presently, there are no qbt commands to manupulate the manifest file to add or remove packages, repositories, or dependencies, so it requires a bit of manual editing of the `qbt-manifest` file.

Any time you manually edit the manifest file, be sure to run `qbt updateManifest` to validate the file.  If successful, it will write the file back out, ensuring that ordering, whitespace, etc is all "canonical", and no whitespace diffs will be introduced in the future.  For this reason, it is very important not to forget to do this.

To add a repository, add a new block at the end of the manifest file like this:

>         },
>         "new_repo_name": {
>             "version": "0000000000000000000000000000000000000000"
>         }
>     }

Be sure to leave off / add trailing commas correctly.  Since this is JSON, the final element in lists and hashes must not be proceeded by a comma.

You can then `git init new_repo_name` in your workspace, and start coding.  For the first commit only, you will have to manually create a pin.  To do this, first run `git commit` in the repository.  Next, run this command:

>     $ (export REPO_NAME="new_repo_name"; export COMMIT="$(git -C $REPO_NAME rev-parse HEAD)"; git init ~/.qbt/pins/v1/$REPO_NAME && git -C $REPO_NAME push ~/.qbt/pins/v1/$REPO_NAME ${COMMIT}:refs/qbt-pins/$COMMIT)
>     Initialized empty Git repository in /home/cmyers/.qbt/pins/v1/new_repo_name/.git/
>     Counting objects: 3, done.
>     Writing objects: 100% (3/3), 203 bytes | 0 bytes/s, done.
>     Total 3 (delta 0), reused 0 (delta 0)
>     To /home/cmyers/.qbt/pins/v1/new_repo_name
>      * [new branch]      c1b0bba63e483c29d9b4eb385dc770b7d7065ba5 -> refs/qbt-pins/c1b0bba63e483c29d9b4eb385dc770b7d7065ba5

Now, update the qbt-manifest version to be the new sha1 "c1b0bba63e483c29d9b4eb385dc770b7d7065ba5", and run `updateManifest`.  It should work.

>     $ qbt updateManifest --repo new_repo_name
>     All update(s) successful, writing manifest.

Your changes will no longer be at the end of the `qbt-manifest` file, it will be canonicalized and sorted.

Creating repositories is pretty messy because of pins, but creating packages is trivially easy.  To create packages in an existing repository, simply add a package hash to the repository hash (if it doesn't already exist) and put a package's metadata in it.  In this example we add two packages, a "main" and a "test", both of which require Java.
>         "new_repo_name": {
>             "packages": {
>                 "new_package.main": {
>                     "metadata": {
>                         "archIndependent": true,
>                         "prefix": "relative/path/from/repo/root/main",
>                         "qbtEnv": {
>                             "JDK": 1
>                         }
>                     },
>                     "normalDeps": {
>                         "3p.gradle": "Weak,HEAD",
>                         "mc.com.google.guava.guava": "Strong,HEAD",
>                         "qbt_fringe.linter.release": "Weak,HEAD"
>                     },
>                     "verifyDeps": {
>                         "new_package.test/test": 1
>                     }
>                 },
>                 "new_package.test": {
>                     "metadata": {
>                         "archIndependent": true,
>                         "prefix": "relative/path/from/repo/root/test",
>                         "qbtEnv": {
>                             "JDK": 1
>                         }
>                     },
>                     "normalDeps": {
>                         "3p.gradle": "Weak,HEAD",
>                         "mc.junit.junit": "Strong,HEAD",
>                         "new_package.main": "Strong,HEAD",
>                         "qbt_fringe.linter.release": "Weak,HEAD"
>                     },
>                 },
>             "version": "c1b0bba63e483c29d9b4eb385dc770b7d7065ba5"
>         },


Both `normalDeps` and `verifyDeps` are optional if empty.  If your package does not require Java, leave off the `qbtEnv' hash.

Changing dependencies at this point is probably pretty obvious, simply find the package you wish to alter in the `qbt-manifest` file and add or remove dependnecy lines.  Rerun `qbt updateManifest` to "canonicalize" the manifest (ensure the lines are in the correct order, the commas are correct, etc).

Note that the `qbt commit` command is very picky about the state of your meta repository.  If you have changes to your manfiest that are not committed, it will refuse to run (lest it unintentionally overwrite something you don't have committed).  For this reason, it is best to commit the metadata changes first, then invoke `qbt commit` to commit your new package revision.  You can use `qbt commit --ammend` to roll it in to your previous commit.

    vi: ft=markdown

## About Pins and Pin Remotes

As we already saw, one of the ugly complexities which QBT generally tries to hide from you, but which cannot always remain hidden, is pins.  Pins are how QBT can "find" commits in various sattelite repositories.  Depending on how your write your configuration file, pins may be stored locally in various different ways, and may be pushed to a remote in different ways as well.  Just as you can push several different branches from several different repositories into the same repository, you can push all your pins to the same local or remote repository, if you desire.  Github seems to have no scaling problems that we have noticed so far, but other backends might perform poorly with hundreds or thousands of unrelated branches in the same repository, so for this reason our recommended configuration stores pins for different repositories in different locations in Github.  This also lets you manually push "normal refs" that other users could read, if you wanted to.

So what do pins look like?  You can examine them yourself using `git ls-remote`.

>     $ git ls-remote ~/.qbt/pins/v1/qbt | head
>     0248696f815ab86beb8c94aba446125d1ee2b16f        refs/qbt-pins/0248696f815ab86beb8c94aba446125d1ee2b16f
>     033b601bceb6eff3ddcde1432e9e45fa3d93ef09        refs/qbt-pins/033b601bceb6eff3ddcde1432e9e45fa3d93ef09
>     035df1a71b73b061ebcaef3addf3b20097ccb672        refs/qbt-pins/035df1a71b73b061ebcaef3addf3b20097ccb672
>     04e852e913fc10182eafb486d4113da6713c2238        refs/qbt-pins/04e852e913fc10182eafb486d4113da6713c2238
>     05ac8fb0e96bfc225ad05b81f91d60153a0a12ab        refs/qbt-pins/05ac8fb0e96bfc225ad05b81f91d60153a0a12ab
>     082fada66b6bb74ed7de640b97136e67a7099ca3        refs/qbt-pins/082fada66b6bb74ed7de640b97136e67a7099ca3
>     094286c256fb68b856d84558e3688b4f5d50fd14        refs/qbt-pins/094286c256fb68b856d84558e3688b4f5d50fd14
>     096c8a8966c7a8f0b040086a35a0ca10abad3b29        refs/qbt-pins/096c8a8966c7a8f0b040086a35a0ca10abad3b29
>     098e2bbbe1d7484df254082b3df62c35eddcd036        refs/qbt-pins/098e2bbbe1d7484df254082b3df62c35eddcd036
>     09c79988ede27a3b3b28dc4302b94bf118e3528b        refs/qbt-pins/09c79988ede27a3b3b28dc4302b94bf118e3528b

Similarly, pins on the server look the same:

>     $ git ls-remote ssh://git@github.com/AmlingQbt/qbt | head
>     0248696f815ab86beb8c94aba446125d1ee2b16f        refs/qbt-pins/0248696f815ab86beb8c94aba446125d1ee2b16f
>     033b601bceb6eff3ddcde1432e9e45fa3d93ef09        refs/qbt-pins/033b601bceb6eff3ddcde1432e9e45fa3d93ef09
>     035df1a71b73b061ebcaef3addf3b20097ccb672        refs/qbt-pins/035df1a71b73b061ebcaef3addf3b20097ccb672
>     04e852e913fc10182eafb486d4113da6713c2238        refs/qbt-pins/04e852e913fc10182eafb486d4113da6713c2238
>     05ac8fb0e96bfc225ad05b81f91d60153a0a12ab        refs/qbt-pins/05ac8fb0e96bfc225ad05b81f91d60153a0a12ab
>     082fada66b6bb74ed7de640b97136e67a7099ca3        refs/qbt-pins/082fada66b6bb74ed7de640b97136e67a7099ca3
>     094286c256fb68b856d84558e3688b4f5d50fd14        refs/qbt-pins/094286c256fb68b856d84558e3688b4f5d50fd14
>     096c8a8966c7a8f0b040086a35a0ca10abad3b29        refs/qbt-pins/096c8a8966c7a8f0b040086a35a0ca10abad3b29
>     098e2bbbe1d7484df254082b3df62c35eddcd036        refs/qbt-pins/098e2bbbe1d7484df254082b3df62c35eddcd036
>     09c79988ede27a3b3b28dc4302b94bf118e3528b        refs/qbt-pins/09c79988ede27a3b3b28dc4302b94bf118e3528b

We call a git remote which contains pins a "pin remote".  Git stores "normal" branches in "refs/heads/NAME".  By default, it doesn't show things that are outside of the "refs/heads" prefix.  QBT takes advantage of that to store pins in "refs/qbt-pins/NAME".  Because there is no way to ask for a commit by sha1 in the git protocol, we have to pin *every* commit that appears in *every* revision of *every* manifest.  We keep pins outside of `refs/heads/` because otherwise it might negatively impact performance of fetches, clones, etc.  Because if the commit is pinned, we can request it directly, fetching pins can be very fast.  Only if the commit isn't pinned do we have to try fetching all the pins and checking their history to find it.

>     $ git push new_git_remote HEAD:master
>     $ qbt pushPins new_qbt_remote

As we saw above when creating a new repository, if you ever have to manually create a pin, all you have to do is push the commit to refs/qbt-pins/X where X is the commit's sha1.  Whether you are creating it locally in your pin cache or remotely, the process is the same.  This is exactly what updateManifest does for you (in addition to updating the manifest file).  `qbt pushPins` and `qbt fetchPins` simply tries to push or fetch each pin listed in the manifest file from/to your pin cache.

Pins solve two problems.  The first problem they solve is one of the biggest problems with git submodules - that it is possible to push a commit that refers to a sha1 in a submodule which has not been pushed.  The only way for that to happen with QBT is if you forget to run pushPins, and the cause will be immediately obvious.  The second problem they solve is enabling disconnected development.  Becuase pin caches keep the complete history of all of your commits in meta (as long as you don't crosswind any sattelites - don't do that!), a simple `git fetch` in meta followed by a `qbt fetchPins` ensures you have all the necessary data cached to examine or build any version of any package in the entire history of your manifest file, without needing any network.  This is much more difficult to do when using more traditional dependency managers like Ivy, Maven, or Gradle.  While many have a "cache dependencies" command, you would have to run it for every revision in history to get the same effect, and the artifacts could be very very large.

Sometimes people worry that the number of pins will explode and eventually fetching pins will be very slow.  We haven't run into problems yet, but there are already some ideas for how to improve the situation.  If you enforce that sattelites can *never* do a non-fast-forward change in a manifest (i.e. each revision of the manifest, for all revisions of all repositories, is a fast-forward of the revision of that repository in the previous version), then we only need to keep pins for all current versions of the manifest (because they include the previous commits in them).

We can also "fake up" pins, by creating fake commits that list many "leaf commits" as their parents, to keep more commits while having to fetch fewer pins.  All of these things could be done purely server-side, as a cleanup process, and would require no changes to QBT or how it fetches or pushes pins.

## Crafting QBT Commits

You have probably already learned about `qbt commit` from the [Quick Start Guide](quick-start.html).  There are some very powerful options available for crafting QBT commits, patterned after Git itself.

Before DVCS, most source control systems recognized two states.  "committed" and "modified".  When you check out a commit, everything is "committed".  As you make modifications, those files are "modified" and can be diffed against the original commit.  Git adds the index to this.  With Git, these files are not just "modified", they are "modified but unstaged".  New files you create are "untracked".  Only after invoking `git add` on the file does it become "staged for commit".  In older source control systems, the commit step "does all the work", but with git, really, each time you invoke "git add" you are changing the "commit to be" which represents what is about to be committed.  Running `git commit` just slaps a name on it and updates some pointers - the actual data is already added to the object database.

So in summary, git has three stages, unstaged, staged, and committed.  QBT explodes this into 6 states, although just like git, users can safely ignore some of these unless they want to use them (and that is why `qbt commit` exists, to help you ignore them when you don't need them).  The reason for 6 stages is, there are always at least two repositories involved in every commit - meta, and one or more sattelites.  So the complete list of stages is unstaged, staged, sattelite ahead, manifest unstaged, manifest staged, committed.  Let's look at a change in each of the 6 possible states, starting from our workspace root where everything is clean.

>     $ qbt status -v
>     ...no output

That right there is the "committed" state.  Ok, so now let's create a new file:

>     $ touch misc1/foo.txt
>     $ qbt status -v
>     [misc1] dirty
>     [misc1] [status] ?? foo.txt

Here we see that foo.txt is "untracked".  ("??" means not in index, not in working tree - this output is identical to `git status --short` with ```[repo] [status] ``` prepended.  If we had also modified an already-tracked file, it would look like this:

>     [misc1] dirty
>     [misc1] [status]  M commons.core/main/qbt-make
>     [misc1] [status] ?? foo.txt

Both of these states are what we would call "unstaged".  Next, we can add these unstaged changes to the index - staging it for commit in the satelite.

>     $ git -C misc1 add foo.txt
>     $ git -C misc1 add commons.core/main/qbt-make
>     $ qbt status -v
>     [misc1] dirty
>     [misc1] [status] A  foo.txt
>     [misc1] [status] M  commons.core/main/qbt-make

Now we are in the "staged" step, because we see that foo.txt is "staged for commit".  `"A "` means added in index (not present in last commit), and not modified in working directory (index == working tree).  Similarly, `"M "` means modified in index (index != last commit) and unmodified in working tree (index == working tree).  These status are all exactly the same as normal git.  Finally, we can make a sattelite commit.

>     $ git -C misc1 commit -m"test commit, forget me"
>     [detached HEAD 6c7705f] test commit, forget me
>      2 files changed, 1 insertion(+)
>      create mode 100644 foo.txt
>     $ qbt status -v
>     [misc1] 1 commit(s) ahead

If this was plain old git, we'd be "committed", but in QBT, we call this "sattelites ahead".  `qbt status` shows us this interesting state.  The misc1 sattelite is "1 commit ahead" of where the manifest says it should be.  This 4th state is sort of like the "manifest equivalent" of having unstaged changes.  Your sattelite repositories are like the "extended working tree" of your meta repository.  How would we stage these changes?  Using `qbt updateManifest`.

>    $ qbt updateManifest
>    Updated repo misc1 from 4f24ec5684b0d65a9421024e9218d9b37719ed1a to 6c7705f7b867c1ba2bc2fa9c81e23bd40a83ccbb...
>    All update(s) successful, writing manifest.
>    $ qbt status -v
>    ...no output

This stage is called "manifest unstaged" because `qbt updateManifest` has made changes to the manifest in our working tree, but those changes are still 'unstaged'.  So here, `qbt status` behaves slightly differently than `git status` would have.  We have changes between the "extended working tree" and the index, but should that appear like an add?  Or like a modification to a tracked "file"?  It's all overly complex and not really a state people use anyways, so for now `qbt status` doesn't really recognize that state.  Thus far, there isn't really a reason to keep changers "in your manifest extended working tree but not staged in meta.".  You can still see this state by running git status directly in the meta repository, of course:

>     $ git -C meta status --short
>      M qbt-manifest

As expected, `git status` in meta shows us the `qbt-manifest` is modified between working tree and index, but index and last commit are the same.  We can move to the next stage by doing a git add:

>     $ git -C meta add qbt-manifest
>     $ git -C meta status --short
>     M  qbt-manifest

This is the "manifest staged" step, because we can now see the 'M' is in the left column, meaning the last commit and index differ but the working tree and index are equal.  Again, `qbt status` would print no output here.  `qbt status` is mostly a tool for examining the first 3 stages in your various sattelites, not the states of the manifest repository, since you can use git to directly examine those.  Finally, we are ready to make our commit.

>     $ git -C meta commit -m"test commit, forget me"
>     [master 2d98e4f] test commit, forget me
>      1 file changed, 1 insertion(+), 1 deletion(-)

And that puts us back to where we started, in the "committed" state.  There are no uncommitted changes in meta or any sattelites.  This is also right where `qbt commit` would have dropped us off if we had used it instead.

### Exotic Commit States

Having 6 states in the mix is not without side effects.  Let's say you are in a clean (committed) state, and you check out a different branch in meta.  Or, even just reset meta to point to a commit or two earlier.  What do you see?

>     $ qbt status
>     [qbt] 1 commit(s) ahead
>     [misc1] 1 commit(s) ahead

This is sort of like doing a `git reset --soft`.  We just changed meta without changing our "extended working tree".  Our sattelite repos are now actually <b>ahead</b> of the manifest.  We could recreate those commits by running `qbt updateManifest`, `git add qbt-manifest`, and `git commit`.  But what we really want to do is finish checking out that change.  To reset the working tree to match with the manifest, we run `qbt updateOverrides`.

>     $ qbt updateOverrides
>     Updated qbt from e44dd25b5fd27384a92cf6c399a8b6a8a837240f to b2a86ab0597caf407350495794f71f903d3ee78c
>     Updated misc1 from 6c7705f7b867c1ba2bc2fa9c81e23bd40a83ccbb to 4f24ec5684b0d65a9421024e9218d9b37719ed1a
>     All update(s) successful

Check out the extended options for updateOverrides - just like `git checkout -- PATH` you can specify certain repositories to reset, while ignoring others.  You can use this to "split up" meta commits that change more than one repo.  If a sattelite repo is "dirty" (has uncommitted changes) updateOverrides will refuse to destroy your changes unless you pass it some forcing options like `--allow-dirty`.  As you might expect, this is for your safety.

The upshot here is, <b>you must always run `qbt updateOverrides` after moving the HEAD of the meta repository</b>.  If you do not, you might try to build and get surprising results.  If you want to build what is in the manifest, and ignore what you have in your working tree, but don't want to actually go through the trouble of moving all the HEADs in all your sattelites, this is why the NO_OVERRIDES trick was born.  Smart, huh?

    vi: ft=markdown
