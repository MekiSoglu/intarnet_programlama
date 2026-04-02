import { Component, OnInit } from '@angular/core';
import { IslemlerService } from '../../services/Islemler.service';
import { DynamicTableCrud } from '../../services/DynamicTableCrud';
import { ActivatedRoute } from '@angular/router';
import {formatDate} from "@angular/common";

@Component({
  selector: 'app-islemlerim',
  templateUrl: './islemlerim.component.html',
  styleUrls: ['./islemlerim.component.css']
})
export class IslemlerimComponent implements OnInit {
  islemDetay: any = null;
  selectedTable: string | null = null;
  tableData: { [key: string]: any[] } = {};
  inputValues: { [key: string]: any } = {};
  tableHeaders: { [key: string]: string[] } = {};
  selectedRowsByTable: { [key: string]: any[] } = {}; // ✅ Tablolara göre ayrı seçim listesi

  constructor(
    private islemlerService: IslemlerService,
    private dynamicTableCrud: DynamicTableCrud,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const islemAdi = params['islemAdi'];
      if (islemAdi) {
        this.getIslemDetay(islemAdi);
      }
    });
  }

  formatDate(value: any): string {
  if (!value) return ''; // Eğer boşsa hata almamak için
  if (typeof value === 'string' && value.includes('T')) {
    return formatDate(new Date(value), 'dd/MM/yyyy', 'en-US');
  }
  return formatDate(value, 'dd/MM/yyyy', 'en-US');
}

  getInputType(dataType?: string): string {
    if (!dataType) return 'text';
    switch (dataType.toLowerCase()) {
      case 'integer': return 'number';
      case 'date': return 'date';
      case 'boolean': return 'checkbox';
      default: return 'text';
    }
  }


  getIslemDetay(islemAdi: string) {
    this.islemlerService.getIslemDetay(islemAdi).subscribe(
      (data) => {
        this.islemDetay = data;
        console.log(`✅ İşlem detayları (${islemAdi}):`, this.islemDetay);
        this.fetchTableData();
      },
      (error) => {
        console.error(`🚨 İşlem detayları alınırken hata oluştu (${islemAdi}):`, error);
      }
    );
  }

  fetchTableData() {
    if (!this.islemDetay?.islemJson?.tables) {
      console.warn("⚠️ İşlem detaylarında tablolar bulunamadı.");
      return;
    }

    this.islemDetay.islemJson.tables.forEach((table: string) => {
      this.dynamicTableCrud.getTableData(table).subscribe(
        (data) => {
          this.tableData[table] = data;
          if (data.length > 0) {
            this.tableHeaders[table] = Object.keys(data[0]);
          }
        },
        (error) => {
          console.error(`🚨 ${table} tablosunun verileri alınırken hata oluştu:`, error);
        }
      );
    });
  }

  toggleTable(table: string) {
    this.selectedTable = this.selectedTable === table ? null : table;
  }

  selectRow(table: string, row: any) {
    if (!this.selectedRowsByTable[table]) {
      this.selectedRowsByTable[table] = [];
    }

    const isAlreadySelected = this.selectedRowsByTable[table].some(selected => selected.id === row.id);

    if (!isAlreadySelected) {
      this.selectedRowsByTable[table].push(row);
    }
  }

  removeSelection(table: string, index: number) {
    this.selectedRowsByTable[table].splice(index, 1);
    if (this.selectedRowsByTable[table].length === 0) {
      delete this.selectedRowsByTable[table];
    }
  }

  completeOperation() {
    if (!this.islemDetay) {
      console.error("🚨 İşlem detayı bulunamadı!");
      return;
    }

    const procedureName = this.islemDetay.islemAdi.replace(/\s/g, "_").toLowerCase();

    // ✅ Seçili Satırların ID'lerini Al
    const selectedIds = Object.entries(this.selectedRowsByTable).flatMap(([table, rows]) =>
      rows.map(row => ({ table, id: row.id }))
    );

    // ✅ Input Değerlerini Al
    const inputValues = Object.keys(this.inputValues).map(key => ({
      name: key,
      value: this.inputValues[key]
    }));

    // ✅ Backend'e Gönderilecek JSON
    const payload = {
      procedureName: procedureName,
      selectedIds: selectedIds,
      inputValues: inputValues
    };

    console.log("📌 Prosedür Çalıştırma JSON:", JSON.stringify(payload, null, 2));

    this.islemlerService.executeProcedure(procedureName, payload).subscribe(
      response => {
        console.log("✅ Prosedür çalıştırıldı:", response);
        alert("Prosedür başarıyla çalıştırıldı!");
      },
      error => {
        console.error("🚨 Hata oluştu:", error);
        alert("Prosedür çalıştırılamadı, lütfen tekrar deneyin.");
      }
    );
  }

  protected readonly Object = Object;
}
