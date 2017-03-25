package LocMess;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by joao on 3/25/17.
 */
@RestController
public class RequestController {

        //private static final String template = "Hello, %s!";
        //private final AtomicLong counter = new AtomicLong();

        @RequestMapping("/login")
        public Cookie login(@RequestParam(value="name", defaultValue="World") String name) {
            return new Cookie();
        }

}
