package me.invakid.azran.controller;

import me.invakid.azran.service.AzranService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

@Controller
public class AzranController {

    @Autowired
    private AzranService service;

    @RequestMapping(value = "/wrong-token", method = RequestMethod.GET)
    public String wrongTokenMapping() {
        return "wrong-token";
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    public String downloadHandler() {
        return "download";
    }

    @PostMapping(value = "/{token}")
    public ResponseEntity<Object> downloadPost(@PathVariable("token") String token) throws Exception {
        if (service.isTokenValid(token)) {
            service.removeToken(token);

            File file = new File("Azran.exe");

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentDispositionFormData("attachment", RandomStringUtils.randomAlphabetic(8) + ".exe");
            respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
            return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
        }

        HttpHeaders errorHeaders = new HttpHeaders();
        errorHeaders.add("Location", "/wrong-token");

        return new ResponseEntity<>(errorHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping(value = "/favicon.ico")
    public ResponseEntity<InputStreamResource> download() throws Exception {
        File file = new File("favicon.ico");

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentDispositionFormData("attachment", "favicon.ico");

        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
    }

}
