import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DynamicTableCrud {
  private baseUrl = 'http://localhost:80/api/create_dynamic-table';

  constructor(private http: HttpClient) {}

  /** ✅ Tablo Verilerini Getir */
  getTableData(tableName: string): Observable<any[]> {
    console.log(this.baseUrl);
    return this.http.get<any[]>(`${this.baseUrl}/${tableName}`);
  }

  /** ✅ Yeni Veri Ekle */
  insertIntoTable(tableName: string, data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/${tableName}`, data);
  }

  /** ✅ Var Olan Veriyi Güncelle */
  updateTable(tableName: string, id: number, data: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${tableName}/${id}`, data);
  }

  /** ✅ Veriyi Sil */
  deleteFromTable(tableName: string, id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${tableName}/${id}`);
  }

  /** ✅ Tablo Kolonlarını Getir */
 getTableColumns(tableName: string): Observable<{ column: string, type: string }[]> {
   console.log(this.baseUrl+"get colum");
  return this.http.get<{ column: string, type: string }[]>(`${this.baseUrl}/columns/${tableName}`);
}


  /** ✅ FK Bağlantılarını Getir */
 /** ✅ Foreign Key Bağlantılarını Getir */
getForeignKeys(tableName: string): Observable<{ relatedTable: string, relationColumn: string }[]> {
  return this.http.get<{ relatedTable: string, relationColumn: string }[]>(`${this.baseUrl}/foreign-keys/${tableName}`);
}


  /** ✅ Foreign Key Bağlantısına Göre Veri Getir */
  getForeignKeyData(relatedTable: string, relationColumn: string): Observable<{ id: number, value: string }[]> {
    return this.http.get<{ id: number, value: string }[]>(`${this.baseUrl}/foreign-key-data/${relatedTable}/${relationColumn}`);
  }

searchTableData(tableName: string, columnName: string, value: any): Observable<any[]> {
  return this.http.get<any[]>(`${this.baseUrl}`, {
    params: {
      tableName: tableName,
      columnName: columnName,
      value: value
    }
  });
}


insertManyToManyRelation(joinTable: string, table1: string, table2: string, recordId: number, relatedIds: number[]): Observable<any> {
  return this.http.post(`${this.baseUrl}/${joinTable}/${table1}/${table2}/${recordId}/many-to-many`, relatedIds);
}

}
