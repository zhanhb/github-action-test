package sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class TestController {

	@RequestMapping(method = GET)
	public String test() {
		return "Content";
	}

}
