# riscv_editor_java
A emulator for riscv with frontend

# git submodiles

See https://stackoverflow.com/questions/36554810/how-to-link-folder-from-a-git-repo-to-another-repo

Break your big project to sub projects as you did so far.
Now add each sub project to you main project using :

```
git submodule add <url>
```

Once the project is added to your repo, you have to init and update it.

```
git submodule init
git submodule update
```

As of Git 1.8.2 new option --remote was added

```
git submodule update --remote --merge
```

will fetch the latest changes from upstream in each submodule, merge them in, and check out the latest revision of the submodule.
