import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ProductDetailsDto} from "../models/products-details-dto";

@Injectable({
  providedIn: 'root'
})
export class ProductDetailsService {
  private baseUrl = 'http://localhost:80/api/prdDetails';

  constructor(private http: HttpClient) { }

  getAllProductDetails(): Observable<ProductDetailsDto[]> {
    return this.http.get<ProductDetailsDto[]>(`${this.baseUrl}`);
  }

  getProductDetails(id: number): Observable<ProductDetailsDto> {
    return this.http.get<ProductDetailsDto>(`${this.baseUrl}/${id}`);
  }

  createProductDetails(productDetails: ProductDetailsDto): Observable<any> {
    return this.http.post(`${this.baseUrl}`, productDetails);
  }

  updateProductDetails(productDetails: ProductDetailsDto): Observable<any> {
    return this.http.put(`${this.baseUrl}`, productDetails);
  }

  deleteProductDetails(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteAllProductDetails(): Observable<any> {
    return this.http.delete(`${this.baseUrl}`);
  }
}
