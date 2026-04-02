import { Component, OnInit } from '@angular/core';
import { CategoryDto } from "../../models/category-dto";
import { CategoryService } from "../../services/category.service";
import { CategoryDetailsDto } from '../../models/category-details-dto';
import { CategoryDetailsService } from '../../services/category-details.service';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {

  categories: CategoryDto[] = [];
  categoryDetailsList: CategoryDetailsDto[] = [];
  selectedCategory: CategoryDto = new CategoryDto();
  selectedCategoryDetails: CategoryDetailsDto = new CategoryDetailsDto();
  categoryDetailsIds: number[] = [];
  selectedCategoryId: number | null = null;
  selectedCategoryDetailIds: number[] = [];

  constructor(
    private categoryService: CategoryService,
    private categoryDetailsService: CategoryDetailsService
  ) { }

  ngOnInit(): void {
    this.getAllCategories();
    this.getAllCategoryDetails();
  }

  ngAfterViewInit(): void {
  console.log("ngAfterViewInit - Category Details List:", this.categoryDetailsList);
}

  getAllCategories(): void {
    this.categoryService.getAllCategories().subscribe(
      data => this.categories = data
    );
  }

 getAllCategoryDetails(): void {
  this.categoryDetailsService.getAllCategoryDetails().subscribe(data => {
    this.categoryDetailsList = data;
    console.log("Kategori detayları yüklendi:", this.categoryDetailsList);
  });
}

  addCategoryDetail(id: number | null): void {
    if (id !== null && !this.selectedCategoryDetailIds.includes(id)) {
      this.selectedCategoryDetailIds.push(id);
    }
  }

getDetailNameById(id: number): string {
  console.log("details.id:", id);

  const detail = this.categoryDetailsList.find(detail => Number(detail.id) === Number(id));


  return detail ? detail.name : 'Unknown Detail';
}

saveCategory(): void {
  console.log("parent_id", this.selectedCategoryId);
console.log("Seçili Category Detail Id'leri:", this.selectedCategoryDetailIds);

const categoryDetailsList = this.selectedCategoryDetailIds.map(id => {
    return { id: Number(id) } as CategoryDetailsDto;
  });

  console.log("categorydetaisl:"+categoryDetailsList)

  const categoryData = {
    parentId: this.selectedCategoryId,
    categoryName: this.selectedCategory.categoryName,
    categoryDetailsList: categoryDetailsList  // Backend'in beklediği nesne listesi
  };

  this.categoryService.createCategory(categoryData).subscribe(
    () => {
      this.getAllCategories();
      this.selectedCategory = new CategoryDto(); // Alanı sıfırla
      this.selectedCategoryDetailIds = []; // Listeyi sıfırla
      this.selectedCategoryId = null;
    }
  );
}

  removeCategoryDetail(id: number) {
    
  }
}
