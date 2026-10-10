import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Accepts the Android SDK licences and installs tools/sdk-packages.txt into ANDROID_HOME.
 * `mise run setup-sdk` runs it with `java tools/SetupSdk.java`, so the same code works on macOS,
 * Linux and Windows without a shell: Windows' cmd has no `yes` to answer the licence prompts.
 */
public class SetupSdk {
    private static final String PACKAGES = "tools/sdk-packages.txt";

    public static void main(String[] args) throws IOException, InterruptedException {
        String root = System.getenv("ANDROID_HOME");
        if (root == null || root.isBlank()) {
            System.err.println("setup-sdk: ANDROID_HOME is not set; run this through `mise run setup-sdk`");
            System.exit(1);
        }
        int licences = run(sdkmanager("--sdk_root=" + root, "--licenses"), true);
        if (licences != 0) System.exit(licences);
        System.exit(run(sdkmanager("--sdk_root=" + root, "--package_file=" + PACKAGES), false));
    }

    // Windows needs the .bat named, and the package list goes in a file because cmd splits a
    // batch file's arguments at semicolons, which every package name has.
    private static List<String> sdkmanager(String... args) {
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        List<String> command = new ArrayList<>(List.of(windows ? "sdkmanager.bat" : "sdkmanager"));
        command.addAll(List.of(args));
        return command;
    }

    private static int run(List<String> command, boolean acceptAll) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true);
        builder.redirectOutput(acceptAll ? ProcessBuilder.Redirect.DISCARD : ProcessBuilder.Redirect.INHERIT);
        Process process = builder.start();
        try (OutputStream answers = process.getOutputStream()) {
            if (acceptAll) answers.write("y\n".repeat(50).getBytes(StandardCharsets.US_ASCII));
        }
        return process.waitFor();
    }
}
