package tech.fastj.asset;

import java.util.Collections;
import java.util.TreeSet;

public interface AssetType<AT extends AssetType<?>> extends Comparable<AT> {
    String getName();

    TreeSet<String> getKnownExtensions();

    @Override
    default int compareTo(AT otherAssetType) {
        if (this == otherAssetType) {
            return 0;
        }

        if (otherAssetType == null) {
            return -1;
        }

        int nameComparison = String.CASE_INSENSITIVE_ORDER.compare(getName(), otherAssetType.getName());

        if (nameComparison != 0) {
            return nameComparison;
        }

        var extensions = Collections.unmodifiableSet(getKnownExtensions());
        var otherExtensions = Collections.unmodifiableSet(otherAssetType.getKnownExtensions());

        var extensionsIterator = extensions.iterator();
        var otherExtensionsIterator = otherExtensions.iterator();

        int maxCheckSize = Math.max(extensions.size(), otherExtensions.size());

        for (int i = 0; i < maxCheckSize; i++) {
            String extension = extensionsIterator.next();
            String otherExtension = otherExtensionsIterator.next();

            int extensionComparison = String.CASE_INSENSITIVE_ORDER.compare(extension, otherExtension);

            if (extensionComparison != 0) {
                return extensionComparison;
            }
        }

        return Integer.compare(extensions.size(), otherExtensions.size());
    }
}
