package org.example.langchain4j.drools;

import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < fileNames.length; i++) {
            URL url = DroolsUtils.class.getClassLoader().getResource(EXAMPLE_PKG + fileNames[i]);
            try {
                paths.add(Paths.get(url.toURI()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return createKieBase(paths);
    }

    public static KieBase createKieBase(List<Path> paths) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Path path : paths) {
            try {
                kfs.write("src/main/resources/" + EXAMPLE_PKG + path.getFileName().toString(),
                          ks.getResources().newInputStreamResource(new FileInputStream(path.toFile())));
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

    public static String getDrlRulesAsString(String drlFileName) {
        URL url = DroolsUtils.class.getClassLoader().getResource(EXAMPLE_PKG + drlFileName);
        try {
            Path path = Paths.get(url.toURI());
            return java.nio.file.Files.readString(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read DRL file: " + drlFileName, e);
        }
    }
}
