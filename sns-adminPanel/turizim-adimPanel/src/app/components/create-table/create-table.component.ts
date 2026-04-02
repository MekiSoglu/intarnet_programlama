import { Component } from '@angular/core';
import { tableNameService } from '../../services/table-name.service';
import { DynamicTableService } from '../../services/DynamicTableService';

@Component({
  selector: 'app-create-table',
  templateUrl: './create-table.component.html',
  styleUrls: ['./create-table.component.css']
})
export class CreateTableComponent {
  tableName: string = '';
  columns: { key: string; type: string }[] = [];
  foreignKeys: { column: string; references: string; relation: string; relationColumn: string }[] = [];
  tableNames: string[] = [];
  foreignTableColumns: string[] = [];

  columnName: string = '';
  columnType: string = 'varchar';

  selectedForeignTable: string = '';
  selectedForeignColumn: string = '';
  selectedRelation: string = 'one-to-many';

  showTablePreview: boolean = false;
  previewData: any[] = [];

  enableAlarm: boolean = false;

  selectedTableToDelete: string = ''; // Silinecek tablo


  createdTableDetails: any = null; // **✅ Bu değişkeni tanımladık**

  constructor(private dynamicTableService: DynamicTableService, private tableNameService: tableNameService) {
    this.loadTables();
  }

  /** ✅ Backend'den tüm tablo isimlerini getirir */
  loadTables() {
    this.tableNameService.getTableNames().subscribe(
      (data: string[]) => {
        this.tableNames = data;
      },
      (error: any) => {
        console.error("❌ Tablo isimleri yüklenirken hata oluştu:", error);
      }
    );
  }

  /** ✅ Seçilen tablonun kolonlarını getirir */
  loadForeignTableColumns() {
    if (this.selectedForeignTable) {
      this.dynamicTableService.getTableColumns(this.selectedForeignTable).subscribe(
        (columns: string[]) => {
          this.foreignTableColumns = columns;
        },
        (error: any) => {
          console.error("❌ Kolonlar yüklenirken hata oluştu:", error);
        }
      );
    }
  }

  /** ✅ Kullanıcının eklemek istediği sütunu tabloya ekler */
  addColumn() {
    if (this.columnName.trim()) {
      this.columns.push({
        key: this.columnName.replace(/\s/g, '_').toLowerCase(),
        type: this.columnType
      });
      this.columnName = '';
    }
  }

  /** ✅ Kullanıcının eklediği sütunu kaldırır */
  removeColumn(index: number) {
    this.columns.splice(index, 1);
  }

  /** ✅ Kullanıcı seçili tablo ve kolon ile foreign key bağlantısı ekler */
  addForeignKey() {
    if (this.selectedForeignTable.trim() && this.selectedForeignColumn.trim()) {
      this.foreignKeys.push({
        column: `${this.selectedForeignTable}_id`,
        references: this.selectedForeignTable,
        relation: this.selectedRelation,
        relationColumn: this.selectedForeignColumn
      });

      // Seçimleri sıfırla
      this.selectedForeignTable = '';
      this.selectedForeignColumn = '';
      this.foreignTableColumns = [];
    }
  }

  /** ✅ Foreign key bağlantısını kaldırır */
  removeForeignKey(index: number) {
    this.foreignKeys.splice(index, 1);
  }

  /** ✅ Alarm özelliğini açıp kapatır ve ilgili sütunları ekler/siler */
  toggleAlarm() {
    if (this.enableAlarm) {
      if (!this.columns.find(col => col.key === 'start_date')) {
        this.columns.push({ key: 'start_date', type: 'date' });
      }
      if (!this.columns.find(col => col.key === 'end_date')) {
        this.columns.push({ key: 'end_date', type: 'date' });
      }
    } else {
      this.columns = this.columns.filter(col => col.key !== 'start_date' && col.key !== 'end_date');
    }
  }

  /** ✅ Backend'e tablo oluşturma isteğini gönderir */
createTable() {
  const payload = {
    tableName: this.tableName.replace(/\s/g, '_').toLowerCase(),
    columns: this.columns,
    foreignKeys: this.foreignKeys,
    enableAlarm: this.enableAlarm
  };

  this.dynamicTableService.createTable(payload).subscribe(
    (response: any) => {
      if (response.success) {
        alert(`✅ Tablo oluşturuldu: ${response.tableName}`);

        // ✅ Oluşturulan tablonun detaylarını ekrana yazdır
        console.log("📌 Yeni Tablo Bilgileri:", response);
        this.createdTableDetails = response; // Veriyi UI'de göstermek için sakla

        this.showTablePreview = true;
        this.generatePreviewData();
        this.loadTables(); // ✅ Tabloları yeniden yükle
      } else {
        alert(`❌ Hata: ${response.message}`);
      }
    },
    (error) => {
      alert(`❌ Hata: ${error.error.message}`);
    }
  );
}


  /** ✅ Önizleme için boş satırlar oluşturur */
  generatePreviewData() {
    this.previewData = Array(3).fill(0).map(() => {
      let row: any = {};
      this.columns.forEach(col => row[col.key] = '');
      return row;
    });
  }

  /** ✅ Kullanıcının önizlemede yeni satır eklemesini sağlar */
  addNewRow() {
    let row: any = {};
    this.columns.forEach(col => row[col.key] = '');
    this.previewData.push(row);
  }

deleteTable() {
  if (!this.selectedTableToDelete) return;

  if (!confirm(`"${this.selectedTableToDelete}" tablosunu silmek istediğinize emin misiniz?`)) return;

  this.dynamicTableService.deleteTable(this.selectedTableToDelete).subscribe(
    response => {
      alert(`✅ Tablo silindi: ${this.selectedTableToDelete}`);
      this.loadTables(); // ✅ Güncellenmiş tablo listesini tekrar yükle
    },
    error => {
      console.error("❌ Hata oluştu:", error);

      // Hata mesajını JSON nesnesinden al
      const errorMessage = error.error?.message || error.error?.error || "Bilinmeyen bir hata oluştu!";
      alert(`❌ Hata: ${errorMessage}`);
    }
  );
}

}
