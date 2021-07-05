package vitriol.mvcservice;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import static org.assertj.core.api.Assertions.assertThat;

class ProfileControllerTest {

    @Test
    void real_profile_조회() {
        String real = "real1";
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile(real);
        env.addActiveProfile("real-db");

        ProfileController profileController = new ProfileController(env);
        assertThat(profileController.profile()).isEqualTo(real);
    }

    @Test
    void default_조회() {
        String expected = "default";
        MockEnvironment env = new MockEnvironment();
        ProfileController profileController = new ProfileController(env);
        assertThat(profileController.profile()).isEqualTo(expected);
    }
}