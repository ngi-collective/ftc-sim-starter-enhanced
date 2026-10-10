# Visual Studio Code

VS Code edits, navigates and highlights errors. Building, testing and installing go through the
`mise` tasks in a terminal (`mise tasks` lists them). Two files in `.vscode/` set VS Code up.

## What is committed

### `.vscode/extensions.json`

```json
{ "recommendations": ["vscjava.vscode-java-pack"] }
```

Recommends Microsoft's **Extension Pack for Java**: Red Hat's Java language support, which also
imports the Gradle project, plus the Java debugger and test runner. VS Code offers to install it
the first time you open the folder.

### `.vscode/settings.json`

```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.jdt.ls.androidSupport.enabled": "on"
}
```

- **`java.configuration.updateBuildConfiguration: automatic`.** VS Code re-imports the Gradle
  project whenever a build file changes, without asking. The other values are `interactive`, which
  asks on every change, and `disabled`. The ftc-sim plugin adds the `robot`/`simulated` flavours
  and the simulator's libraries to TeamCode, and VS Code does not see them until it re-imports.
- **`java.jdt.ls.androidSupport.enabled: on`.** Turns on the Java extension's Android project
  import. That is how VS Code learns that `TeamCode` and `FtcRobotController` are Android modules
  and resolves `android.*` and the FTC SDK's libraries. The extension marks it experimental. Its
  default, `auto`, enables it only in VS Code Insiders; `on` enables it in regular VS Code. It
  needs Android Gradle Plugin 3.2.0 or later, and this project uses 8.13.2.

## What VS Code needs from your machine

The Gradle import runs the project's own Gradle (9.1) and Android Gradle Plugin (8.13.2). They need
a JDK and the Android SDK, which `mise` supplies as `JAVA_HOME` (Temurin 21) and `ANDROID_HOME`.

- **Run `mise install` and `mise run setup-sdk` once**, so the JDK, the SDK platforms and the
  build-tools exist.
- **Start VS Code from a terminal in this folder** with `code .`, so it inherits those variables.
  If VS Code is already running, `code .` can open the folder in the running process, which keeps
  the environment it started with. Quit VS Code first. On macOS, run **Shell Command: Install
  'code' command in PATH** from the command palette once to get `code`.
- **`local.properties` overrides `ANDROID_HOME`.** If it has an `sdk.dir` line, Gradle uses that
  SDK, in VS Code and on the command line. The file is gitignored and specific to one machine.
  `mise run verify-toolchain` reports which SDK Gradle will use and whether it has what the build
  needs.

The Java language server runs on the JRE bundled with the extension, so it does not need
`JAVA_HOME`. Only the Gradle import does.

## What it leaves behind

The Java extension compiles into a `bin/` folder in each module, such as `TeamCode/bin/`.
`.gitignore` already covers `bin/`, and Gradle ignores those folders. If you delete a module folder
while VS Code is open, the extension can recreate its empty `bin/` folders, so close VS Code first.
