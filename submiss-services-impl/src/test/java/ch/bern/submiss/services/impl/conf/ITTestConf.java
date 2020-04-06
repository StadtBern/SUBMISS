package ch.bern.submiss.services.impl.conf;

import static com.eurodyn.qlack2.util.testing.TestingUtil.copyITConf;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.url;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import ch.bern.submiss.services.impl.SubmissIntegrationTests;
import com.google.common.collect.ImmutableMap;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ITTestConf {

  private final static Logger LOGGER = Logger.getLogger(ITTestConf.class.getName());
  protected static final String COVERAGE_COMMAND = "jcoverage.command";

  private static Option addCodeCoverageOption() {
    String coverageCommand = System.getenv(COVERAGE_COMMAND);
    if (coverageCommand != null) {
      LOGGER.log(Level.INFO, "Setting coverage command to: " + coverageCommand);
      return CoreOptions.vmOption(coverageCommand);
    }
    return null;
  }

  @Configuration
  public static Option[] config() throws IOException {
    MavenArtifactUrlReference karafArtifact = maven()
      .groupId("org.apache.karaf")
      .artifactId("apache-karaf")
      .versionAsInProject()
      .version("4.0.9")
      .type("zip");

    MavenUrlReference qlack2FuseFeatures = maven()
      .groupId("com.eurodyn.qlack2.fuse")
      .artifactId("qlack2-fuse-karaf-features")
      .versionAsInProject()
      .classifier("features")
      .type("xml");

    MavenUrlReference qlack2UtilFeatures = maven()
      .groupId("com.eurodyn.qlack2.util")
      .artifactId("qlack2-util-karaf-features")
      .versionAsInProject()
      .classifier("features")
      .type("xml");

    MavenArtifactUrlReference karafStandardFeatures = maven()
      .groupId("org.apache.karaf.features")
      .artifactId("standard")
      .versionAsInProject()
      .classifier("features")
      .type("xml");

    MavenUrlReference projectFeatures = maven()
      .groupId("ch.bern.submiss")
      .artifactId("submiss-karaf-features")
      .versionAsInProject()
      .classifier("features")
      .type("xml");

    String localRepository = System.getProperty("org.ops4j.pax.url.mvn.localRepository");

    return new Option[]{
      karafDistributionConfiguration()
        .frameworkUrl(karafArtifact)
        .unpackDirectory(new File("target", "exam"))
        .useDeployFolder(false),
      keepRuntimeFolder(),
      copyITConf("etc/com.eurodyn.qlack2.fuse.scheduler.cfg"),
      copyITConf("etc/com.eurodyn.qlack2.util.liquibase.cfg"),
      copyITConf("etc/org.ops4j.datasource-managed.cfg",
        ImmutableMap.of("hostPort", SubmissIntegrationTests.DB_PORT, "dockerEngineHost",
          "localhost")),
      copyITConf("etc/org.ops4j.datasource-nonmanaged.cfg",
        ImmutableMap.of("hostPort", SubmissIntegrationTests.DB_PORT, "dockerEngineHost",
          "localhost")),
      when(localRepository != null)
        .useOptions(editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg",
        "org.ops4j.pax.url.mvn.localRepository", localRepository)),
      logLevel(LogLevelOption.LogLevel.INFO),
      configureConsole().ignoreLocalConsole(),
      addCodeCoverageOption(),
      features(qlack2FuseFeatures, "pax-jdbc-mariadb"),
      features(qlack2UtilFeatures, "qlack2-util-liquibase"),
      features(projectFeatures, "submiss-deps"),
      features(projectFeatures, "submiss-deps-test"),
      url("file:../../../../submiss-services-api/target/submiss-services-api-" +
        MavenUtils.getArtifactVersion("ch.bern.submiss", "submiss-services-api") + ".jar"),
      url("file:../../submiss-services-impl-" +
        MavenUtils.getArtifactVersion("ch.bern.submiss", "submiss-services-impl") + ".jar"),
    };
  }
}