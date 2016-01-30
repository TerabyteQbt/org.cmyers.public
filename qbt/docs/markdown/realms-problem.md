# QBT - The Realms Problem

In an idea world, everyone would use QBT.  The Apache project would have a massive manifest with all of their software building together, in peaceful consistent link-checked harmony.  The same would be true for google's various libraries (Guava, Gson, Guice, etc).  But now my organization wants to consume Apache's manifest for commons-lang3, and google's manifest for guava and Guice, and suddenly I have a big problem.  I could just merge these manifests together, and it'd work so long as there are no repository naming or package naming conflicts - but how do I contribute changes back to them?

Here's a similar scenario - I personally work on a lot of software.  Some of it is QBT related, some are random git scripts, others are related to my other interests.  It seems silly to shove all of my packages together into one manifest when 99% of the open source world is going to want zero or one of those packages.  In essence, I'd be forcing them to build (and read, and decide to trust) my entire manifest.  If only I could split my manifest up into smaller chunks, but pull it all together into a "working set" manifest while I am doing development...then split those changes back out into their appropriate manifests afterwards.  I think you see where this is going...

The "realms problem" is figuring out how to solve this natural desire to have software split into "realms" for distribution so people can consume just the parts they want, but still allow the software to be composed into larger manifests for working or depending upon.  I want to "compose, share, depend upon, and ontribute back to hierarchical realms".

We still don't have a good plan for this, unfortunately, though we are working on it.

    vi: ft=markdown
