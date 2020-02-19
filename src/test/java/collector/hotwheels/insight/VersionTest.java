package collector.hotwheels.insight;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.junit.Test;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;

/**
 * This Test-Class is used to validate the version in the pom file and make sure that we are not in demo mode
 */
public class VersionTest {

    @Test
    public void test_pom_version() throws Exception {

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));

        System.out.println(model.getVersion());

        int countDots = StringUtils.countMatches(model.getVersion(), ".");

        assertEquals(countDots, 2); // We just want to have a 3 digit version to publish
    }
}