# QBT - Concepts

QBT was heavily influenced by Git, and like Git, successfully using QBT is best accomplished with a deep understanding of its data model key concepts.

## Manifest - `qbt-manifest`

The Manifest is the distributed, atomic, version-controlled metadata store at the heart of everything QBT does.  This invention lets the user manage a consistent set of versions of software which is known to be consistent using QBT.  QBT is divided into two parts, QBT core, and metatools.  QBT core assumes that a manifest file exists and a config file exists, but nothing else.  Metatools, on the other hand, further assumes that the `qbt-manifest` file lives in a git repository.  Therefore, key commands like `qbt build` and `qbt resolveManifestCumulativeVersions` are all part of QBT core.  Other commands that require the assumption that the manifest is under source control, such as `qbt commit`, `qbt resolveManifestConflicts`, QBT's merge driver, etc are all part of metatools.

## Configuration - `qbt-config`

The QBT Config file contains local configuration and frequently includes credentials, local file paths, remote server paths, remote repository locations, etc.  The `qbt-config` file is typically not checked in, but large enterprises may decide to vend a default config file which works on their network to assist developers in using QBT effectively.

## Workspace

A `qbt-manifest` file next to a `qbt-config` file is all that is needed to have a QBT workspace.  The recommended workspace layout (needed for metatools) is to have the `qbt-manifest` file in a git repo which is usually called "meta" and located in the "meta" directory in the root of the workspace.  The `qbt-config` file is then usually git-ignored and put next to it.  By placing a `.qbt-meta-location` file in the workspace root, you can put the manifest and config file wherever you want and qbt will still automatically find it.  The "workspace" is a rough concept, and very flexible, but developers will probably prefer to think of their repositories as a "workspace" because any code they have "overridden in their workspace" will build based upon local revisions, instead of the revisions in their repository.  This is very similar to how other proprietary enterprise-grade buildsystems operate.

In this manual, whenever we say "the QBT workspace" we simply mean any directory which has a `qbt-manifest` and a `qbt-config` file in it's find-up path, *or* a `.qbt-meta-location` file in the find-up path which points to those files.

## Repository

A repository is a DVCS repository (currently only Git is supported) which contains packages, all of which are at a particular revision.

## Packages

In QBT, a package is the basic "buildable unit".  A package exists in a given repository (usually at some prefix relative to the repository root).  A package has dependencies (see dependencies below for more details), qbtEnv environment variables, and some other associated metadata.

The actual structure of a package is very flexible, but some common files include:

* `qbt-make` - this shell script is the main entry-point for building the package.  QBT sets `$INPUT_ARTIFACTS_DIR`, `$OUTPUT_ARTIFACTS_DIR`, a few other variables, and then runs this script to perform the package's build.
* `.qbt-dev-proto` - this directory contains `.inputs` and `.exec` files that let a package "opt in" to development protocols.  This is how Eclipse IDE Integration works, among other things.  See "Dev Proto" below for details.
* .gitignore - it is strongly recommended that each package include a .gitignore file and ignore build artifacts and anything else that might be littered around (i.e. by dev protos, like eclipse `.classpath` and `.project` files).  By including the file in the package itself instead of the repo root, it ensures it still has effect if the package gets moved or copied around.

Packages should read dependencies from the directory pointed to by `$INPUT_ARTIFACTS_DIR` and write their output to `$OUTPUT_ARTIFACTS_DIR`, anything they do to modify their own working tree will be disgarded after the build when they are not overridden, but if they are overrides their changes will be left lying around, so it is best practice never to modify the working directory.  In the future, QBT might check for this.

## Dependencies

Packages can depend upon zero or more other packages, via "normal dependencies" and "verify dependencies".  Normal dependencies can be "Strong" or "Weak".

"Strong Dependencies" are what people normally think of as typical compile time dependencies.  A package's strong dependencies will be assembled during the build and transitive Strong dependencies will also be included.  For example, if A depends upon B and C, and B and C both depend upon D, all using strong links, then A's `$INPUT_ARTIFACTS_DIR` will include `strong/A`, `strong/B`, `strong/C`, and any other strong dependencies of those packages.  This makes it easy to construct Java classpaths, C++ linker lines, PERL5_LIB paths, etc.

"Weak Dependencies" are build tool dependencies, or "scripts" dependencies in Gradle.  These dependencies are present at the time of the build, but are not generally transitive.  Furthermore, multiple packages in your graph might depend upon different "Tips" (see Tips below) of build tools without conflict, since they are not transitive.  The classic example here is, you don't care what version of Gradle, or a linter or static code analysis tool, your dependencies used.  That doesn't impact your build.  These are the usecase for weak dependencies.  Weak dependencies show up similarly to strong dependencies, in `$INPUT_ARTIFACTS_DIR/weak/NAME`.

TODO: better document weak versus strong differences

Verify dependencies are used to say "this package tests me" so that QBT knows when verification is requested, that package should be built also.  Normally you might think of a test package as any other "client" package that depends upon the thing it tests, but in QBT you might often want to build a certain part of the graph, and thus, request a particular package be built.  Adding `--verify` as an argument tells QBT to follow verify dependency links and build those packages even if they are not requested.  It also helps to ensure tests run on every version of a package when Tips are in use (more on that below).

## Repository Set

A "repository set" is a repository that contains the manifest file, plus a repository for each sattelite in that manifest.  A repository set is all you need to build every package in a particular manifest.

## Realm

A "realm" is a collection of repository sets with comparable repositories.  For example, if I worked a repository set and made my own changes, my fork is part of the realm that describes all forks that contain those comparable repositories.  I might add or remove repositories, but as long as most of the manifest is comparable, we would still call that part of the same realm.

## QBT Software Lifestream

The "QBT Software Lifestream", or just "lifestream", is our name for the collection of all realms that exist.  Theoretically, a user could take multiple realms and compose them together in order to use components from all of them (possibly by merging their manifests, possibly be prefixing repo and package names first to avoid naming conflicts).  The resultant manifest could then have packages added to it which depend upon artifacts from both previous realms.

Presently there is not a good way to accomplish this, especially long-term when a user is likely to wish to maintain changes, and submit them back to the "source realms".  We call this "the realm problem", and it is a problem under active development.

## Dev Proto

"Dev Proto", or development protocols, are how QBT implements things like Eclipse IDE Integration.  Packages "opt in" to eclipse generation by subscribing to the `eclipse-gen` protocol.  Other protocols could implement support for other IDEs, or for generating other helpful package metadata (such as documentation, identifier indexes, etc).

Protocols are useful because they are external to the build, so they can work even when the normal package build would fail, or if other packages this package depends upon might fail.  Because protocols replace artifacts with a placeholder for overridden packages, any overridden package might fail to build but still allow devProto to run successfully.  It is important to be able to do things like generate eclipse project files while the code is in a broken state.  For more details, see [Dev Proto](dev-proto.html).

## Package/Repo Tips

Under classic use, QBT Manifests contain only a single revision for each repository (and thus, a single revision for each package).  This is by design.  However, sometimes a single manifest needs to model multiple revisions of a library at the same time.  The most common situation is where one team vends an API to many other teams, and needs to make a non-backwards-compatible change.  If they cannot get every customer of their library to upgrade all at once, they instead need to "fork" their package.  However, creating a new package with a new name has many drawbacks - the information that they are *really* the same package, just different versions, is lost.  Migrating to the new version requires changing dependencies, which might require also changing many transitive dependencies and introducing difficult-to-anticipate version conflicts elsewhere in the dependency tree.  Tips were invented for this exact case.

Let's make our example more concrete.  Let's say the library is `org.cmyers.nappysak.main`.  The default tip is `HEAD`.  That is why dependencies on this library in the `qbt-manifest` would currently say `org.cmyers.nappysak.main` (the tip `HEAD` is implicit).  We could add a second Tip to the repository that contains this package, calling it `FROZEN`, which would look like `org.cmyers.nappysak.main^{FROZEN}`.  Then, in a single change, we can merge in our new changes to HEAD and update whatever clients we can to support it.  Clients which require the old version can be adjusted to depend upon `org.cmyers.nappysak.main^{FROZEN}` instead.  If any package depended upon both HEAD and FROZEN versions of a given package through "Strong" links, that would be a strict dependency conflict and the build would fail.  The package could override the version it gets using replacedeps, and some other advanced dependency mechanisms.  For a more comprehensive treatment, see [Advanced Dependency Management](advanced-dependency-management.html).
## Cumulative Version (CV)

A package's Cumulative Version (CV) is a unique identifier generated deterministically from a package's tree contents, dependencies, and qbtEnvironment settings.  All inputs that could change the output of a build should also be part of a package's CV, it is the cache key used to cache the built artifacts.  CVs do not, however, contain platform/OS/hostname information, so users who wish to use remote artifact caches will have to be very careful about that.  There are ways to deal with that using QBT environment variables, see the next section for that.

## QBT Environment (qbtEnv)

Dependencies are supposed to always be specified via the `qbt-manifest` file, but sometimes certain large, highly-platform dependent things (like the JDK themselves) are best kept as an external dependency (setting JAVA_HOME).  Another case for QBT environment variables might be enabling debugging or profiling information, or other compile flags.  Because in either case these change the built artifacts, these environment variables are calculated as part of the package artifact's Cumulative Version (CV).

For example, in the `qbt-manifest`, Java packages list `JDK: "1_8"` under the `qbtEnv` hash.  This means that the value of QBT_ENV_JDK is a dependency of the package, and defaults to the value `1_8`.  You can override this value by manually passing `--qbtEnv JDK="1_9"` to any command that might trigger a build or a cache lookup.  If you examine `qbt-make` scripts for a Java package, you will see that they defer to that variable to determine which JDK to use, reading the value stored in `$JAVA_1_8_HOME` for the value `1_8`, `$JAVA_1_9_HOME` for the value `1_9`, etc.  Therefore, if you wanted to build a package using a JDK9, you would set `$JAVA_1_9_HOME` to point to it and set `$QBT_ENV_JDK` to `1_9`.

The reason the JDK environment variable works like this is, two different people might keep their JDKs in two different locations, so if we used the JAVA_HOME variable directly, they would calculate two different CVs for the same package (because they are both using a JDK8, but the value of the variable is different).  This extra layer of indirection lets us say they are both using a `1_8` JDK, so their CVs should be the same, but the path where the JDK is actually stored is independent of CV.

>     # from qbt-make
>     eval export JAVA_HOME=\$JAVA_${QBT_ENV_JDK}_HOME

You could do the same thing with python, ruby, etc. if you wanted to version the interpreter outside of QBT.  You could also use `qbtEnv` for other things besides external dependencies, like compiler flags, but your packages MUST specify the variable in the `qbt-manifest` file, otherwise it cannot be included in the CV calculation which is important for reproducibility.

>     $ qbt build --package some.package --qbtEnv 'CCFLAGS=-O3'

`qbt build`, `qbt runPackage`, `qbt runArtifact`, and all other commands that can trigger a build take this option.

    vi: ft=markdown
