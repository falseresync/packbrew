package ru.falseresync.packbrew.model;


import java.net.URI;
import java.util.List;

public class VersionManifest {
    private Latest latest;
    private List<Version> versions;

    public Latest getLatest() {
        return latest;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public static class Latest {
        private String release;
        private String snapshot;

        public String getRelease() {
            return release;
        }

        public String getSnapshot() {
            return snapshot;
        }
    }

    public static class Version {
        private String id;
        private VersionType type;
        private URI url;
        private String time;
        private String releaseTime;

        public String getId() {
            return id;
        }

        public VersionType getType() {
            return type;
        }

        public URI getUrl() {
            return url;
        }

        public String getTime() {
            return time;
        }

        public String getReleaseTime() {
            return releaseTime;
        }
    }
}
