import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class tableNameService {
    private baseUrl = 'http://localhost:80/api/dynamic_table';
    constructor(private http: HttpClient) { }

 getTableNames(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/list-tables`);
  }

  getCreateTableName() : Observable<any>{
      return this.http.get<any>(`${this.baseUrl}/getCrateTableName`)
  }


}
