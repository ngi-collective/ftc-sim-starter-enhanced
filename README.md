# ftc-sim starter for VS Code

The FTC SDK (FtcRobotController v12.0, BIOBUZZ 2026-2027), set up to test your OpModes against
[ftc-sim](https://github.com/ngi-collective/ftc-sim), a simulated robot on a simulated field, and
to work in VS Code with [mise](https://mise.jdx.dev) supplying the JDK and the Android SDK. No
Android Studio needed, though it still works: see [Android Studio](#android-studio).

- **Tests** drive your OpModes with no robot attached and check where the robot ends up.
- **A Driver Hub in your browser**: a 3D field, gamepad input, the robot's camera view and the
  live score.
- **The competition APK is unchanged.** The simulator lives in a separate `simulated` build, and
  the `robot` build that goes to a Control Hub contains none of it.

Not affiliated with or endorsed by FIRST. FIRST Tech Challenge and FTC are trademarks of FIRST.

## Start a new team repository

1. Click **Use this template** → **Create a new repository**.
2. Clone it, and **keep SDK updates mergeable** (once, right after creating it):

   ```bash
   git remote add upstream https://github.com/FIRST-Tech-Challenge/FtcRobotController.git
   git fetch upstream --tags
   git merge -s ours --allow-unrelated-histories v12.0 -m "Record FTC SDK v12.0 as merged"
   git push
   ```

   A repository made from a template starts with a single commit and no link to FIRST's history.
   This merge changes no files. It records that your code already contains SDK v12.0, so when
   FIRST releases the next SDK, `git merge upstream/master` brings in only what changed. Skip it
   and that merge conflicts on every file the SDK touches.

3. Install [VS Code](https://code.visualstudio.com) and [mise](https://mise.jdx.dev/installing-mise.html),
   with mise [activated in your shell](https://mise.jdx.dev/getting-started.html). Then, in the
   repository:

   ```bash
   mise trust            # allow this repository's mise.toml to run
   mise install          # Temurin JDK 21 and the Android command-line tools
   mise run setup-sdk    # the Android SDK platforms and build-tools, about 500 MB
   code .
   ```

   Start VS Code from that terminal with `code .`, so it gets mise's `JAVA_HOME` and
   `ANDROID_HOME`, and accept its offer to install the recommended Java extensions.
   [`VSCODE.md`](VSCODE.md) explains the settings and what to do if the import fails.

Already have a team fork of FtcRobotController? You don't need this repository. Add the plugin to
your own `TeamCode/build.gradle` as described in the
[ftc-sim README](https://github.com/ngi-collective/ftc-sim#use-it-in-your-teams-code).

### Android Studio

Opening the folder in Android Studio (Narwhal 3 Feature Drop or later) works as it does for any
FTC SDK project, with Studio's bundled JDK and its own SDK; Studio ignores `mise.toml` and
`.vscode/`. One thing to know if you use both: Studio writes your SDK's path to `local.properties`
as `sdk.dir`, and Gradle then uses that SDK in place of mise's, in VS Code and the terminal too.
That works as long as Studio's SDK has the packages; `mise run verify-toolchain` says which SDK is
in use and whether it does. Delete the `sdk.dir` line to go back to mise's.

## Run the tests

```bash
mise run test
```

`mise tasks` lists the rest: `check` compiles TeamCode, `build` builds the competition APK,
`install` puts it on a connected Control Hub, `lint` runs Android lint, and `verify-toolchain`
reports which JDK and SDK Gradle will use and whether they can build the project.

Two tests come with it:

- `MecanumDrivePoseTest` runs `BasicMecanumTeleOp` with simulated gamepad input and checks the
  robot's pose on the field: forward stick drives forward, strafe goes sideways, twist turns in
  place, no power stays put, and a wall stops the robot while its encoders keep counting.
- `SyntheticCameraAcceptanceTest` runs FIRST's own `ConceptAprilTagEasy` sample, unmodified,
  against the simulated camera, and reads the sample's own telemetry: it must report the red
  HIVE's AprilTags, by their season names, at a plausible range.

GitHub Actions runs them on every push on Linux and Windows, with the same mise tasks, and builds
the competition APK (`.github/workflows/test.yml`).

## Drive it in a browser

```bash
mise run dashboard
```

Then open http://localhost:8765. Pick an OpMode, press INIT and START, and drive with a gamepad or
the keyboard. Your OpModes appear there as they do on a Driver Station.

## Make it your robot

- **`TeamCode/robot-config/example.json`** is the robot's physics: chassis size and mass, wheel
  radius, gear ratio, motor speed, encoder resolution, camera mount. Measure yours and edit the
  numbers, or copy the file under a new name. The simulator reads it again at every INIT.
- **`TeamCode/src/simulated/java/.../simulated/ExampleRobot.java`** names the devices, the same
  names your Robot Controller configuration uses. Names are code so that a rename breaks the
  build. If you rename the class, update
  `TeamCode/src/simulated/resources/META-INF/services/org.ngicollective.ftcsim.hardware.SimulatedRobot`,
  which registers it.
- **`TeamCode/build.gradle`** sets the simulator and season versions in its `ftcSim { }` block.
  New releases are listed in the
  [ftc-sim repository](https://github.com/ngi-collective/ftc-sim).

The plugin compiles TeamCode against Android SDK 34, because the simulator's vision tests need it.
Upstream uses 30. The minimum and target SDK, and so the Control Hubs your APK runs on, stay the
same.

## What is here, beyond FIRST's SDK

| Path | What it is |
| --- | --- |
| `TeamCode/build.gradle` | the `org.ngi-collective.ftc-sim` plugin and its `ftcSim { }` block |
| `TeamCode/robot-config/` | the example robot's physics, and the field |
| `TeamCode/src/main/java/.../BasicMecanumTeleOp.java` | a robot-centric mecanum TeleOp, with `DriveHardware`, `ChassisMotors`, `JoystickInput` and `MecanumMovement` |
| `TeamCode/src/simulated/` | `ExampleRobot` and its registration |
| `TeamCode/src/testSimulated/` | the two tests |
| `mise.toml`, `mise-tasks/` | the toolchain (JDK 21, Android SDK) and the tasks above |
| `.vscode/`, `VSCODE.md` | VS Code's recommended extensions and settings, and why |
| `.github/workflows/test.yml` | CI |
| `gradle.properties` | Gradle's heap raised to 2 GB; 1 GB can run out building both APKs and the tests |

## Licence

The FTC SDK is FIRST's; see `LICENSE`. The example robot, OpModes and tests come from ftc-sim
and are BSD-3-Clause; see `LICENSE-ftc-sim`. Change or delete them as you like.
