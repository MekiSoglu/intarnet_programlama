package by.zeus.demo.dynamicquery.service.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DynamicProductQueryBuilder {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Map<String, String> ENTITY_MAP = new HashMap<>();

    static {
        ENTITY_MAP.put("Product", "ProductEntity");
        ENTITY_MAP.put("ProductDetails", "ProductDetailsEntity");
    }

    public List<Long> buildAndExecuteQuery(String tableName, String columnName, Object value, Long categoryDetailsId) {
        String entityName = ENTITY_MAP.get(tableName);

        if (entityName == null) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        System.out.println("📌 Gelen Parametreler -> Table: " + tableName + ", Column: " + columnName + ", Value: " + value + ", CategoryDetailsId: " + categoryDetailsId);

        if (value == null) {
            throw new IllegalArgumentException("Value parametresi null olamaz!");
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT p.id FROM ProductEntity p INNER JOIN ProductDetailsEntity pd ON pd.productId = p.id ");

        if ("Product".equalsIgnoreCase(tableName)) {
            queryBuilder.append(" WHERE p.")
                        .append(columnName)
                        .append(" = :value");
        } else if ("ProductDetails".equalsIgnoreCase(tableName)) {
            queryBuilder.append(" WHERE pd.value = :value");

            if (categoryDetailsId != null) {
                queryBuilder.append(" AND pd.categoryDetailsId = :categoryDetailsId");
            }
        } else {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        System.out.println("📌 Oluşturulan JPQL: " + queryBuilder.toString());

        TypedQuery<Long> query = entityManager.createQuery(queryBuilder.toString(), Long.class);

        if (columnName.equalsIgnoreCase("name") || columnName.equalsIgnoreCase("description")) {
            System.out.println("🔄 String formatına dönüştürülüyor: " + value);
            query.setParameter("value", value.toString().trim());
        } else {
            query.setParameter("value", value);
        }

        if (categoryDetailsId != null) {
            query.setParameter("categoryDetailsId", categoryDetailsId);
        }

        return query.getResultList();
    }
}




