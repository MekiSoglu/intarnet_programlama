import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DynamicJoinService } from '../../services/DynamicJoin.service';

@Component({
  selector: 'app-carilerim',
  templateUrl: './carilerim.component.html',
  styleUrls: ['./carilerim.component.css']
})
export class CarilerimComponent implements OnInit {
  cariler: string[] = []; // 🟢 Backend'den Gelen Cariler
  selectedCari: string | null = null; // ✅ Seçilen cari adı
  tableData: any[] = []; // ✅ View'den gelen veri listesi
  tableColumns: string[] = []; // ✅ Dinamik kolon başlıkları
  searchFilters: { [key: string]: string } = {}; // ✅ Kullanıcının sorgu girdilerini saklar

  constructor(private dynamicJoinService: DynamicJoinService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.loadCariler();

    // ✅ URL'den gelen `viewName` parametresini al
    this.route.paramMap.subscribe(params => {
      this.selectedCari = params.get('viewName');
      if (this.selectedCari) {
        console.log("🔹 Seçilen Cari:", this.selectedCari);
        this.fetchCariData(this.selectedCari); // ✅ View'den verileri çek
      }
    });
  }

  /** ✅ Backend'den Carileri Yükle */
  loadCariler() {
    this.dynamicJoinService.getCariler().subscribe(data => {
      this.cariler = data;
      console.log("✅ Yüklenen Cariler:", this.cariler);
    }, error => {
      console.error('Carileri yüklerken hata oluştu:', error);
    });
  }

  /** ✅ Backend'den Seçili View İçin Verileri Al */
  fetchCariData(viewName: string) {
    this.dynamicJoinService.fetchDataFromView(viewName).subscribe(data => {
      console.log("✅ View'den Gelen Veri:", data);

      if (data.length > 0) {
        this.tableColumns = Object.keys(data[0]); // ✅ Kolon isimlerini al
        this.tableData = data;
      } else {
        this.tableColumns = [];
        this.tableData = [];
      }
    }, error => {
      console.error('View verileri alınırken hata oluştu:', error);
    });
  }

  /** ✅ Kullanıcı Enter'a bastığında sorgu yap */
  handleKeyPress(event: KeyboardEvent, column: string) {
    if (event.key === 'Enter') {
      if (!this.searchFilters[column]) {
        this.fetchCariData(this.selectedCari!); // ✅ Eğer kutu boşsa, tüm verileri getir
      } else {
        this.fetchFilteredCariData(column, this.searchFilters[column]); // 🔎 Sorgu başlat
      }
    }
  }

  /** ✅ Backend'e sorgu gönder (Kolon + Değer) */
  fetchFilteredCariData(column: string, value: string) {
    if (!this.selectedCari || !column || !value) return;

    this.dynamicJoinService.fetchFilteredData(this.selectedCari, column, value).subscribe(data => {
      console.log("✅ Filtrelenmiş Veri:", data);
      this.tableData = data; // 📌 Yeni tablo verisini güncelle
    }, error => {
      console.error('Filtrelenmiş veriler alınırken hata oluştu:', error);
    });
  }
}
