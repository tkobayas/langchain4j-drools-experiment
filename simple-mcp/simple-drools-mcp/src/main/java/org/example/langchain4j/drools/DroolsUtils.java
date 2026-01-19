package org.example.langchain4j.drools;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.drools.model.codegen.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

public class DroolsUtils {

    private static final String EXAMPLE_PKG = "org/example/";

    private DroolsUtils() {
    }

    // expects files under resources/org/example/
    public static KieBase createKieBase(String... fileNames) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        for (String fileName : fileNames) {
            String resourcePath = EXAMPLE_PKG + fileName;
            URL url = DroolsUtils.class.getClassLoader().getResource(resourcePath);
            if (url == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }

            try (InputStream is = url.openStream()) {
                byte[] bytes = is.readAllBytes();
                kfs.write("src/main/resources/" + resourcePath,
                          ks.getResources().newByteArrayResource(bytes));
            } catch (Exception e) {
                throw new RuntimeException("Failed to read DRL file: " + resourcePath, e);
            }
        }

        ReleaseId releaseId = ks.newReleaseId("org.example.langchain4j.drools", "drools-agent", "1.0.0");
        kfs.generateAndWritePomXML(releaseId);
        ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        KieContainer kcontainer = ks.newKieContainer(releaseId);
        return kcontainer.getKieBase();
    }

    // Keep this method for backward compatibility with tests
    public static KieBase createKieBase(List<Path> paths) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Path path : paths) {
            try (InputStream is = path.toUri().toURL().openStream()) {
                byte[] bytes = is.readAllBytes();
                kfs.write("src/main/resources/" + EXAMPLE_PKG + path.getFileName().toString(),
                          ks.getResources().newByteArrayResource(bytes));
            } catch (Exception e) {
                throw new RuntimeException("Failed to read DRL file: " + path, e);
            }
        }
        ReleaseId releaseId = ks.newReleaseId("org.example.langchain4j.drools", "drools-agent", "1.0.0");
        kfs.generateAndWritePomXML(releaseId);
        ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        KieContainer kcontainer = ks.newKieContainer(releaseId);
        return kcontainer.getKieBase();
    }
}
