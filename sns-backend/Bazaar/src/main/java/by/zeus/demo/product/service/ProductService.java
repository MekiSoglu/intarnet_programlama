package by.zeus.demo.product.service;
import by.zeus.demo.base.domain.BaseEntity;
import by.zeus.demo.base.web.dto.BaseDTO;
import by.zeus.demo.base.web.mapper.BaseMapper;
import by.zeus.demo.category.domain.CategoryDetailsEntity;
import by.zeus.demo.category.domain.CategoryEntity;
import by.zeus.demo.product.domain.ProductDetailsEntity;
import by.zeus.demo.product.domain.ProductEntity;
import by.zeus.demo.product.web.dto.ProductDTO;
import by.zeus.demo.product.web.dto.ProductDetailsDTO;
import by.zeus.demo.product.web.mapper.ProductMapper;
import by.zeus.demo.category.service.CategoryService;
import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.product.repository.ProductRepository;
import by.zeus.demo.base.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService extends BaseService<ProductEntity> {

    private final CategoryService   categoryService;
    private final ProductRepository     productRepository;
    private final ProductDetailsService productDetailsService;
    private final ProductMapper productMapper;


    public ProductService(BaseRepository<ProductEntity> repository, CategoryService categoryService, ProductRepository productRepository, ProductDetailsService productDetailsService,
                          final ProductMapper productMapper) {
        super(repository);
        this.categoryService = categoryService;
        this.productRepository = productRepository;
        this.productDetailsService = productDetailsService;
        this.productMapper = productMapper;
    }

    public Page<ProductEntity> pageable(List<ProductEntity> productEntities, Pageable pageable) {
        int page=pageable.getPageNumber();
        int size=pageable.getPageSize();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, productEntities.size());
        List<ProductEntity> pageContent = productEntities.subList(startIndex, endIndex);
        long totalElements = productEntities.size();
        return new PageImpl<>(pageContent, PageRequest.of(page - 1, size), totalElements);
    }


    public Page<ProductEntity> findByCategoryId(Long id, Pageable pageable) {
        List<ProductEntity> productEntities =productRepository.findByCategoryId(id);
        return  pageable(productEntities,pageable);
    }

    public Page<ProductEntity> findByName(String name, Pageable pageable) {
        List<ProductEntity> productEntities =productRepository.findByNameContaining(name);
        return  pageable(productEntities,pageable);
    }


    public Map<String, List<? extends BaseDTO>> getProductAndDetails(long categoryId) {
        Optional<CategoryEntity> category = categoryService.findOne(categoryId);
        Long parentId =null;

        if(categoryId != -1){
             parentId = category.get().getParentId();

        }

        if (categoryId == -1) {
            List<Object[]> rawResult = productRepository.findProductsWithDetailsBy();
            return convert(rawResult);
        } else if (parentId == null) {
            List<Long> parents = productRepository.findBy(categoryId);
            List<Object[]> allRawResults = new ArrayList<>();

            for (Long parentIdResult : parents) {
                allRawResults.addAll(productRepository.findProductsWithDetailsByCategoryId(parentIdResult));
            }

            return convert(allRawResults);
        } else {
            List<Object[]> rawResults = productRepository.findProductsWithDetailsByCategoryId(categoryId);
            return convert(rawResults);
        }
    }

    public Map<String, List<? extends BaseDTO>> convert(List<Object[]> rawResults) {
        Map<String, List<? extends BaseDTO>> result = new HashMap<>();
        Map<Long, ProductDTO> productDTOMap = new HashMap<>();
        Map<Long, List<ProductDetailsDTO>> productDetailsDTOMap = new HashMap<>();

        List<Long> categoryDetailIds = new ArrayList<>();

        // İlk tur: ID'leri topla
        for (Object[] row : rawResults) {
            ProductDetailsEntity productDetailsEntity = (ProductDetailsEntity) row[1];
            categoryDetailIds.add(productDetailsEntity.getCategoryDetailsId());
        }

        // Tek seferde tüm kategori detay isimlerini çek
        List<Object[]> categoryDetailNames = productRepository.findDetailsNames(categoryDetailIds);
        Map<Long, String> categoryDetailsMap = categoryDetailNames.stream()
                                                                  .collect(Collectors.toMap(row -> (Long) row[0], row -> (String) row[1]));

        // İkinci tur: DTO'ları oluştur ve detay isimlerini ekle
        for (Object[] row : rawResults) {
            ProductEntity product = (ProductEntity) row[0];
            ProductDetailsEntity productDetailsEntity = (ProductDetailsEntity) row[1];

            ProductDTO productDTO = productMapper.toDto(product);
            ProductDetailsDTO productDetailsDTO = productDetailsService.getProductDetailsDto(productDetailsEntity);

            // İlgili detay ismini bul ve DTO'ya ekle
            String detailName = categoryDetailsMap.get(productDetailsEntity.getCategoryDetailsId());
            productDetailsDTO.setName(detailName); // DTO'da detailName alanı olduğunu varsayıyoruz

            productDTOMap.putIfAbsent(product.getId(), productDTO);
            productDetailsDTOMap.putIfAbsent(product.getId(), new ArrayList<>());
            productDetailsDTOMap.get(product.getId()).add(productDetailsDTO);
        }

        result.put("products", new ArrayList<>(productDTOMap.values()));
        result.put("productDetails", productDetailsDTOMap.values().stream()
                                                         .flatMap(List::stream)
                                                         .collect(Collectors.toList()));

        return result;
    }


}
