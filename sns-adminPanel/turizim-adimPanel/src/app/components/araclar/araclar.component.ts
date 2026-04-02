import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ProductDto } from '../../models/products-dto';
import { CategoryDto } from '../../models/category-dto';
import { CategoryDetailsDto } from '../../models/category-details-dto';
import { ProductDetailsDto } from '../../models/products-details-dto';
import { CategoryService } from '../../services/category.service';
import { ProductsService } from '../../services/products.service';
import { CategoryDetailsService } from '../../services/category-details.service';
import { ProductDetailsService } from '../../services/product-details.service';
import { MinCategoryDto } from '../../models/minCategory-dto';
import { DynamicQueryService } from '../../services/query.service';

@Component({
  selector: 'app-araclar',
  templateUrl: './araclar.component.html',
  styleUrls: ['./araclar.component.css']
})
export class AraclarComponent implements OnInit {
  @Output() categoriesLoaded = new EventEmitter<MinCategoryDto[]>();

  product: ProductDto = new ProductDto();
  category: CategoryDto = new CategoryDto();
  categories: CategoryDto[] = [];
  categoryDetails: CategoryDetailsDto[] = [];
  minCategory: MinCategoryDto[] = [];
  selectedCategoryId: number | null = null;
  productDetailsMap: { [key: number]: any[] } = {};
  maxDetailsHeaders: string[] = [];
  products: any[] = [];
  productDetails: any[] = [];
  searchInputs: { [key: string]: string } = {}; // Kullanıcı girişlerini tutan obje

  constructor(
    private productsService: ProductsService,
    private categoryService: CategoryService,
    private categoryDetailsService: CategoryDetailsService,
    private productDetailsService: ProductDetailsService,
    private dynamicQueryService: DynamicQueryService
  ) {}

  ngOnInit(): void {}

  loadParentCategories(categoryId: number): void {
    this.selectedCategoryId = categoryId;
    this.categoryService.getAllMinCategories().subscribe(data => {
      this.minCategory = data.filter(category => category.parentId === categoryId);
      console.log(`Kategori ID ${categoryId} için getirilen kategoriler:`, JSON.stringify(this.minCategory, null, 2));
      this.categoriesLoaded.emit(this.minCategory);
    });
  }

  loadCategories(): void {
    this.categoryService.getAllMinCategories().subscribe(data => {
      this.minCategory = data;
      console.log("categories::", JSON.stringify(data, null, 2));
      this.categoriesLoaded.emit(data);
    });
  }

  loadProductAndDetails(categoryId: number): void {
    this.productsService.getProductAndDetails(categoryId).subscribe(data => {
      this.products = data.products;
      this.productDetails = data.productDetails;

      console.log("productAndDetails:::", JSON.stringify(data, null, 2));

      this.productDetailsMap = {};
      for (let detail of this.productDetails) {
        if (!this.productDetailsMap[detail.productId]) {
          this.productDetailsMap[detail.productId] = [];
        }
        this.productDetailsMap[detail.productId].push(detail);
      }

      let maxColumns = 0;
      let headerNames: string[] = [];

      for (let productId in this.productDetailsMap) {
        if (this.productDetailsMap[productId].length > maxColumns) {
          maxColumns = this.productDetailsMap[productId].length;
          headerNames = this.productDetailsMap[productId].map(d => d.name);
        }
      }
      this.maxDetailsHeaders = headerNames;
    });
  }

onEnterSearch(columnKey: string, value: string): void {
    console.log(`Enter tuşu algılandı! columnKey: ${columnKey}, value: ${value}`);

    if (!value) {
        this.loadProductAndDetails(this.selectedCategoryId ?? 1);  // Seçili kategori ID yoksa varsayılan 1 olsun
        return;
    }

    let tableName: string;
    let columnName: string;
    let categoryDetailsId: number | null = null;

    if (columnKey === 'name' || columnKey === 'unitPrice' || columnKey === 'description') {
        tableName = 'Product';
        columnName = columnKey;
    } else {
        tableName = 'ProductDetails';
        columnName = "value";
        categoryDetailsId = parseInt(columnKey, 10);
    }

    console.log(`Sorgu gönderiliyor: Table - ${tableName}, Column - ${columnName}, Value - ${value}, CategoryDetailsId - ${categoryDetailsId}`);

    this.dynamicQueryService.search(tableName, columnName, value, categoryDetailsId).subscribe(
        (data) => {
            console.log("Backend'den gelen sonuç:", data);
            this.products = data.products || [];
            this.productDetailsMap = {};

            if (data.productDetails) {
                for (let detail of data.productDetails) {
                    if (!this.productDetailsMap[detail.productId]) {
                        this.productDetailsMap[detail.productId] = [];
                    }
                    this.productDetailsMap[detail.productId].push(detail);
                }
            }
        },
        (error) => {
            console.error("Sorgu hatası:", error);
        }
    );
}


}
