import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CategoryDetailsDto } from '../models/category-details-dto';

@Injectable({
  providedIn: 'root'
})
export class CategoryDetailsService {
  private baseUrl = 'http://localhost:80/api/catDetails';

  constructor(private http: HttpClient) { }

  getAllCategoryDetails(): Observable<CategoryDetailsDto[]> {
    return this.http.get<CategoryDetailsDto[]>(`${this.baseUrl}/all`);
  }

  getCategoryDetails(id: number): Observable<CategoryDetailsDto> {
    return this.http.get<CategoryDetailsDto>(`${this.baseUrl}/${id}`);
  }

  createCategoryDetails(categoryDetails: CategoryDetailsDto): Observable<CategoryDetailsDto> {
    return this.http.post<CategoryDetailsDto>(`${this.baseUrl}`, categoryDetails);
  }

  updateCategoryDetails(categoryDetails: CategoryDetailsDto): Observable<any> {
    return this.http.put(`${this.baseUrl}`, categoryDetails);
  }

  deleteCategoryDetails(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteAllCategoryDetails(): Observable<any> {
    return this.http.delete(`${this.baseUrl}`);
  }
}
