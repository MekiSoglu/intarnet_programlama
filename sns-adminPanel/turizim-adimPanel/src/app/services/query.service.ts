import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DynamicQueryService {
  private baseUrl = 'http://localhost:80/api/search'; // Backend API URL

  constructor(private http: HttpClient) {}

  search(tableName: string, columnName: string, value: string, categoryDetailsId: number | null): Observable<any> {
  let url = `${this.baseUrl}?tableName=${tableName}&columnName=${columnName}&value=${value}`;

  if (categoryDetailsId) {
    url += `&categoryDetailsId=${categoryDetailsId}`;  // Eğer varsa ekle
  }

  return this.http.get<any>(url);
}

}
