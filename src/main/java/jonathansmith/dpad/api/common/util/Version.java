package jonathansmith.dpad.api.common.util;

import java.util.Arrays;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Version matching utilities.
 * MAYBE: Can we move this? Or should we leave it to authors to determin how an outdated plugin is handled?
 */
public class Version {

    private final int[] version_parts;

    public Version(int... versionParts) {
        this.version_parts = versionParts;
    }

    public Version(String version) {
        String[] versionParts = version.split(".");
        this.version_parts = new int[versionParts.length];
        for (int i = 0; i < versionParts.length; i++) {
            this.version_parts[i] = Integer.parseInt(versionParts[i]);
        }
    }

    public static boolean matches(Version version, Version version1) {
        return Arrays.equals(version.getVersionParts(), version1.getVersionParts());
    }

    public static int compareVersions(Version oldVersion, Version newVersion) {
        if (Version.matches(oldVersion, newVersion)) {
            return 0;
        }

        if (oldVersion.getVersionParts().length == newVersion.getVersionParts().length) {
            int[] oldVersionParts = oldVersion.getVersionParts();
            int[] newVersionParts = newVersion.getVersionParts();

            for (int i = 0; i < oldVersionParts.length; i++) {
                if (oldVersionParts[i] > newVersionParts[i]) {
                    return -1;
                }

                else if (oldVersionParts[i] < newVersionParts[i]) {
                    return 1;
                }
            }

            // Really should not happen but hey :P
            return 0;
        }

        return -999;
    }

    private int[] getVersionParts() {
        return this.version_parts;
    }

    public String getVersionString() {
        String versionString = "";
        for (int i = 0; i < this.version_parts.length; i++) {
            versionString += this.version_parts[i];

            if (i != this.version_parts.length - 1) {
                versionString += ".";
            }
        }

        return versionString;
    }
}
