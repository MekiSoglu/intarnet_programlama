import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class DynamicJoinService {
    private baseUrl = 'http://localhost:80/api';

    constructor(private http: HttpClient) {}

    /** ✅ Seçilen tablonun ilişkili tablolarını getir */
    getTableRelations(selectedTables: string[]): Observable<any> {
      return this.http.post(`${this.baseUrl}/dynamic-join/relations`, selectedTables);
    }

    /** ✅ Dinamik View oluşturma isteği gönder */
    createView(payload: any): Observable<any> {
      return this.http.post(`${this.baseUrl}/dynamic-join/create-view`, payload);
    }

    getCariler(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/join`);
    }

  fetchDataFromView(viewName: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/dynamic-join/fetch/${viewName}`);
    }


fetchFilteredData(viewName: string, columnName: string, columnValue: any): Observable<any[]> {
  return this.http.get<any[]>(`${this.baseUrl}/dynamic-join/fetch/${viewName}/filter`, {
    params: { columnName, columnValue }
  });
 }

    /** ✅ Belirli bir cariyi sil */
deleteCari(viewName: string): Observable<{ message: string }> {
  return this.http.delete<{ message: string }>(`${this.baseUrl}/dynamic-join/delete/${viewName}`);
}




}
