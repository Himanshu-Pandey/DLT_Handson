package thoughtworks.quorum.clientapi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import thoughtworks.quorum.clientapi.service.PodType;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PodConfigurationTest {
    @Test
    public void shouldConfigurePODProfile() throws IOException {
        String json = "{\"booth1\":{\"host\":\"172.10.1.2\",\"port\":\"24002\",\"type\":\"BOOTH\",\"accounts\":[{\"name\":\"JUHI\",\"address\":\"3a1076bf45ab87712ad64ccb3b10217737f7faacbf2872e88fdd9a537d8fe266\"},{\"name\":\"NAVDEEP\",\"address\":\"6d07f7ecd0b9de76c10de994fccf49a21a43ad5e7edb603e65937ff61ce2e6aa\"}]},\"booth2\":{\"host\":\"172.10.1.3\",\"port\":\"24003\",\"type\":\"BOOTH\",\"accounts\":[{\"name\":\"HIMANSHU\",\"address\":\"f2b618b041a197244a655efc055e6753f8b71356fd66e7a1d58d10cc818da334\"},{\"name\":\"MANALI\",\"address\":\"9d163b5b4e09792e3eea6b2946f1814ee61566411aa47ed9c771c5ed71e46e42\"}]},\"EC\":{\"host\":\"172.10.1.1\",\"port\":\"24001\",\"type\":\"EC\",\"accounts\":[{\"name\":\"EC1\",\"address\":\"d55ae7c247804428ca98ed524764d7b3f8f009c4147faac06b97033ecc1b986b\"},{\"name\":\"EC2\",\"address\":\"6d07f7ecd0b9de76c10de994fccf49a21a43ad5e7edb603e65937ff61ce2e6aa\"}]}}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, PodConfiguration> podConfigurationMap =
            mapper.readValue(json, new TypeReference<Map<String, PodConfiguration>>() {
            });

        assertThat(podConfigurationMap.get("booth1").getType(), is(PodType.BOOTH));

    }


}
