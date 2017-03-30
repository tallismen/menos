package nl.anwb.hv.menos;

import nl.anwb.hv.menos.SpringNoodoproepServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringNoodoproepServer.class)
public class SpringNoodoproepServerTest {

    @Test
    public void contextLoads() {
    }

}
