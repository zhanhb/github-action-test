package sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void contextLoads() throws Exception {
		assertThrows(
			ClassNotFoundException.class,
			() -> Class.forName("org.codehaus.groovy.runtime.InvokerHelper"));

		String content = mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("Content"))
			.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
			.andReturn().getResponse().getContentAsString();

		assertThat(content).contains("My footer").doesNotContain("Custom footer here");
	}

}
