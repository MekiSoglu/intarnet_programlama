import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DynamicTableCrud } from '../../services/DynamicTableCrud';

interface ForeignKey {
  column: string; // ✅ Foreign Key alanı
  relatedTable: string; // ✅ Bağlı tablonun ismi
  relationColumn: string; // ✅ Kullanıcıya gösterilecek kolon (ör: name)
  relationType: string; // ✅ Many-to-One veya Many-to-Many ayrımı
}

@Component({
  selector: 'app-tablolarim',
  templateUrl: './tablolarim.component.html',
  styleUrls: ['./tablolarim.component.css']
})
export class TablolarimComponent implements OnInit {
  selectedTable: string = '';
  tableData: any[] = [];
  tableColumns: string[] = [];
  newRow: any = {};
  foreignKeys: ForeignKey[] = [];
  foreignKeyOptions: { [key: string]: any[] } = {}; // ✅ FK seçenekleri (Many-to-One)
  manyToManySelections: { [key: string]: any[] } = {}; // ✅ Many-to-Many seçimleri

  constructor(
    private route: ActivatedRoute,
    private dynamicTableService: DynamicTableCrud
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const tableName = params.get('tableName');
      if (tableName) {
        this.loadTableData(tableName);
      }
    });
  }

  /** ✅ Tablo Verilerini Yükle */
  loadTableData(tableName: string) {
    this.selectedTable = tableName;

    this.dynamicTableService.getTableData(tableName).subscribe(data => {
      console.log("🔍 Backend'den gelen veri:", data);

      if (data.length > 0) {
        if (typeof data[0] === 'object' && !Array.isArray(data[0])) {
          this.tableColumns = Object.keys(data[0]);
        }
        this.tableData = data;
      } else {
        // @ts-ignore
        this.dynamicTableService.getTableColumns(tableName).subscribe((columns: string[]) => {
          this.tableColumns = columns;
        });
      }

      // **Many-to-Many ve One-to-Many İlişkileri Çek ve UI'ye Ekle**
      this.dynamicTableService.getForeignKeys(tableName).subscribe(foreignKeys => {
        console.log("🔹 Gelen Foreign Key Verileri:", foreignKeys);

        this.foreignKeys = foreignKeys.map(fk => ({
          column: `${fk.relatedTable}_id`, // ✅ FK alanı **relatedTable_id olarak düzeltildi**
          relatedTable: fk.relatedTable,
          relationColumn: fk.relationColumn,
          relationType: (fk as any).relationType || "many-to-one"
        }));

        this.foreignKeys.forEach(fk => {
          if (fk.relationType === "many-to-many" && !this.tableColumns.includes(`${fk.relatedTable}_id`)) {

            this.tableColumns.push(`${fk.relatedTable}_id`); // ✅ Many-to-Many için "relatedTable_id" olarak eklendi
            this.loadManyToManyData(fk.relatedTable);
                        this.loadForeignKeyData(fk.relatedTable, fk.relationColumn);

          } else {
            this.loadForeignKeyData(fk.relatedTable, fk.relationColumn);
          }
        });
      });

      this.newRow = this.createEmptyRow();
    });
  }

  /** ✅ FK Verilerini Yükle (Many-to-One) */
  loadForeignKeyData(relatedTable: string | null, relationColumn: string | null) {
    if (!relatedTable || !relationColumn) return;
    this.dynamicTableService.getForeignKeyData(relatedTable, relationColumn)
      .subscribe((data) => {
        this.foreignKeyOptions[`${relatedTable}_id`] = data;
      });
  }

  /** ✅ Many-to-Many Seçimlerini Yükle */
  loadManyToManyData(relatedTable: string) {
    this.dynamicTableService.getTableData(relatedTable).subscribe(data => {
      this.manyToManySelections[`${relatedTable}_id`] = data; // ✅ "relatedTable_id" olarak saklandı
    });
  }

  /** ✅ Boş Bir Satır Oluştur */
  createEmptyRow() {
    let row: any = {};
    this.tableColumns.forEach(col => {
      if (col !== 'created_date') {
        row[col] = '';
      }
    });
    return row;
  }

  /** ✅ Kolonun Boolean (True/False) Olup Olmadığını Kontrol Et */
isBooleanColumn(column: string): boolean {
  return column.toLowerCase().includes('status')
      || column.toLowerCase().includes('is_')
      || column.toLowerCase().includes('active')
      || column.toLowerCase().includes('enabled');
}


  /** ✅ Yeni Veri Ekle */
/** ✅ Yeni Veri Ekle */
addNewRow() {
  console.log("📝 Yeni kayıt oluşturuluyor:", this.newRow);
  console.log("🔗 Many-to-Many Seçimleri (İşlenmeden Önce):", this.manyToManySelections);

  // 🛠 **Many-to-Many seçimlerini temizle ve sadece son seçimi gönder**
  let cleanedNewRow = { ...this.newRow }; // Veriyi değiştirmeden kopyala

this.foreignKeys.forEach(fk => {
  if (fk.relationType === "many-to-many") {
    // **Many-to-Many için seçilen değeri `this.newRow` üzerinden al**
    let selectedId = this.newRow[`${fk.relatedTable}_id`];

    // Eğer seçili bir ID varsa, bunu gönder
    cleanedNewRow[`${fk.relatedTable}_id`] = selectedId ? selectedId : null;
  }

  // ✅ `relatedTable` ismini backend'e göndermemek için sil
  delete cleanedNewRow[fk.relatedTable];
});


  console.log("📦 Backend'e Gidecek Son Veri:", cleanedNewRow);

  // **Backend'e temizlenmiş veriyi gönder**
  this.dynamicTableService.insertIntoTable(this.selectedTable, cleanedNewRow)
    .subscribe((response: any) => {
      console.log("✅ Yeni Kayıt Eklendi -> ID:", response?.id);

      // **Many-to-Many ilişkileri kaydet**
      if (response && response.id) {
        console.log("📢 saveManyToManyRelations() çağrılacak...");
        this.saveManyToManyRelations(response.id);
      }

      // **Tabloyu güncelle**
      this.loadTableData(this.selectedTable);
    }, error => {
      console.error("❌ Backend'e Veri Gönderilirken Hata:", error);
    });
}





  /** ✅ Many-to-Many Seçimleri Ara Tabloya Kaydet */
/** ✅ Many-to-Many Seçimleri Ara Tabloya Kaydet */
saveManyToManyRelations(recordId: number) {
  console.log("🔗 Many-to-Many İlişkileri Kaydetme Başladı...");

  this.foreignKeys.forEach(fk => {
    if (fk.relationType === "many-to-many") {
      let selectedValues = this.manyToManySelections[`${fk.relatedTable}_id`] || [];

      // **Sadece son seçilen değeri al**
      if (selectedValues.length > 1) {
        selectedValues = [selectedValues[selectedValues.length - 1]];
      }

      console.log(`🔎 Many-to-Many (${fk.relatedTable}) için işlenen değer:`, selectedValues);

      if (selectedValues.length > 0) {
        const joinTable = `${this.selectedTable}_${fk.relatedTable}`;
        console.log(`📝 Many-to-Many İçin İstek Gönderiliyor: ${joinTable}, ${this.selectedTable}_id = ${recordId}, Değer:`, selectedValues);

        this.dynamicTableService.insertManyToManyRelation(joinTable, this.selectedTable, fk.relatedTable, recordId, selectedValues)
          .subscribe(() => console.log(`✅ Many-to-Many İlişkisi Kaydedildi: ${joinTable}`),
          error => console.error("❌ Many-to-Many Ekleme Hatası:", error));
      } else {
        console.log("⚠️ Many-to-Many için Seçili Veri Yok!");
      }
    }
  });
}







  /** ✅ Enter Tuşuna Basıldığında Sorgulama */
  handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.searchTableData();
    }
  }

  /** ✅ Dinamik Sorgulama Yap */
  searchTableData() {
    const queryColumn = Object.keys(this.newRow).find(col => this.newRow[col]);

    if (!queryColumn) {
      console.log("🔄 Hiçbir filtre girilmedi, tüm veriler getiriliyor...");
      this.loadTableData(this.selectedTable);
      return;
    }

    const queryValue = this.newRow[queryColumn];

    this.dynamicTableService.searchTableData(this.selectedTable, queryColumn, queryValue).subscribe(data => {
      console.log("🔍 Backend'den gelen sorgu sonucu:", data);
      this.tableData = data;
    }, error => {
      console.error("❌ Sorgulama hatası:", error);
      alert("Sorgulama sırasında bir hata oluştu!");
    });
  }

  /** ✅ Enter Tuşuna Basıldığında Güncelleme Yap */
  updateRow(id: number, column: string, event: Event) {
    const target = event.target as HTMLInputElement;
    if (!target) return;

    if (event instanceof KeyboardEvent && event.key !== 'Enter') {
      return;
    }

    const updatedValue = target.value;

    this.dynamicTableService.updateTable(this.selectedTable, id, { [column]: updatedValue }).subscribe(() => {
      console.log(`✅ Güncellendi -> ID: ${id}, Column: ${column}, Yeni Değer: ${updatedValue}`);
    }, error => {
      console.error("❌ Güncelleme hatası:", error);
      alert("Güncelleme sırasında bir hata oluştu!");
    });
  }

  /** ✅ Veriyi Sil */
  deleteRow(id: number) {
    this.dynamicTableService.deleteFromTable(this.selectedTable, id).subscribe(() => {
      this.loadTableData(this.selectedTable);
    });
  }

  /** ✅ FK Kolonu mu? */
  isForeignKey(column: string): boolean {
    return this.foreignKeys.some(fk => fk.column === column);
  }

  getInputTypeForNewRow(col: string) {

  }
}
