package org.ioad.spring.language;

import org.ioad.spring.auth.models.User;

public interface ILangService {
    void addLangRecord(String lang, User user);
}
