package org.ioad.spring.language.controllers;

import org.ioad.spring.language.models.Language;
import org.ioad.spring.language.repository.LangRepository;
import org.ioad.spring.security.postgresql.models.User;
import org.ioad.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/lang")
@CrossOrigin(origins = "*")
public class LangController {
    @Autowired
    private LangRepository langRepository;
    @Autowired
    private UserRepository userRepository;

    public LangController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/{id}/{lang}")
    public ResponseEntity<String> changeUserLang(
            @PathVariable Long id,
            @PathVariable String lang
    ) {
        if (lang == null) {
            return ResponseEntity.badRequest().body("Language cannot be null");
        } else if (!"en".equals(lang) && !"pl".equals(lang)) {
            return ResponseEntity.badRequest().body("Language must be either 'en' or 'pl'");

        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Language language = langRepository.findFirstByUser_Id(user.getId());
        language.setLanguage(lang);
        langRepository.save(language);
        return ResponseEntity.ok("Language changed to " + lang);
    }

    @RequestMapping("/getlang/{id}")
    public ResponseEntity<String> getUserLang(
            @PathVariable Long id
    ) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Language language = langRepository.findFirstByUser_Id(user.getId());
        return ResponseEntity.ok(language.getLanguage());
    }
}

