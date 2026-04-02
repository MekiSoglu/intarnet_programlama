export class CategoryDto {
  id: number;
  parentId: number | null; // `parent_id` yerine `parentId` kullanılıyor
  categoryName: string;
  categoryDetailsList: number[]; // API'de categoryDetailsList olarak geliyor
  version: number;

  constructor() {
    this.id = 0;
    this.parentId = null; // parentId'yi null olarak başlat
    this.categoryName = '';
    this.categoryDetailsList = [];
    this.version = 0;
  }
}
