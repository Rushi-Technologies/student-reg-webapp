package com.rushi.controller;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class RegistrationApiController {

   
    private static final Logger logger = LoggerFactory.getLogger(RegistrationApiController.class);
   
    /**
     * Endpoint to fetch all registrations.
     * This method retrieves the list of registrations from the HomeController.
     *
     * @return List of RegistrationForm objects
     */

    @GetMapping("/api/registrations")
    public List<HomeController.RegistrationForm> getRegistrations() {
        logger.info("Fetching all registrations");
        return HomeController.REGISTRATIONS;
    }
    
   
}
