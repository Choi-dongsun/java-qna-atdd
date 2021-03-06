package codesquad.web;

import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class HomeAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(HomeAcceptanceTest.class);

    @Autowired
    QuestionRepository questionRepository;

    @Test
    public void home() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

}
