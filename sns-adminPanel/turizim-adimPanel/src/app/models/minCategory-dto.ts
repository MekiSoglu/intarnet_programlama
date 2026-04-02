export class MinCategoryDto{
    id : number;
    categoryName: string;
    parentId : number;


    constructor() {
      this.id =0;
      this.categoryName = '';
      this.parentId = 0;
    }

}
