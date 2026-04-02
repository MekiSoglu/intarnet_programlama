import { Component, OnInit } from '@angular/core';
import { DynamicTableService } from "../../services/DynamicTableService";
import { DynamicJoinService } from "../../services/DynamicJoin.service";

interface RelatedTable {
  table_name: string;
  related_table: string;
  relation_type: string;
}

@Component({
  selector: 'app-cari',
  templateUrl: './cari.component.html',
  styleUrls: ['./cari.component.css']
})
export class CariComponent implements OnInit {
  tables: string[] = [];
  selectedTables: string[] = [];
  relatedTables: RelatedTable[] = [];
  dropdownOpen: boolean = false;
  cariName: string = '';
  cariler: string[] = [];
  showCariler: boolean = false; // ✅ Cariler listesini aç/kapat

  constructor(private dynamicTableService: DynamicTableService, private dynamicJoinService: DynamicJoinService) {}

  ngOnInit(): void {
    this.loadTables();
    this.loadCariler();
  }

  loadTables() {
    this.dynamicTableService.getTables().subscribe((data: string[]) => {
      this.tables = data;
    }, error => {
      console.error('Tabloları yüklerken hata oluştu:', error);
    });
  }

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  selectTable(tableName: string) {
    if (!this.selectedTables.includes(tableName)) {
      this.selectedTables.push(tableName);
    }

    this.dynamicJoinService.getTableRelations(this.selectedTables).subscribe((data: RelatedTable[]) => {
      this.relatedTables = data;
    }, error => {
      console.error('İlişkili tabloları yüklerken hata oluştu:', error);
    });

    this.dropdownOpen = false;
  }

  createCari() {
    if (!this.cariName.trim() || this.selectedTables.length === 0) {
      alert("Lütfen cari adı girin ve en az bir tablo seçin.");
      return;
    }

    const requestPayload = {
      tableNames: this.selectedTables,
      relations: this.relatedTables,
      viewName: this.cariName
    };

    this.dynamicJoinService.createView(requestPayload).subscribe(() => {
      alert(`Cari "${this.cariName}" başarıyla oluşturuldu!`);
      this.loadCariler();
      this.cariName = '';
      this.selectedTables = [];
      this.relatedTables = [];
    }, error => {
      alert("Cari oluşturulurken hata oluştu.");
    });
  }

  toggleCariler() {
    this.showCariler = !this.showCariler;
  }

  loadCariler() {
    this.dynamicJoinService.getCariler().subscribe((data: string[]) => {
      this.cariler = data;
    }, error => {
      console.error('Carileri yüklerken hata oluştu:', error);
    });
  }

deleteCari(viewName: string) {
  if (!confirm(`"${viewName}" carisini silmek istediğinize emin misiniz?`)) return;

  this.dynamicJoinService.deleteCari(viewName).subscribe(response => {
    console.log("✅ Silme Başarılı:", response);

    // ✅ Backend artık JSON döndürüyor, bu yüzden response.message kullanılmalı
    alert(response.message);

    this.loadCariler(); // ✅ Güncellenmiş carileri tekrar yükle
  }, error => {
    console.error('❌ Cari silinirken hata oluştu:', error);

    // ✅ Hata mesajını JSON formatında göster
    alert(error.error?.error || "Silme işlemi başarısız oldu!");
  });
}



}
