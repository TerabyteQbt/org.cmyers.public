# QBT - Command `submanifest`

## Usage

Create a private manifest which will be a superset of the current manifest at HEAD

>     $ qbt submanifest --importedFile public-repos --base HEAD --lift HEAD
>     lift HEAD (3072eb492d4322451608aaa5fe5f3e03ffa1fa23) -> 769471b3002758aef0a427082e7afdf941abe304

The BASE, henceforth for this submanifest, will be 3072eb492d4322451608aaa5fe5f3e03ffa1fa23.  You may want to note that.
The new manifest branch should start out pointing to 769471b3002758aef0a427082e7afdf941abe304.  If you examine the history, you see:

>     $ git log -2
>     commit 769471b3002758aef0a427082e7afdf941abe304
>     Author: Carl Myers <cmyers@cmyers.org>
>     Date:   Fri Jan 29 11:21:55 2016 -0800
>     
>         (submanifest import)
>     
>     commit 3072eb492d4322451608aaa5fe5f3e03ffa1fa23
>     Author: Carl Myers <cmyers@cmyers.org>
>     Date:   Fri Jan 29 11:21:55 2016 -0800
>     
>         Major QBT documentation rewrite

What submanifest did was copy the commit 3072eb, and create a new commit with identical metadata and a tree which only differs by the "imported file".

If you run a `git diff` between these two commits, you will see the only difference in the contents is the imported file:

>     $ git diff 3072eb492d4322451608aaa5fe5f3e03ffa1fa23 769471b3002758aef0a427082e7afdf941abe304
>     diff --git a/public-repos b/public-repos
>     new file mode 100644
>     index 0000000..ee7bceb
>     --- /dev/null
>     +++ b/public-repos
>     @@ -0,0 +1,11 @@
>     +3p
>     +eclipse_gen
>     +link_checker
>     +linter
>     +meta_tools
>     +misc1
>     +org.cmyers.public
>     +pbt
>     +qbt
>     +wrapper_generator

Repositories which are "public" go in this list.  As you add "private only" repositories, they will only be present in the "lifeted" commits.  You can make changes in the "private" or "lifeted" history which changes both public and private repositories.  In fact, all development, public or private, which is done "within the private organization", should be based upon that commit with the message "(submanifest import)".  When it is time to contribute those changes back to the "public" or "split" history, you must "split" the changes.  If you split a change that has nothing private in it (like the base commit) you get the original commit back

>     $ qbt submanifest --importedFile public-repos --base 3072eb492d4322451608aaa5fe5f3e03ffa1fa23 --split 769471b3002758aef0a427082e7afdf941abe304
>     split 769471b3002758aef0a427082e7afdf941abe304 (769471b3002758aef0a427082e7afdf941abe304) -> 3072eb492d4322451608aaa5fe5f3e03ffa1fa23

If, however, you have made changes, when you do a split, you will get a new history generated that is "as if the private stuff never happened".  This history can then be safely be proposed to the "public" history.

Any time you want to "lift" public changes into your private manifest, you would run (where public/master is the public manifest branch):

>     $ git fetch public
>     $ qbt fetchPins public --all --manifest <(public/master:qbt-manifest)
>     $ git checkout public/master
>     ...inspect the contents, make sure it builds, etc.
>     $ qbt submanifest --importedFile public-repos --base 3072eb492d4322451608aaa5fe5f3e03ffa1fa23 --lift public/master

The resultant commit should then be merged with your private branch (private/master)

>     $ git checkout private/master && git merge <SHA1 from previous command>

Any time you want to "split" out the public parts of your changes to propose them back to the world, you would run:

>     $ qbt submanifest --importedFile public-repos --base 3072eb492d4322451608aaa5fe5f3e03ffa1fa23 --split private/master

Then you can push those changes to a public repo and ask for a code review

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

`qbt submanifest` is an advanced solution for mantaining two (or more) manifests, where one is a superset of the other.  It is designed for an organization that wants to have private repositories they do not share built on top of open-source repositories they do share.  QBT's submanifest command is influenced, but not an exact analog of, git's subtree command.

Whether submanifest is the recommended long-term solution for collaboration between organizations, or some other "realm plan" is devised, remains an open question.

Also, note that if your proposed public changes are not accepted, or even worse are accepted only with some rewrites, it is going to make your private history very sad and confused.  This case is not well-handled by this workflow.

    vi: ft=markdown
