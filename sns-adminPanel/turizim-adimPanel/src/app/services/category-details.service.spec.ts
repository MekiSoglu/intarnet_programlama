import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CategoryDetailsDto } from '../models/category-details-dto';

@Injectable({
  providedIn: 'root'
})
export class CategoryDetailsService {
  private baseUrl = 'http://localhost:80/api/catDetails';

  constructor(private httpClient: HttpClient) { }

  getCategoryDetails(id: number): Observable<CategoryDetailsDto> {
    const url = `${this.baseUrl}/${id}`;
    return this.httpClient.get<CategoryDetailsDto>(url);
  }

  getAllCategoryDetails(): Observable<CategoryDetailsDto[]> {
    return this.httpClient.get<CategoryDetailsDto[]>(this.baseUrl);
  }

  createCategoryDetails(categoryDetails: CategoryDetailsDto): Observable<void> {
    return this.httpClient.post<void>(this.baseUrl, categoryDetails);
  }

  updateCategoryDetails(categoryDetails: CategoryDetailsDto): Observable<void> {
    return this.httpClient.put<void>(this.baseUrl, categoryDetails);
  }

  deleteCategoryDetails(id: number): Observable<void> {
    const url = `${this.baseUrl}/${id}`;
    return this.httpClient.delete<void>(url);
  }

  deleteAllCategoryDetails(): Observable<void> {
    return this.httpClient.delete<void>(this.baseUrl);
  }
}
