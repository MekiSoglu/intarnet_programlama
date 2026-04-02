export class ProductDetailsDto {
  value: string;
  productId: number | undefined;
  categoryDetailsId: number;

  constructor() {
    this.value = '';
    this.productId = 0;
    this.categoryDetailsId = 0;
  }
}
