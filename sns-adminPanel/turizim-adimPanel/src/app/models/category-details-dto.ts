export class CategoryDetailsDto {
  id: number | null;
  name: string;
  categoryList: number[];

  constructor() {
    this.id = null;
    this.name = '';
    this.categoryList = [];
  }
}
