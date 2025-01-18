package org.ioad.spring.language.controllers;

import org.ioad.spring.language.models.Language;
import org.ioad.spring.language.repository.LangRepository;
import org.ioad.spring.language.services.LangService;
import org.ioad.spring.security.postgresql.IAuthService;
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
    private LangService langService;
    //@Autowired
    //private UserRepository userRepository;
    @Autowired
    private IAuthService iAuthService;

    //public LangController(UserRepository userRepository) {
    //    this.userRepository = userRepository;
    //}

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
        return langService.changeUserLang(id, lang);
    }

    @RequestMapping("/getlang/{id}")
    public ResponseEntity<String> getUserLang(
            @PathVariable Long id
    ) {
        User user = iAuthService.getUserById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        String language = langService.getUserLang(user.getId());
        return ResponseEntity.ok(language);
    }
}

