package net.rtxyd.fallen.lib.service;

import com.google.common.collect.ImmutableMap;
import cpw.mods.jarhandling.JarContents;
import cpw.mods.jarhandling.JarMetadata;
import cpw.mods.jarhandling.SecureJar;
import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.fml.loading.moddiscovery.ModJarMetadata;
import net.neoforged.fml.loading.moddiscovery.readers.JarModsDotTomlModFileReader;
import net.neoforged.neoforgespi.locating.*;
import net.rtxyd.fallen.lib.FallenCoreLib;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Manifest;

import static net.rtxyd.fallen.lib.service.FallenBootstrap.LOGGER;

public class SimpleForgeConnector implements IDependencyLocator {

    Path getPath() {
        URL url = this.getClass().getClassLoader().getResource(FallenCoreLib.FORGE_MOD_LOC);
        if (url == null) {
            if (FallenBootstrap.isDevEnvironment) {
                LOGGER.warn("Run fallen core lib in dev environment without runtime lib.");
                return null;
            } else {
                url = this.getClass().getClassLoader().getResource("net/rtxyd/fallen/lib/runtime/forgemod/FallenLib.class");
                if (url == null) {
                    // runtime should always pair with core.
                    throw new RuntimeException("Can't find fallen runtime lib!");
                } else {
                    // if we found the class, it means forge can load it.
                    return null;
                }
            }
        }
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Failed transfer url %s to URI!", url));
        }
    }

    @Override
    public String toString() {
        return "fallen_lib_mod_locator";
    }

    @Override
    public void scanMods(List<IModFile> loadedMods, IDiscoveryPipeline pipeline) {
        Optional<JarContents> result = scanModsInner();
        result.ifPresent(jarContents -> pipeline.addJarContent(jarContents, ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.ERROR));
    }

    // forge way to adapt the union path.
    @SuppressWarnings("resource")
    private Optional<JarContents> scanModsInner() {
        Path pathInModFile = getPath();
        if (pathInModFile == null) return Optional.empty();
        try {
            final URI filePathUri = new URI("jij:" + (pathInModFile.toAbsolutePath().toUri().getRawSchemeSpecificPart())).normalize();
            final Map<String, ?> outerFsArgs = ImmutableMap.of("packagePath", pathInModFile);
            // we want to hold the file for a long time, so don't need to handle close.
            final FileSystem zipFS = FileSystems.newFileSystem(filePathUri, outerFsArgs);
            final Path pathInFS = zipFS.getPath("/");
            var jar = JarContents.of(pathInFS);
            return Optional.of(jar);
        } catch (Exception e) {
            LOGGER.error("Failed to load mod fallen lib runtime from fallen lib");
            final RuntimeException exception = new ModFileLoadingException("Failed to load fallen lib runtime");
            exception.initCause(e);

            throw exception;
        }
    }
}
