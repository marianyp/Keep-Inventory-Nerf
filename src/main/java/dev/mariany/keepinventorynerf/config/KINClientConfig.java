package dev.mariany.keepinventorynerf.config;

public class KINClientConfig {
    public DeathScreenConfig deathScreen = new DeathScreenConfig();

    public static class DeathScreenConfig {
        public boolean enabled = true;
        public ItemsConfig items = new ItemsConfig();
        public LevelsConfig levels = new LevelsConfig();

        public static class LevelsConfig {
            public int textY = 100;
        }

        public static class ItemsConfig {
            public int horizontalOffset = 146;
        }
    }
}
