import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DynamicTableService {
  private baseUrl = 'http://localhost:80/api/dynamic-tables';

  constructor(private http: HttpClient) {}

  /** ✅ Dinamik tablo oluşturma */
  createTable(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/create-table`, payload);
  }



  /** ✅ Mevcut tabloları listeleme */
  getTables(): Observable<any> {
    return this.http.get(`${this.baseUrl}/list-tables`);
  }

 /** ✅ Seçilen tablonun kolonlarını getirir */
  getTableColumns(selectedForeignTable: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/columns/${selectedForeignTable}`);
  }

  getForeignKeyData(tableName: string, relationColumn: string): Observable<any[]> {
  return this.http.get<any[]>(`${this.baseUrl}/foreign-key-data/${tableName}/${relationColumn}`);
}

deleteTable(selectedTableToDelete: string) {
  return this.http.delete(`${this.baseUrl}/drop-table`, { params: { tableName: selectedTableToDelete } });
}

}
