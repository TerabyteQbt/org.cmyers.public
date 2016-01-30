# QBT - Why QBT?

### {Fast, Correct, Scalable, Powerful}.keepall()

QBT is an exciting new build tool.  It is easy to be skeptical in a world where everyone thinks the solution to "there are 15 choices for X, and they all suck" is to make a 16th one which "also sucks".  QBT is far from another "me too" build tool, it distinguishes itself from every other freely available offering in at least one way, and often many highly valuable ways.  The landing page listed some of QBT's standout features, but here we will try to stand QBT up against the build tools currently in common use, then explain why QBT offers such a huge benefit over them.  We will also discuss some of QBT's drawbacks.

Many of these questions have an answer that depends upon how your build is structed.  It may be slightly unfair to these very established tools, but we will generally answer by the "average" project using the tool, as that is most indicative of how the community uses the tool and what the tool encourages people to do.  If you think any of our evaluations are inaccurate, please do not hesitate to reach out.  We do not wish to misrepresent any other tools.

LEGEND:

* YES - in our experience, this is almost always true.  1.0 pts
* MOSTLY - in our experience, this is usually true.  1.0 pts
* BARELY - in our experience, this is almost never true.  0.5 pts
* NO - in our experience, this is never true.  0.0 pts

## QBT Feature Matrix

Here are the features we will discuss in this document.  We consider each feature either important, or critical, in an enterprise software development environment, a quality open source project, or both.

* Free and Open Source (is the tool freely available for use and improvement including source, and unencumbered by any license restrictions that would prevent commercial use?)
* Tool Bootstrapping (is the version of build tool used versioned for reproducibility?)
* Repeatable/Reproducible Builds (can a well-constructed build with this tool reasonably be thought to be repeatable and reproducible?) 
* Technology Agnosticism (can you easily use the tool for any and all technology stacks?)
* Performance Scalability (can the tool scale to 10,000+ packages, or hundreds of millions of lines of code?)
* Parallelism (does the tool enable builds to occur in parallel easily or automatically?)
* Incremental Building (can the tool safely and correctly build incrementally?)
* Flexibility (can you decide how to distribute the tool, perform the builds, structure any infrastructure it needs, etc)
* Trust (can you easily use the tool with minimal or no trust of external third parties?)
* Network Independence (can builds be done disconnected, without network?)
* Social Scalability (can the tool be used by independent but collaborating groups, even across lines of trust?)
* Consistency (does the tool assist in building and managing a set of consistent versions of software owned by independent teams or entities that collaborate via software dependencies, and ensure they do not break each other?)
* Powerful (does the tool enable advanced features like code generation, using arbitrary build tools, etc without having to write a custom module and without breaking other functionality, like incremental building?)
* Atomic Changes Across Multiple Dependencies (If A depends upon B and C, can you make a change to both B and C, or both A and B, as a single atomic operation, which either succeeds or fails?)

### Gradle

Gradle is probably the most powerful free, open source tool, and covers the largest part of our feature matrix besides QBT.  Unfortunately, Gradle (like all of the other tools on this list) still has some serious shortcomings related to trust, incremental building correctness (see [GRADLE-2579](https://issues.gradle.org/browse/GRADLE-2579)), and inability to facilitate atomic cross-package change unless the packages are all stored in the same repository, or using git submodules which make development very painful and hurt scalability.

* Free and Open Source - YES
* Tool Bootstrapping - YES - best in class
* Repeatable/Reproducible Builds - BARELY - unless your builds are constructed *very* carefully.  Most depend upon "+" dependencies and network which are fundamentally not repeatable.
* Technology Agnosticism - YES
* Performance Scalability - MOSTLY - Gradle has some performance problems at very large scale, it can perform the builds but configuration steps are very slow
* Parallelism - YES - most built-in compile tasks are annotated as @ParallelizableTask, so we assume it can run them in parallel, but this may require additional configuration
* Incremental Building - MOSTLY - Gradle has excellent incremental build support but sometimes produces incorrect results or poisoned cache results, especially if using custom tasks or if your build is not architected carefully
* Flexibility - YES - Gradle has complete flexibility with its built-in DSL and easy writing and distribution of modules
* Trust - NO - Gradle downloads and runs plugins from a third-party website you must trust by default
* Network Independence - NO - Gradle implements the minimum here by being capable of downloading your dependencies in advance and putting them in a local cache which it can consult for future builds.  Most builds, however, are not designed to work this way and have additional network dependencies during compile or tests.  Adding dependencies or updating versions requires network connection, first build requires network connection, poisoned cache requries network connection, etc.
* Social Scalability - NO
* Consistency - NO - unless your entire software ecosystem is placed in a single git repository
* Powerful - NO - requires considerable customization to do anything fancy
* Atomic Changes Across Multiple Dependencies - NO - again, unless your entire software ecosystem is placed in a single git repository

TOTAL: 6.5 / 14

### Maven

Maven is a highly opinionated, "convention over configuration" java build tool used by the vast majority of Java libraries.  Despite its popularity, it performs very poorly in enterprise settings because of its inflexibility.

* Free and Open Source - YES
* Tool Bootstrapping - NO - maven is versioned external to the build and most projects require maven2 or maven3 and will not work with the other
* Repeatable/Reproducible Builds - NO - unless your builds are constructed *very* carefully.  Most depend upon "+" dependencies and network which are fundamentally not repeatable.
* Technology Agnosticism - NO - tool is highly specialized for Java
* Performance Scalability - NO - pom.xml files make large multi-project builds slow and a management nightmare.
* Parallelism - NO - no built-in parallelism support
* Incremental Building - NO - no built-in support for incremental building
* Flexibility - NO - favors convention over configuration, extremely inflexible.
* Trust - NO - Building the tool from source is difficult, and almost every maven project depends upon binary artifacts on a third-party website you must trust by default.
* Network Independence - NO - Maven can download artifacts to a local cache for later building offline, but most builds are not designed with this in mind, so they fail tests or have other problems when disconnected.  Adding dependencies or updating versions requires network connection, first build requires network connection, poisoned cache requires network connection, etc.
* Social Scalability - NO
* Consistency - NO - unless your entire software ecosystem is placed in a single git repository
* Powerful - NO - requires writing maven plugins to do anything fancy, and writing and distributing said plugins is much more challenging than with Gradle.
* Atomic Changes Across Multiple Dependencies - NO - again, unless your entire software ecosystem is placed in a single git repository

TOTAL: 1.0 / 14

### Ant

Ant was written as "cross-platform-compatible make".  It has only some of make's benefits and all but one of make's shortcomings (namely, that it is cross-platform-compatible).

* Free and Open Source - YES
* Tool Bootstrapping - NO
* Repeatable/Reproducible Builds - NO - unless your build is extremely carefully constructed - most make builds depend upon external tools that are not versioned like gcc, /usr/lib, etc.
* Technology Agnosticism - NO - comes with Java-specific tasks and little else
* Performance Scalability - NO - larger projects quickly become unmanageable due to thie bloat of xml code needed
* Parallelism - NO - Requires specialized code to run builds or tests in parallel.
* Incremental Building - NO - Ant has practically none of make's incremental build support because filesystem mtimes (the way make does it) are not reliable cross-platform.
* Flexibility - YES - even the dependency manager is external and you can "choose" whether or not to use it (Ivy)
* Trust - YES - If you obtain a trusted version of Ant, it is pretty easy to have a trusted build by simply not using a dependency manager and checking in all the jars you need.
* Network Independence - YES - again, refusing to use dependency management will get you this, though most ant-based projects out there do use Ivy and do require network.
* Social Scalability - NO
* Consistency - NO - unless your entire software ecosystem is placed in a single git repository
* Powerful - BARELY - if you are willing to write the code, you can do pretty much anything, but noting is "facilitated".
* Atomic Changes Across Multiple Dependencies - NO - again, unless your entire software ecosystem is placed in a single git repository, though with make it often is.

TOTAL: 4.5 / 14

### Make

Make is literally 4 decades old.  One of the most stable and common choices for a build tool outside of the Java ecosystem, make has no support for external dependencies and requires you to write basically everything it does (or use external tools like autoconf, etc).

* Free and Open Source - YES
* Tool Bootstrapping - NO
* Repeatable/Reproducible Builds - NO - unless your build is extremely carefully constructed - most make builds depend upon external tools that are not versioned like gcc, /usr/lib, etc.
* Technology Agnosticism - YES
* Performance Scalability - NO - larger projects quickly become unmanageable due to thie bloat of makefile code needed
* Parallelism - YES - `make -jN` was the standard in parallel building for 20+ years
* Incremental Building - MOSTLY - a well-written build can be incremental with make, but again it depends greatly on the quality of the makefile authors.
* Flexibility - YES
* Trust - YES - Due to make's complete lack of dependency management, most projects check in their dependencies and use them locally.  Make downloads and runs no external code.  Obtaining a "trusted" make binary is easy because every linux distro includes it.
* Network Independence - YES - again, make's lack of dependency management has really helped it win this one
* Social Scalability - NO
* Consistency - NO - unless your entire software ecosystem is placed in a single git repository
* Powerful - BARELY - if you are willing to write the code, you can do pretty much anything, but noting is "facilitated".
* Atomic Changes Across Multiple Dependencies - NO - again, unless your entire software ecosystem is placed in a single git repository, though with make it often is.

TOTAL: 7.5 / 14

### QBT

* Free and Open Source - YES - Released under the Apache 2.0 License
* Tool Bootstrapping - MOSTLY - You have to obtain a binary copy of QBT along with a JDK8 first, but after that you can build a versioned copy of QBT which is kept in your manifest along with your other software, protecting against tool breaks.
* Repeatable/Reproducible Builds - YES - every build is performed in a sandbox and passed its isolated dependencies.  Packages cannot "see eachother" outside of that, and relying upon system dependencies is discouraged.
* Technology Agnosticism - YES - After resolving your dependencies, QBT simply runs a shell script, you do the rest.  What you build and how you build it is up to you, though there are some example templates for building Java and others are in the works.
* Performance Scalability - YES - QBT can operate on dependency graphs which contain tens of thousands of packages or more.  All algorithms in QBT core were written with immense scalability in mind.
* Parallelism - YES - QBT can run jobs in parallel (and does, by default) at the package granularity, and because it has your entire dependency graph it is often bounded only by the maximum critical path in your graph.  You don't have to do anything to get this benefit.
* Incremental Building - YES - all build results are cached at the package granularity using a content-of-dependencies hash, so the same inputs are always only ever built a single time.  Shared remote caches can extend this benefit across machines too.
* Flexibility - YES - QBT is designed like git - to be a toolkit rather than a monolithic tool.  You can decide how to structure your code, how to set up continuous integration, what infrastructure is appropriate, etc.  QBT tries to stay out of the way and let you get your job done.
* Trust - MOSTLY - The only "trust hole" in QBT is obtaining a trusted binary of the tool the first time.  QBT can be built from source without QBT, but it is currently "not fun".  Once you have a trusted binary of QBT, you can trivially build QBT from source and version it along with your software.  All software versions are specified by git commit hashes which are generally cryptographially trusted, and by signing commits in your manifest repository you can have a very strong degree of trust with barely any additional work.
* Network Independence - YES - Like git, QBT has only two commands that invoke the network, `fetchPins` and `pushPins`.  All building, dependency resolution, comitting, and testing is completely network independent unless your build violates that itself.
* Social Scalability - YES - By using a distributed but atomic metadata store (the meta repo in git), QBT facilitates collaboration in a way familiar to developers, which functions across teams and even across disparate organizations which needen't trust each other, to produce guaranteed consistent and tested software.
* Consistency - YES - By using a distributed but atomic metadata store (the meta repo) changes can be tested atomically, even when they involve tens, hundreds, or thousands of repositories, and are accepted or rejected atomically, ensuring that "the current set of versions in meta" are always consistent with a very small, simple piece of infrastructure.
* Powerful - YES - QBT actually uses code generation and many other advanced dependency features (frozen versions to eliminate cyclic dependencies, build tools that build themselves, etc) with little or no custom code.
* Atomic Changes Across Multiple Dependencies - YES - QBT was designed to solve this very problem.  Again, the distributed atomic metadata store (the meta repo) makes it possible.

TOTAL: 14 / 14

### QBT Drawbacks

QBT may be an exciting new tool, but it is not without flaws, some of them serious to users with certain usecases.

* QBT is still under very active development, and configuration and manifest formats are still in flux, although almost all manifest changes are forwards compatible (newer QBT versions can read old and new formats and will seamlessly upgrade them).
* QBT is a complex tool with a steep learning curve.  Like Git itself, it is powerful and transparent to those who understand its inner workings, but learning it can be challenging for those who want to follow a script, or get fast results with little effort.
* QBT is not a miracle worker.  While the documentation has made grand, sweeping promises, QBT cannot prevent users from doing stupid things like writing flakey tests, or reading $HOME and looking for stuff that will never be present for any other user.  All claims fall under the major quid pro quo of "as long as your design your build properly and don't do anything insane, which QBT makes every effort to asist you with".
* QBT works great with a single large manifest inside an organization, and it works sufficiently well in our testing with a "public" and a "private" manifest (which is a superset of the public one), separated out using the [Submanifest](qbt-submanifest.html) command.  However, two or more organizations that wish to share manifests, or an organization that wants to combine the contents of multiple manifest and build on top of it, is an outstanding problem under current development (we call this "the realms problem").  We strongly suggest enterprise users plan to have a single company-wide manifest (because splitting manifests is not done for scaling reasons, it is done to deliniate ownership/trust, and a single enterprise should have similar levels of trust among the entire orgnaization), or at worst, two levels, high side and low side, which can be served by a submanifest split.
* QBT's value as a dependency manager is limited to some extent by what dependencies are available in a manifest, and right now, the available libraries are limited.  A somewhat janky, but mostly reliable tool called the [Third Party Importer](qbt-thiird-party-importer.html) exists and can import almost any jar in maven central into QBT for use by other qbt java packages (this is how QBT's third-party dependencies happened).  These jars are checked-in binaries and their dependencies are modeled as closely as we can based upon the (highly flawed and often incorrect) information in their maven pom descriptor.  Manual mucking is often required after import.  The jars consistency is maintained by a tool called the [Link Checker](link-checker.html), which uses a bytecode analyzer to ensure that all classes load and have their dependencies satisfied by the compile time classpath of the module.  Non-java dependencies generally have to be brought into QBT, and their terrible non-reproducible builds rewritten.  If a package were to depend upon something outside of QBT, it would invalidate QBT's CumulativeVersions and cache of that package, and basically break everything QBT is trying to do.

## Summary

QBT is a framework you can use to manage a set of consistent versions of many interconnected software projects, and make changes to those projects while ensuring none of them break.  QBT is just a tool which can manage dependencies and stitch together many repositories.  When used correctly, however, it enables reproducibility, consistency, change management, and trust/auditing.

QBT is the first open-source build tool targeted at usecases the likes of which have previously only been served in large enterprises like Amazon.com.

    vi: ft=markdown
