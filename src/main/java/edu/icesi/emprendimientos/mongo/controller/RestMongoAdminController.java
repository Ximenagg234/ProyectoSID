package edu.icesi.emprendimientos.mongo.controller;

import edu.icesi.emprendimientos.mongo.service.MongoSeederService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile("sid")
@RestController
@RequestMapping("/api/mongo/admin")
public class RestMongoAdminController {

    private final MongoSeederService seeder;

    public RestMongoAdminController(MongoSeederService seeder) {
        this.seeder = seeder;
    }

    /** Fuerza re-siembra completa de MongoDB (solo ADMIN) */
    @PostMapping("/reseed")
    public ResponseEntity<Map<String, String>> reseed() {
        seeder.forceReseed();
        return ResponseEntity.ok(Map.of("status", "MongoDB resembrado correctamente"));
    }
}
