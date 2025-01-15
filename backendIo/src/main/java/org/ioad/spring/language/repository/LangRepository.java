package org.ioad.spring.language.repository;

import org.ioad.spring.language.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LangRepository extends JpaRepository<Language, Long> {
    //List<Language> findByUser_Id(Long userId);
    Language findFirstByUser_Id(Long userId);
}
