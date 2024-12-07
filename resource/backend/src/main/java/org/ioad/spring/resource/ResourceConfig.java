package org.ioad.spring.resource;

import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.models.ResourceType;
import org.ioad.spring.resource.repositories.ResourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

//Creates default resources for testing

@Configuration
public class ResourceConfig{

    @Bean
    CommandLineRunner commandLineRunner(ResourceRepository repository) {
        return args -> {
            Resource chleb = new Resource(
                "chleb",
                    "Łódź",
                    LocalDate.of(2030, Month.JANUARY, 5),
                    10D,
                    "pcs",
                    1L,
                    ResourceType.FOOD
            );

            Resource jajko = new Resource(
                    "jajko",
                    "Częstochowa",
                    LocalDate.of(2050, Month.JANUARY, 5),
                    5010D,
                    "pcs",
                    2L,
                    ResourceType.FOOD
            );

            repository.saveAll(List.of(jajko, chleb));
        };
    }
}