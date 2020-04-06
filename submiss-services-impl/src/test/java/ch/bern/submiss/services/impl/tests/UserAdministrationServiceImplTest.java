package ch.bern.submiss.services.impl.tests;

import javax.inject.Inject;

import org.junit.Test;

import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.impl.conf.ITTestConf;

/**
 * @author European Dynamics SA
 */
//@RunWith(PaxExam.class)
//@ExamReactorStrategy(PerSuite.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAdministrationServiceImplTest extends ITTestConf {

  @Inject
//  @Filter(timeout = 1000 * 60 * 10)
  UserAdministrationService userAdministrationService;

  @Test
  public void createUser() {
//    // Create test user.
//    String username = UUID.randomUUID().toString();
//    String groupName = UUID.randomUUID().toString();
//    UserDTO userDTO = new UserDTO();
//    userDTO.setUsername(username);
//    String userId = userAdministrationService.createUser(userDTO, groupName, false);
//
//    // Assert new user was correctly created.
//    final SubmissUserDTO userById = userAdministrationService.getUserById(userId);
//    Assert.assertNotNull(userById);
//    Assert.assertEquals(username, userById.getUsername());
  }


}