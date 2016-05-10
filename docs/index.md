---
layout:            default
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

The 2git project is a conversion engine that will enable you to convert _anything_ 2git using the special designed DSL language and the generic migration engine
{: .cuff}

If you start using 2git you will eventually fall in love with Git - and in the end leave your current Version Control System. We do not want to encourage adultery, so make sure that you're ready for the journey. Be a gentleman about it!
{: .warning }

_"Why another conversion kit for Git?"_" you might ask?

_"Why use different conversion kits for the same thing - you all wanna go 2git right?"_" ...we might ask back!

The thing is that we have migrated teams from Subversion, ClearCase, ClearCase UCM, CVS, Perforce, Mercurial, SurroundCM to Git. And always using the same approach.

## This is the conversion challenge
Git is doing clones, and a clone is clone - basta! It has some impact on how you (should) organize your Git repositories, It' very likely that they will be organized differently than the repository you're moving away from. Both performance and security issues needs to be considered.

### Security
Git does not support that different areas of your clone has different security levels, that kinda comes with the _clone_ concept. Once you have the clone on your local file system, you have access to the files - _all_ the files.

So you might wanna think about how you organize stuff.

### Performance
Many of the older client/server oriented VCS supports that your can do partial checkouts, views, work-trees or what ever you call'em. So you might have one big, fat, monolithic repo, where users do partial workspaces.

But Git does clones, and it kinda goes with the clone concept, that it's all or nothing.

So you might wanna think about how you organize stuff.

### The things we left behind
If you have been in an _old_ VCS, you have probably been doing old-style branching: _early branching_, _cascading branches_, _feature branches_ or any other old-style branching pattern which essentially had the purpose, of compensating for either poor merging abilities, poor performance, expensive license, pessimistic file locking - or some other not-very-desirable ability.

Git doesn't do poor X or expensive Y. So you probably wanna change your branching strategy in Git. (Make is _simple_ for gods sake!!)

## What it is

2git is a DSL extension to groovy, so you can say that it's a small language that is designed to describe what you want to migrate - from source to target.

You feed the recipe to a engine that knows how to deal with all kinds of version control systems and it will then take what you've filtered out and commit it into Git.

Then you get a new repository containing what you specified, and you can try to use your new repo in the context your normally use your VCS, for instance try feed it to your continuous delivery pipeline and see what happens.

If you don't like it, go back, change the recipe and run it again. Keep doing it till you get it right!
