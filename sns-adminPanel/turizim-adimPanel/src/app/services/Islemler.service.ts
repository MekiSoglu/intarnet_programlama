import {Injectable} from "@angular/core";
import {map, Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class IslemlerService {
  private baseUrl = 'http://localhost:80/api/islemler';

  constructor(private http: HttpClient) {}


     getTables(): Observable<string[]> {
    return this.http.get<{ islemAdi: string }[]>(`${this.baseUrl}`).pipe(
      map(response => response.map(islem => islem.islemAdi)) // ✅ Sadece `islemAdi` değerlerini al
    );
  }

    /** ✅ Seçilen işlem detaylarını çek */
getIslemDetay(islemAdi: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${encodeURIComponent(islemAdi)}`);
  }

   createTable(name: string): Observable<any> {
   return this.http.post(`${this.baseUrl}`, { name });
}

  createOperation(operationName: string, selectedTables: string[], selectedColumns: any[], inputFields: any[]): Observable<any> {
    const payload = {
      name: operationName,
      jsonData: {
        tables: selectedTables,
        columns: selectedColumns.map(col => ({
          table: col.table,
          column: col.column,
          operation: col.operation || null
        })),
        inputs: inputFields.map(input => ({
          name: input.name,
          dataType: input.dataType || null
        }))
      }
    };

    console.log("📌 Gönderilen JSON:", JSON.stringify(payload, null, 2));

    return this.http.post(`${this.baseUrl}`, payload);
  }

  createProcedure(operationName: string, selectedTables: string[], selectedColumns: any[], inputFields: any[]): Observable<any> {
  const payload = {
    name: operationName,
    jsonData: {
      tables: selectedTables,
      columns: selectedColumns.map(col => ({
        table: col.table,
        column: col.column,
        operation: col.operation || null
      })),
      inputs: inputFields.map(input => ({
        name: input.name,
        dataType: input.dataType || null
      }))
    }
  };

  console.log("📌 Gönderilen JSON:", JSON.stringify(payload, null, 2));

  return this.http.post(`${this.baseUrl}/create`, payload);
}


executeProcedure(procedureName: string, payload: {
  procedureName: string;
  selectedIds: { id: any; table: any }[];
  inputValues: { name: string; value: any }[];
}): Observable<any> {
  return this.http.post(`${this.baseUrl}/execute/${encodeURIComponent(procedureName)}`, payload);
}

}
