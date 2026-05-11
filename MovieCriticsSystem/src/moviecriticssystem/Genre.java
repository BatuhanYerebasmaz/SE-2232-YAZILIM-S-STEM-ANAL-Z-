package moviecriticssystem;

public enum Genre {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    ANIMATION("Animation"),
    COMEDY("Comedy"),
    CRIME("Crime"),
    DOCUMENTARY("Documentary"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    MUSICAL("Musical"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Science Fiction"),
    THRILLER("Thriller"),
    WAR("War"),
    WESTERN("Western");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Genre fromString(String value) {
        if (value == null) return null;
        for (Genre g : values()) {
            if (g.displayName.equalsIgnoreCase(value) || g.name().equalsIgnoreCase(value))
                return g;
        }
        return null;
    }
}
