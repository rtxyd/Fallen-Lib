package net.rtxyd.fallen.lib.service;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import net.minecraftforge.forgespi.locating.IModLocator;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class SimpleModLocator extends AbstractJarFileModLocator implements IModLocator {

    @Override
    public Stream<Path> scanCandidates() {
        final String runtime_loc = "META-INF/runtime/";
        final String mod_name = "fallen_lib";
        final String version = "1.2.0";
        final String classifier = "runtime";
        final String fullPath = runtime_loc
                + mod_name
                + "-"
                + version
                + "-"
                + classifier
                + ".jar";
        var paths = Stream.<Path>builder();
        try {
            URL url = this.getClass().getClassLoader().getResource(fullPath);
            if (url == null) {
                throw new RuntimeException("Can't find runtime jar in fallen lib!");
            }
            return paths.add(Path.of(url.toURI())).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return "fallen lib mod locator";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {}
}
