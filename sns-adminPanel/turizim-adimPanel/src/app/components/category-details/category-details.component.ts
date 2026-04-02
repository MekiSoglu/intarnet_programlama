import { Component, OnInit } from '@angular/core';
import { CategoryDetailsDto } from "../../models/category-details-dto";
import { CategoryDetailsService } from "../../services/category-details.service";

@Component({
  selector: 'app-category-details',
  templateUrl: './category-details.component.html',
  styleUrls: ['./category-details.component.css']
})
export class CategoryDetailsComponent implements OnInit {

  categoryDetailsList: CategoryDetailsDto[] = [];
  newCategoryDetail: CategoryDetailsDto = new CategoryDetailsDto();

  constructor(private categoryDetailsService: CategoryDetailsService) { }

  ngOnInit(): void {
    this.getAllCategoryDetails();
  }

  getAllCategoryDetails(): void {
    this.categoryDetailsService.getAllCategoryDetails().subscribe(
      data => this.categoryDetailsList = data
    );
  }

  createCategoryDetails(): void {
    this.categoryDetailsService.createCategoryDetails(this.newCategoryDetail).subscribe(
      () => {
        this.getAllCategoryDetails();
        this.newCategoryDetail = new CategoryDetailsDto(); // Alanı sıfırlayın
      }
    );
  }

  removeDetail(id: number | null) {
    
  }
}
