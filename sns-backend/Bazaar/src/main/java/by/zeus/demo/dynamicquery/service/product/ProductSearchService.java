package by.zeus.demo.dynamicquery.service.product;

import by.zeus.demo.base.web.dto.BaseDTO;
import by.zeus.demo.product.domain.ProductEntity;
import by.zeus.demo.product.domain.ProductDetailsEntity;
import by.zeus.demo.product.repository.ProductRepository;
import by.zeus.demo.product.web.dto.ProductDTO;
import by.zeus.demo.product.web.dto.ProductDetailsDTO;
import by.zeus.demo.product.web.mapper.ProductMapper;
import by.zeus.demo.product.web.mapper.ProductDetailsMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {

    private final DynamicProductQueryBuilder dynamicProductQueryBuilder;
    private final ProductMapper productMapper;
    private final ProductDetailsMapper productDetailsMapper;
    private final ProductRepository productRepository;

    public ProductSearchService(DynamicProductQueryBuilder dynamicProductQueryBuilder, ProductMapper productMapper,
                                ProductDetailsMapper productDetailsMapper, ProductRepository productRepository) {
        this.dynamicProductQueryBuilder = dynamicProductQueryBuilder;
        this.productMapper = productMapper;
        this.productDetailsMapper = productDetailsMapper;
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<? extends BaseDTO>> searchAndConvert(String tableName, String columnName, String value, Long categoryDetailsId) {
        try {
            System.out.println("📌 Gelen Parametreler: tableName=" + tableName + ", columnName=" + columnName + ", value=" + value + ", categoryDetailsId=" + categoryDetailsId);

            Map<String, List<? extends BaseDTO>> result = new HashMap<>();
            result.put("products", new ArrayList<ProductDTO>());
            result.put("productDetails", new ArrayList<ProductDetailsDTO>());

            // **Sadece productId değerlerini çekiyoruz**
            List<Long> productIds = dynamicProductQueryBuilder.buildAndExecuteQuery(tableName, columnName, value, categoryDetailsId);
            System.out.println("🆔 Filtrelenmiş Product ID'leri: " + productIds);

            if (productIds.isEmpty()) {
                return result; // Sonuç yoksa boş liste döndür
            }

            // **Product ID'lerine göre ürünleri ve detaylarını çekiyoruz**
            List<Object[]> rawResults = productRepository.findProductsWithDetailsByIds(productIds);
            System.out.println("🔍 Query Sonucu: " + rawResults);

            // **Kategori detay ID'lerini topla**
            List<Long> categoryDetailIds = rawResults.stream()
                                                     .map(row -> ((ProductDetailsEntity) row[1]).getCategoryDetailsId())
                                                     .distinct()
                                                     .collect(Collectors.toList());

            // **Tek seferde tüm kategori detay isimlerini al**
            List<Object[]> categoryDetailNames = productRepository.findDetailsNames(categoryDetailIds);
            Map<Long, String> categoryDetailsMap = categoryDetailNames.stream()
                                                                      .collect(Collectors.toMap(row -> (Long) row[0], row -> (String) row[1]));

            // **Ürünleri tekil olarak saklayacak bir Map kullanıyoruz**
            Map<Long, ProductDTO> productDTOMap = new HashMap<>();

            for (Object[] row : rawResults) {
                ProductEntity product = (ProductEntity) row[0];
                ProductDetailsEntity productDetailsEntity = (ProductDetailsEntity) row[1];

                // **Eğer ürün daha önce eklenmediyse ekleyelim**
                productDTOMap.putIfAbsent(product.getId(), productMapper.toDto(product));

                // **Ürün detaylarını oluştur ve isim ekle**
                ProductDetailsDTO productDetailsDTO = productDetailsMapper.toDto(productDetailsEntity);
                String detailName = categoryDetailsMap.getOrDefault(productDetailsEntity.getCategoryDetailsId(), "Unknown Detail");
                productDetailsDTO.setName(detailName);

                ((List<ProductDetailsDTO>) result.get("productDetails")).add(productDetailsDTO);
            }

            // **Tekil ürünleri listeye ekleyelim**
            result.put("products", new ArrayList<>(productDTOMap.values()));

            System.out.println("✅ Dönüştürülmüş Veri: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("❌ Hata oluştu: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("searchAndConvert() metodunda hata oluştu!", e);
        }
    }

}

