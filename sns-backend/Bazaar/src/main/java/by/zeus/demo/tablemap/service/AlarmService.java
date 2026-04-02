package by.zeus.demo.tablemap.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlarmService {

    @PersistenceContext
    private EntityManager entityManager;

    public AlarmService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Her gün sabah 09:00'da çalışır
    public void checkTableAlarms() {
        // `table_alarm` tablosundaki tüm kayıtlı tabloları getir
        List<String> alarmTables = getAlarmTables();

        for (String tableName : alarmTables) {
            List<Object[]> expiringRecords = findExpiringRecords(tableName);
            for (Object[] record : expiringRecords) {
                System.out.println("🚨 Uyarı: " + tableName + " tablosunda bitiş tarihi yaklaşan veri bulundu!");
            }
        }
    }

    // ✅ `table_alarm` tablosundaki tüm tablo isimlerini getir
    private List<String> getAlarmTables() {
        String query = "SELECT table_name FROM table_alarm";
        return entityManager.createNativeQuery(query).getResultList();
    }

    // ✅ Her tablo için `end_date` sütununa sahip olup olmadığını kontrol eder
    private boolean hasEndDateColumn(String tableName) {
        String query = "SELECT EXISTS (SELECT 1 FROM information_schema.columns " +
                "WHERE table_name = :tableName AND column_name = 'end_date')";
        Boolean exists = (Boolean) entityManager.createNativeQuery(query)
                                                .setParameter("tableName", tableName)
                                                .getSingleResult();
        return exists;
    }

    // ✅ Belirtilen tablodaki `end_date` sütununu kontrol eder
    private List<Object[]> findExpiringRecords(String tableName) {
        if (!hasEndDateColumn(tableName)) {
            System.out.println("⚠️ Uyarı: " + tableName + " tablosunda `end_date` sütunu bulunamadı.");
            return List.of();
        }

        String query = "SELECT * FROM " + tableName + " WHERE " +
                "(end_date - INTERVAL '7 days' = CURRENT_DATE " +
                "OR end_date - INTERVAL '3 days' = CURRENT_DATE " +
                "OR end_date - INTERVAL '1 day' = CURRENT_DATE)";

        return entityManager.createNativeQuery(query).getResultList();
    }
}
