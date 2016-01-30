# QBT - Enterprise Deployment

## Distributing QBT

TODO: recommend distributing QBT binaries checked into a repo?

## Writing your company's `qbt-config` file

TODO

## Setting up an "unbreakable" continuous build

TODO

## Migrating builds into QBT

TODO

## Training users to use QBT

TODO

## Ideas for code review

TODO

## Ideas for push hooks

* Enforce qbt binary distro == manifest CV, if applicable
* Enforce all commits contain only fast-forwards in sattelites
* Enforce pins present in a particular pin remote so people don't forget to pushPins
* Enforce that people didn't merge two unrelated histories (i.e. between private and public if using submanifest)

    vi: ft=markdown
