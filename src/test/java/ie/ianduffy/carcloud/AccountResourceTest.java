package ie.ianduffy.carcloud;

import ie.ianduffy.carcloud.domain.User;
import ie.ianduffy.carcloud.web.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles({"dev", "test"})
public class AccountResourceTest extends AbstractResourceTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        User user = userService.findOne("user");

        mockMvc.perform(get("/app/rest/account").with(
            user(user))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value(user.getUsername()))
            .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(user.getLastName()))
            .andExpect(jsonPath("$.email").value(user.getEmail()))
            .andExpect(jsonPath("$.phone").value(user.getPhone()))
            .andExpect(jsonPath("$.links.authorities").exists())
            .andExpect(jsonPath("$.links.self").exists());
    }

    @Test
    public void testRegisterAccount() throws Exception {
        UserDTO userDTO = new UserDTO();

        userDTO.setUsername("mburns");
        userDTO.setFirstName("montgomery");
        userDTO.setLastName("burns");
        userDTO.setEmail("mburns@carcloud.ianduffy.ie");
        userDTO.setPhone("+3530000000000");
        userDTO.setPassword("password");

        mockMvc.perform(post("/app/rest/account")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/app/rest/account")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testUpdateAnAccount() throws Exception {
        UserDTO userDTO = new UserDTO();

        userDTO.setUsername("mburns");
        userDTO.setFirstName("montgomery");
        userDTO.setLastName("burns");
        userDTO.setEmail("mburns@carcloud.ianduffy.ie");
        userDTO.setPhone("+3530000000000");
        userDTO.setPassword("password");
        userDTO.setVersion(userService.findOne("user").getVersion());

        mockMvc.perform(put("/app/rest/account")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO))
            .with(user(userService.findOne("user"))))
            .andExpect(status().isOk());
    }
}
