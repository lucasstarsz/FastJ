package tech.fastj.resources;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ResourceManager<T extends Resource<V>, V> {

    protected final Map<String, T> resourceStorage;
    protected final Map<Path, UUID> pathToUUIDMap;
    protected final Map<String, Path> idToPathMap;

    protected ResourceManager() {
        resourceStorage = new ConcurrentHashMap<>();
        pathToUUIDMap = new ConcurrentHashMap<>();
        idToPathMap = new ConcurrentHashMap<>();
    }

    public abstract T createResource(Path resourcePath);

    public List<T> loadResource(Path... resourcePaths) {
        List<T> resources = new ArrayList<>();
        Arrays.stream(resourcePaths).parallel().forEach(path -> resources.add(loadResource(path)));

        return resources;
    }

    public Path tryFindPathOfResource(V rawResource) {
        AtomicReference<Path> resultingPath = new AtomicReference<>();
        resourceStorage.forEach((id, resource) -> {
            if (resource.get().equals(rawResource)) {
                resultingPath.set(idToPath(id));
            }
        });

        if (resultingPath.get() == null) {
            throw new IllegalArgumentException("Couldn't find a matching path for resource " + rawResource);
        }

        return resultingPath.get();
    }

    public T loadResource(Path resourcePath) {
        return loadResource(pathToId(resourcePath.toAbsolutePath()));
    }

    public T getResource(Path resourcePath) {
        return getResource(pathToId(resourcePath));
    }

    public T unloadResource(Path resourcePath) {
        return unloadResource(pathToId(resourcePath));
    }

    public void unloadAllResources() {
        resourceStorage.forEach((path, resource) -> resource.unload());
    }

    @SuppressWarnings("unchecked")
    private T loadResource(String resourceId) {
        T imageResource = resourceStorage.get(resourceId);
        if (imageResource == null) {
            imageResource = createResource(idToPath(resourceId));
        }

        return (T) imageResource.load();
    }

    private T getResource(String resourceId) {
        return resourceStorage.computeIfAbsent(resourceId, id -> loadResource(idToPath(resourceId)));
    }

    private T unloadResource(String resourceId) {
        return resourceStorage.computeIfPresent(resourceId, (id, imageResource) -> {
            imageResource.unload();
            return imageResource;
        });
    }

    private String pathToId(Path resourcePath) {
        UUID uuid = pathToUUIDMap.computeIfAbsent(resourcePath, path -> UUID.randomUUID());
        String id = resourcePath.toAbsolutePath() + uuid.toString();
        idToPathMap.put(id, resourcePath);
        return id;
    }

    private Path idToPath(String resourceId) {
        return idToPathMap.get(resourceId);
    }
}
