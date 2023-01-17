package tech.fastj.asset;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

public record CustomAssetType(String name, TreeSet<String> knownExtensions) implements AssetType<CustomAssetType> {
    public CustomAssetType(String name, String... knownExtensions) {
        this(name, Arrays.stream(knownExtensions).collect(Collectors.toCollection(TreeSet::new)));
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public TreeSet<String> getKnownExtensions() {
        return knownExtensions();
    }
}
