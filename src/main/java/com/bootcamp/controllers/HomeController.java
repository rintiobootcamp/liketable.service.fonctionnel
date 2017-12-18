/**
 * Created by darextossa on 5/4/17.
 */

package com.bootcamp.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Bello
 */
@Controller
public class HomeController {

    /**
     * The web service home page
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}