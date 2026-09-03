package dev.mariany.keepinventorynerf.config;

public class KINClientConfig {
    public DeathScreenConfig deathScreen = new DeathScreenConfig();

    public static class DeathScreenConfig {
        public boolean enabled = true;
        public LevelsConfig levels = new LevelsConfig();
        public ItemsConfig items = new ItemsConfig();

        public static class LevelsConfig {
            public int verticalOffset = 65;
        }

        public static class ItemsConfig {
            public int verticalOffset = 88;
        }
    }
}
