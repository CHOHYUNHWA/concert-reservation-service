package kr.hhplus.be.server.util;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleanUp {

    @PersistenceContext
    private EntityManager entityManager;

    private final List<String> tables = new ArrayList<>();

    @PostConstruct
    public void afterPropertiesSet() {
        tables.addAll(
                entityManager.getMetamodel().getEntities().stream()
                        .filter(entity -> entity.getJavaType().isAnnotationPresent(Entity.class))
                        .map(entity -> entity.getJavaType().getAnnotation(Table.class).name())
                        .toList()
        );
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (String table : tables) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
            try {
                // PK 가 A.I 아니면 터져요..
                entityManager.createNativeQuery("ALTER TABLE " + table + " AUTO_INCREMENT = 1").executeUpdate();
            } catch (Exception ignored) {

            }
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
