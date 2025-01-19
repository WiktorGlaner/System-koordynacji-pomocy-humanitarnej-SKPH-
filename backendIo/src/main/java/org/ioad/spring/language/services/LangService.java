package org.ioad.spring.language.services;

import org.ioad.spring.language.ILangService;
import org.ioad.spring.language.models.Language;
import org.ioad.spring.language.repository.LangRepository;
import org.ioad.spring.auth.models.User;
import org.ioad.spring.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LangService implements ILangService {
    @Autowired
    private LangRepository langRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void addLangRecord(String lang, User user) {
        Language language = new Language();
        language.setLanguage(lang);
        language.setUser(user);
        langRepository.save(language);
    }

    public String getUserLang(Long id) {
        return langRepository.findFirstByUser_Id(id).getLanguage();
    }

    public ResponseEntity<String> changeUserLang(Long id, String lang) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Language language = langRepository.findFirstByUser_Id(user.getId());
        language.setLanguage(lang);
        langRepository.save(language);
        return ResponseEntity.ok("Language changed to " + lang);
    }
}
