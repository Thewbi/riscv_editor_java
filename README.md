# riscv_editor_java

A emulator for riscv with frontend

# git submodules

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
git submodule update --recursive --remote --merge
```

will fetch the latest changes from upstream in each submodule, merge them in, and check out the latest revision of the submodule.

# Error: java.lang.module.FindException: Module javafx.controls not found

```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module javafx.controls not found
```

Tried: Reload Project from context menu on pom.xml

Tried: Maven clean followed by maven install

Solution:

Go to https://jdk.java.net/javafx20/ and download JavaFX SDK 20
Install it on your machine and remember the path (you need to insert that path into launch.json later)
The path might be C:\Users\wolfg\Downloads\javafx-sdk-20.0.2 on windows.

Create a launch.json that has the correct paths in it to load the javafx modules:

```
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "App_On_Win",
            "request": "launch",
            "mainClass": "de.template.App",
            "projectName": "template",
            "vmArgs": "--module-path \"C:/Users/wolfg/Downloads/javafx-sdk-20.0.2/lib\" --add-modules javafx.controls,javafx.fxml"
        },
        {
            "type": "java",
            "name": "App",
            "request": "launch",
            "mainClass": "de.template.App",
            "projectName": "template",
            "vmArgs": "--module-path \"/opt/javafx-sdk-20/lib\" --add-modules javafx.controls,javafx.fxml"
        },
        {
            "type": "java",
            "name": "Current File",
            "request": "launch",
            "mainClass": "${file}"
        }
    ]
}
```

# Highlighting Lines

In order to highlight lines, a container format is required.

It is not possible to highlight lines given raw machine code since the raw
machine code for a instruction has no relation to the source line that it has
been generated from.

In order to match a machine instruction back to the source line, the assembler
has to output a container format that stores the machine code along with
the line in the source file that it has been generated from. The editor can
then debug the machine code and for the instruction that is executed next,
it can look up the source line and hightlight that line in the editor.

It is not possible to use the PC of the execution stage since that PC cannot
be transformed into a source line number. The reason is that the user can
insert labels, assembler instructions or comments into the source file which
are all resolved and will not make it into the machine code. Thefore the PC
will not match up with the source code line.

It is also not possible to decode machine code back to ASMLines and then
try to renumber the ASMLines to add back source code lines. The reason is
the exact same as outlined above. The machine code does not match 1:1 with
the source line.

The container has to store: The entire machine code. For each address
inside the machine code it has to contain a mapping from address to the source
line and also the source file that contains that source line. If some part
of the machine code does not express an instruction but a constant for example,
the container stores the source file and line where that constant is defined.

The source file has to be supplied externally. The container format together
with the original source file is used to show which source code line is
currently executed by highlighting that line.

The editor can parse the source file again and index it for variable names
and constants. It can then support the user reading the source code.


# git - Changing the remote URL of a subproject

https://stackoverflow.com/questions/913701/how-to-change-the-remote-repository-for-a-git-submodule

Change the URL in .gitmodules, then

```
git submodule sync --recursive
```

Check the remote URLs:

```
git submodule foreach -q git config remote.origin.url
```

then

```
git submodule update --init --recursive --remote
```