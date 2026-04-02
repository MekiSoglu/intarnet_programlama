import { Component, OnInit } from '@angular/core';
import { Chart, registerables } from 'chart.js';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  soldTours: number = 100;
  pendingTours: number = 50;
  approvedTours: number = 75;

  income: number = 1500;
  expense: number = 500;

  constructor() {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.createTourChart();
    this.createIncomeExpenseChart();
  }

  createTourChart() {
    const canvas = document.getElementById('tourChart') as HTMLCanvasElement;
    if (canvas) {
      const ctx = canvas.getContext('2d');
      if (ctx) {
        new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: ['Satılan', 'Onay Bekleyen', 'Onaylanmış'],
            datasets: [{
              label: 'Tur Durumları',
              data: [this.soldTours, this.pendingTours, this.approvedTours],
              backgroundColor: ['#007bff', '#ffc107', '#28a745'],
              borderColor: ['#007bff', '#ffc107', '#28a745'],
              borderWidth: 1
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: true,
                position: 'bottom',
              },
            }
          }
        });
      }
    }
  }

  createIncomeExpenseChart() {
    const canvas = document.getElementById('incomeExpenseChart') as HTMLCanvasElement;
    if (canvas) {
      const ctx = canvas.getContext('2d');
      if (ctx) {
        new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: ['Gelir', 'Gider'],
            datasets: [{
              label: 'Gelir Gider Durumu',
              data: [this.income, this.expense],
              backgroundColor: ['#28a745', '#dc3545'],
              borderColor: ['#28a745', '#dc3545'],
              borderWidth: 1
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: true,
                position: 'bottom',
              },
            }
          }
        });
      }
    }
  }

}
