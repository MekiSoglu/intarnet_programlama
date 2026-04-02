import {Input, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CategoryComponent } from './components/category/category.component';
import { CategoryDetailsComponent } from './components/category-details/category-details.component';
import { HomeComponent } from './components/home/home.component';
import { ProductsComponent } from './components/products/products.component';
import { CategoryService } from './services/category.service';
import { ProductsService } from './services/products.service';
import { CategoryDetailsService } from './services/category-details.service';
import { AraclarComponent } from './components/araclar/araclar.component';
import {MinCategoryDto} from "./models/minCategory-dto";
import { CreateTableComponent } from './components/create-table/create-table.component';
import { TablolarimComponent } from './components/tablolarim/tablolarim.component';
import { CariComponent } from './components/cari/cari.component';
import { CarilerimComponent } from './components/carilerim/carilerim.component';
import { IslemlerComponent } from './components/islemler/islemler.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { IslemlerimComponent } from './components/islemlerim/islemlerim.component';


@NgModule({
  declarations: [
    AppComponent,
    CategoryComponent,
    CategoryDetailsComponent,
    HomeComponent,
    ProductsComponent,
    AraclarComponent,
    CreateTableComponent,
    TablolarimComponent,
    TablolarimComponent,
    CariComponent,
    CarilerimComponent,
    IslemlerComponent,
    IslemlerimComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    DragDropModule


  ],
  providers: [CategoryService, ProductsService, CategoryDetailsService],
  bootstrap: [AppComponent]
})
export class AppModule {


}
