package codesquad.domain;

import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class UserTest extends BaseTest {
    public static final User MOVINGLINE = new User(1L, "movingline", "123456", "name", "movinglinecheck@gmail.com");
    public static final User ZINGOWORKS = new User(2L, "zingoworks", "123456", "name", "zingoworks@gmail.com");

    public static User newUser(Long id) {
        return new User(id, "userId", "1234", "name", "movinglinecheck@gmail.com");
    }

    public static User newUser(String userId) {
        return newUser(userId, "123456");
    }

    public static User newUser(String userId, String password) {
        return new User(0L, userId, password, "name", "movinglinecheck@gmail.com");
    }

    @Test
    public void update_owner() throws Exception {
        User origin = newUser("movingline");
        User loginUser = origin;
        User target = new User("movingline", "123456", "name", "movinglinecheck@gmail.com");
        origin.update(loginUser, target);
        softly.assertThat(origin.getName()).isEqualTo(target.getName());
        softly.assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User origin = newUser("zingoworks");
        User loginUser = newUser("movingline");
        User target = new User("zingoworks", "123456", "name", "zingoworks@gmail.com");
        origin.update(loginUser, target);
    }

    @Test
    public void update_match_password() {
        User origin = newUser("movingline");
        User target = new User("movingline", "123456", "name", "movinglinecheck@gmail.com");
        origin.update(origin, target);
        softly.assertThat(origin.getName()).isEqualTo(target.getName());
        softly.assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_mismatch_password() {
        User origin = newUser("movingline", "123456");
        User target = new User("movingline", "1234567", "name", "movinglinecheck@gmail.com");
        origin.update(origin, target);
    }
}
