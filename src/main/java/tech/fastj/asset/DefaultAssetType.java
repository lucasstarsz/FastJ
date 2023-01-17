package tech.fastj.asset;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

public enum DefaultAssetType implements AssetType<DefaultAssetType> {
    Image("Image", ".bmp", ".jpeg", ".jpg", ".wbmp", ".png", ".gif", ".tif", ".tiff"),
    Audio("Audio", ".mp3", ".ogg", ".aif", ".aiff", ".aifc", ".snd", ".wav", ".au"),
    Prefab("FastJ Prefab", ".fjpb"),
    Scene("FastJ Scene", ".fastj");

    private final String name;
    private final TreeSet<String> knownExtensions;

    DefaultAssetType(String name, String... knownExtensions) {
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
