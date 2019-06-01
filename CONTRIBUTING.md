# Contributing to Selenium Shutterbug

The Selenium Shutterbug project welcomes contributions from everyone. 

### Step 1: Fork

Fork the project [on Github](https://github.com/assertthat/selenium-shutterbug)
and check out your copy locally.

```text
% git clone https://github.com/username/selenium-shutterbug.git
% cd selenium-shutterbug
% git remote add upstream https://github.com/assertthat/selenium-shutterbug.git
```
### Step 2: Branch

Create a feature branch and start improving:

```text
% git checkout -b my-feature-branch
```

HEAD-based development is preferred, which means all changes are applied
directly on top of master.

### Step 3: Commit

If you hve not configured Git already, its right time to do it, by specifying your name and email:

```text
% git config --global user.name 'Your Name'
% git config --global user.email 'name@example.com'
```

Please read this before writing commit message:

http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html

### Step 4: Rebase

Use `git rebase` (not `git merge`) to sync your work from time to time.

```text
% git fetch upstream
% git rebase upstream/master
```

### Step 7: Push

```text
% git push origin my-feature-branch
```

Go to https://github.com/yourusername/selenium-shutterbug and press the _Pull
Request_ and fill out the form. 

You PR will be reviewed ASAP and integrated to master.
