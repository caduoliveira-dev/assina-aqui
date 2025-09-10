package com.assinaaqui.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/info")
@CrossOrigin(origins = "http://localhost:3000")
public class InfoController {

    @GetMapping("/hostname")
    public ResponseEntity<Map<String, String>> getHostname() {
        Map<String, String> response = new HashMap<>();

        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String containerID = System.getenv("HOSTNAME");

            response.put("hostname", hostname);
            response.put("containerID", containerID != null ? containerID : hostname);
            response.put("instanceID", containerID != null ? containerID.substring(0, 12) : hostname);
        } catch (UnknownHostException e) {
            response.put("hostname", "unknown");
            response.put("containerID", "unknown");
            response.put("instanceID", "unknown");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "assina-aqui-backend");
        return ResponseEntity.ok(response);
    }
}
