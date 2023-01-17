package tech.fastj.asset;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class ExtendableAssetType implements AssetType<ExtendableAssetType> {

    protected String name;
    protected TreeSet<String> knownExtensions;

    protected ExtendableAssetType(String name) {
        this.name = name;
        this.knownExtensions = new TreeSet<>();
    }

    protected ExtendableAssetType(String name, String... knownExtensions) {
        this.name = name;
        this.knownExtensions = Arrays.stream(knownExtensions).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TreeSet<String> getKnownExtensions() {
        return knownExtensions;
    }
}