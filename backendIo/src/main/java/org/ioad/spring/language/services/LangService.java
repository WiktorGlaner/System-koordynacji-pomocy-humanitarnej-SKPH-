package org.ioad.spring.language.services;

import org.ioad.spring.language.models.Language;
import org.ioad.spring.language.repository.LangRepository;
import org.ioad.spring.security.postgresql.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LangService {
    @Autowired
    private LangRepository langRepository;

    public void addLangRecord(String lang, User user) {
        Language language = new Language();
        language.setLanguage(lang);
        language.setUser(user);
        langRepository.save(language);
    }

    public String getUserLang(Long id) {
        return langRepository.findFirstByUser_Id(id).getLanguage();
    }
}
