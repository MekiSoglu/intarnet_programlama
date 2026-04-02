import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CategoryComponent } from './components/category/category.component';
import { CategoryDetailsComponent } from './components/category-details/category-details.component';
import { ProductsComponent } from './components/products/products.component';
import { AraclarComponent } from "./components/araclar/araclar.component";
import { CreateTableComponent } from "./components/create-table/create-table.component";
import { TablolarimComponent } from "./components/tablolarim/tablolarim.component";
import {CariComponent} from "./components/cari/cari.component";
import {CarilerimComponent} from "./components/carilerim/carilerim.component";
import {IslemlerComponent} from "./components/islemler/islemler.component";
import {IslemlerimComponent} from "./components/islemlerim/islemlerim.component";  // ✅ Tablolarım bileşeni eklendi!

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'create-category', component: CategoryComponent },
  { path: 'create-details', component: CategoryDetailsComponent },
  { path: 'products', component: ProductsComponent },
  { path: 'araclar', component: AraclarComponent },
  { path: 'create-table', component: CreateTableComponent },

  { path: 'tablolarim/:tableName', component: TablolarimComponent },
  { path: 'cari', component: CariComponent },
  { path: 'carilerim', component: CarilerimComponent },
  { path: 'carilerim/:viewName', component: CarilerimComponent },
  { path: 'islemler', component: IslemlerComponent },
  { path: 'islemlerim', component: IslemlerimComponent },
  { path: 'islemlerim/:islemAdi', component: IslemlerimComponent },




  { path: '', redirectTo: '/home', pathMatch: 'full' }  // Varsayılan yönlendirme
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
