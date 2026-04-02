import { Component, OnInit } from '@angular/core';
import { CategoryDto } from '../../models/category-dto';
import { ProductsService } from '../../services/products.service';
import { CategoryService } from '../../services/category.service';
import { CategoryDetailsService } from '../../services/category-details.service';
import { ProductDetailsService } from '../../services/product-details.service';
import { CategoryDetailsDto } from '../../models/category-details-dto';
import { ProductDto } from '../../models/products-dto';
import {ProductDetailsDto} from "../../models/products-details-dto";
import {catchError, of} from "rxjs";

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  product: ProductDto = new ProductDto();
  categories: CategoryDto[] = [];
  products: ProductDto[] = [];
  categoryDetails: CategoryDetailsDto[] = [];
  productDetails: ProductDetailsDto[] = [];

  constructor(
    private productsService: ProductsService,
    private categoryService: CategoryService,
    private categoryDetailsService: CategoryDetailsService,
    private productDetailsService: ProductDetailsService
  ) { }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe(data => {
      this.categories = data;
    });
  }

loadProducts(): void {
  this.productsService.getAllProducts().pipe(
    catchError((error) => {
      console.error('Error in getAllProducts:', error);
      return of([]); // Hata durumunda boş bir dizi döndür
    })
  ).subscribe((data) => {
    this.products = data;
  });
}


  onProductChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const productId = Number(target.value);
    if (productId) {
      this.productsService.getProduct(productId).subscribe(product => {
        this.product = product;
        if (product.categoryId) {
          console.log(product.categoryId)
        //  this.loadCategoryDetails(product.category_id);
        }
      });
    }
  }

loadCategoryDetails(categoryId: number): void {
  this.categoryService.getDetails(categoryId).pipe(
    catchError((error) => {
      console.error('Error in loadCategoryDetails:', error);
      return of([]);
    })
  ).subscribe(data => {
    console.log('Category Details:', data);
    this.categoryDetails = data;
    this.productDetails = this.categoryDetails.map(detail => {
      const pd = new ProductDetailsDto();
      pd.categoryDetailsId = detail.id || 0; // Null kontrolü
      pd.productId = this.product.id || 0; // Undefined kontrolü
      return pd;
    });
  });
}
  saveProduct(): void {
  this.productsService.createProduct(this.product).subscribe(response => {
    console.log('Product saved successfully!', response);

    // Backend'den dönen yanıt içinde `id` varsa `product.id`'ye atayalım
    if (response && response.id) {
      this.product.id = response.id;
    }

    // Kategori id boş değilse, ilgili detayları yükleyelim
    if (this.product.categoryId !== undefined) {
      this.loadCategoryDetails(this.product.categoryId);
    }
  });
}

 saveProductDetails(): void {
  if (!this.product.id) {
    console.error("Hata: Ürün kaydedilmeden detaylar eklenemez!");
    return;
  }

  this.productDetails.forEach(detail => {
    detail.productId = this.product.id; // Yeni eklenen ürünün ID'sini burada kullan

    this.productDetailsService.createProductDetails(detail).subscribe(response => {
      console.log('Product detail saved successfully!', response);
    });
  });
}
}
