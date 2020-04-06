package ch.bern.submiss.services.impl;

import ch.bern.submiss.services.impl.tests.UserAdministrationServiceImplTest;
import com.eurodyn.qlack2.common.util.net.NetUtils;
import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import com.eurodyn.qlack2.util.availcheck.mariadb.AvailabilityCheckMariaDB;
import com.eurodyn.qlack2.util.docker.DockerContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  UserAdministrationServiceImplTest.class,
})
public class SubmissIntegrationTests {

  // JUL reference.
  private final static Logger LOGGER = Logger.getLogger(SubmissIntegrationTests.class.getName());

  // Test containers name prefix.
  public static final String TEST_CONTAINER_PREFIX = "TEST-submiss-";

  // The ID of the container created with the database.
  private static String dbContainerId;

  // Docker DB configuration.
  private static final String DB_IMAGE = "mariadb:10.1.26";
  private static final String DB_USERNAME = "root";
  private static final String DB_PASSWORD = "root";
  private static final long DB_MAX_WAITING_FOR_CONTAINER = 1000L * 60L * 10L;
  private static final long DB_MAX_WAITING_PER_CYCLE = 3000L;
  public static int DB_PORT;
  private static String DB_URL;

  static {
    try {
      DB_PORT = NetUtils.getAvailablePort();
      DB_URL = "jdbc:mariadb://localhost" + ":" + DB_PORT + "?useSSL=false";
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not obtain a free network port.", e);
      afterClass();
      System.exit(2);
    }
  }


  @BeforeClass
  public static void beforeClass() {
    // Start the DB container.
    dbContainerId = DockerContainer.builder()
      .withImage(DB_IMAGE)
      .withPort(DB_PORT + "/tcp", "3306/tcp")
      .withEnv("MYSQL_ROOT_PASSWORD", DB_PASSWORD)
      .withName(TEST_CONTAINER_PREFIX + UUID.randomUUID())
      .run();
    Assert.assertNotNull(dbContainerId);

    // Wait for the DB container to become accessible.
    LOGGER.log(Level.INFO, "Waiting for DB to become accessible...");
    AvailabilityCheck dbAvailabilityCheck = new AvailabilityCheckMariaDB();
    if (!dbAvailabilityCheck
      .isAvailable(DB_URL, DB_USERNAME, DB_PASSWORD,
        DB_MAX_WAITING_FOR_CONTAINER, DB_MAX_WAITING_PER_CYCLE, new HashMap<>())) {
      LOGGER.log(Level.SEVERE, "Could not connect to the DB. Tests will be terminated.");
      afterClass();
      System.exit(1);
    } else {
      LOGGER.log(Level.INFO, "DB is accessible.");
    }
  }

  @AfterClass
  public static void afterClass() {
    if (dbContainerId != null) {
      DockerContainer.builder().withId(dbContainerId).clean();
    }
  }
}