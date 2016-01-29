# Install QBT

This is a quick guide to getting QBT installed.  In order to run any builds, you will also need to follow the directions in the [Quick Start Guide](quick-start.html).

## Getting Metatools Release

QBT core is the tool that performs builds and reads a manifest file.  Metatools is the set of tools built on top of QBT which further assume that the manifest is a file checked into a git repository.  Most people will use metatools, instead of running QBT core directly.  Metatools is a superset of QBT core.

To obtain meta_tools.release:

>     $ mkdir ~/qbt && cd ~/qbt && wget http://qbtbuildtool.com/meta_tools.release.tar.gz && tar -xzf meta_tools.release.tar.gz && export PATH=$PATH:~/qbt/bin
>     $ export JAVA_HOME=/path/to/jdk1.8
>     $ qbt help

You should see output like this:
>     Available common commands:
>        build - build packages
>        commit - commit status of all satellites and all changes in meta
>        fetchPins - Fetch pins from remote qbt repositories
>        getOverride - check out a repository, setting it up as an override
>        help - print this message
>        pushPins - Push pins to remote qbt repositories
>        sdiff - compare qbt-manifests in a friendly manner
>     ...etc

If that doesn't work, ensure you have a python interpreter on your path.  The wrapper scripts that invoke qbt are written in python.

That's it!  QBT is installed successfully.  There is still a lot of configuring to do, however, so you should follow the [Quick Start Guide](qhick-start.html) to set up the configuration and start building things.

    vi: ft=markdown
