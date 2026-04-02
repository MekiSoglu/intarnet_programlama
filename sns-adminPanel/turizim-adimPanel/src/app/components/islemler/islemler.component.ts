import { Component, OnInit } from '@angular/core';
import { DynamicTableService } from "../../services/DynamicTableService";
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import {IslemlerService} from "../../services/Islemler.service";

@Component({
  selector: 'app-islemler',
  templateUrl: './islemler.component.html',
  styleUrls: ['./islemler.component.css']
})
export class IslemlerComponent implements OnInit {
  tables: string[] = [];
  selectedTable: string = '';
  tableColumns: { [key: string]: string[] } = {};
  selectedColumns: { table?: string; column?: string; name?: string; operation?: string; dataType?: string }[] = [];

  newInputName: string = '';
  inputFields: { name: string; dataType: string }[] = [];

  operationName: string = ''; // ✅ Kullanıcının girdiği işlem adı


  constructor(private dynamicTableService: DynamicTableService,private islemlerService: IslemlerService) {}

  ngOnInit(): void {
    this.fetchTables();
  }

  /** ✅ Tabloları getir */
  fetchTables(): void {
    this.dynamicTableService.getTables().subscribe({
      next: (tables: string[]) => {
        this.tables = tables;
        this.tables.forEach(table => this.fetchColumns(table));
      },
      error: (err: any) => console.error('🚨 Tablo Listesi Alınırken Hata:', err)
    });
  }

  /** ✅ Seçilen tablonun kolonlarını getir */
  fetchColumns(tableName: string): void {
    this.dynamicTableService.getTableColumns(tableName).subscribe({
      next: (columns: string[]) => {
        this.tableColumns[tableName] = columns;
      },
      error: (err: any) => console.error(`🚨 ${tableName} Kolon Listesi Alınırken Hata:`, err)
    });
  }

  /** ✅ Tabloyu seçme */
  dropTable(event: CdkDragDrop<string[]>) {
    const selectedTable = event.previousContainer.data[event.previousIndex];
    this.selectedTable = selectedTable;
    console.log(`📌 Seçilen Tablo: ${this.selectedTable}`);
  }

  /** ✅ Kolonu işlem alanına bırakma */
  dropColumn(event: CdkDragDrop<string[]>) {
    if (!this.selectedTable) return;
    const movedColumn = event.previousContainer.data[event.previousIndex];

    this.selectedColumns.push({ table: this.selectedTable, column: movedColumn });

    event.previousContainer.data.splice(event.previousIndex, 1);
  }

  /** ✅ Yeni input ekleme */
  addNewInput() {
    if (!this.newInputName.trim()) return;
    this.inputFields.push({ name: this.newInputName, dataType: 'integer' }); // 🔹 Default olarak integer atanıyor
    this.newInputName = '';
  }

  /** ✅ Inputları işlem alanına bırakma */
/** ✅ Inputları işlem alanına bırakma */
dropToSelected(event: CdkDragDrop<any[]>) {
  const movedItem = event.previousContainer.data[event.previousIndex];

  if (movedItem.name) {
    this.selectedColumns.push({ name: movedItem.name, dataType: movedItem.dataType || 'integer' });
  } else if (movedItem.table && movedItem.column) {
    this.selectedColumns.push(movedItem);
  }

  event.previousContainer.data.splice(event.previousIndex, 1);
}


  /** ✅ Inputları işlem alanına bırakma */
/** ✅ Inputları işlem alanına bırakma */
dropInput(event: CdkDragDrop<{ name: string, dataType: string }[]>) {
  const movedInput = event.previousContainer.data[event.previousIndex];

  // ✅ Seçilen input'u işlem alanına ekle
  this.selectedColumns.push({ name: movedInput.name, dataType: movedInput.dataType || 'integer' });

  // ✅ Orijinal input listesinden kaldır
  event.previousContainer.data.splice(event.previousIndex, 1);
}

createOperation() {
  if (!this.operationName.trim()) {
    alert("Lütfen işlem adını girin!");
    return;
  }

  // ✅ Kullanıcının seçtiği tabloları belirle (undefined olanları sil)
  const selectedTables = Array.from(
    new Set(this.selectedColumns.map(col => col.table).filter((table): table is string => Boolean(table)))
  );

  // ✅ Kullanıcının seçtiği kolonları JSON formatına uygun hale getir
  const formattedColumns = this.selectedColumns
    .filter(col => col.table && col.column) // Geçerli kolonları al
    .map(col => ({
      table: col.table,
      column: col.column,
      operation: col.operation || null, // Kullanıcının seçtiği işlem
      islem: col.operation || null      // JSON'da "islem" olarak ekledik
    }));

  // ✅ Kullanıcının eklediği giriş alanlarını JSON formatına uygun hale getir
  const formattedInputs = this.selectedColumns
    .filter(col => col.name && col.dataType) // ✅ Seçili input'ları al
    .map(input => ({
      name: input.name,
      dataType: input.dataType || 'string'  // 🔹 Varsayılan olarak string atanıyor
    }));

  console.log("✅ İşlem Adı:", this.operationName);
  console.log("✅ Seçilen Tablolar:", selectedTables);
  console.log("✅ Seçilen Kolonlar:", formattedColumns);
  console.log("✅ Seçilen Input'lar:", formattedInputs);

  // ✅ Backend'e gönderilecek JSON oluştur
  const payload = {
    name: this.operationName,
    jsonData: {
      tables: selectedTables,
      columns: formattedColumns,
      inputs: formattedInputs
    }
  };

  console.log("📌 Gönderilen JSON:", JSON.stringify(payload, null, 2));

  this.islemlerService.createOperation(payload.name, selectedTables, formattedColumns, formattedInputs)
    .subscribe(response => {
      console.log("✅ İşlem başarıyla oluşturuldu:", response);
      alert(`"${this.operationName}" işlemi başarıyla oluşturuldu!`);
      this.operationName = ''; // Input'u temizle
    }, error => {
      console.error("🚨 İşlem oluşturulurken hata:", error);
      alert("Bir hata oluştu, lütfen tekrar deneyin.");
    });

    this.islemlerService.createProcedure(payload.name, selectedTables, formattedColumns, formattedInputs)
    .subscribe(response => {
      console.log("✅ İşlem başarıyla oluşturuldu:", response);
      alert(`"${this.operationName}" işlemi başarıyla oluşturuldu!`);
      this.operationName = ''; // Input'u temizle
    }, error => {
      console.error("🚨 İşlem oluşturulurken hata:", error);
      alert("Bir hata oluştu, lütfen tekrar deneyin.");
    });
}





}
