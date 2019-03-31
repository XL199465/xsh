app.service('goodsService', function ($http) {
    this.add = function (entity) {
        return $http.post('../goods/add.do', entity);
    };

    this.update = function (entity) {
        return $http.post('../goods/update.do', entity);
    };
    // 分页+条件查询
    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../goods/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize, searchEntity);
    };

    // 根据商品id查询
    this.findOne = function (id) {
        return $http.get('../goods/findOne.do?id=' + id);
    };

    // 批量修改商品状态
    this.updateStatus = function (selectIds, id) {
        return $http.get('../goods/updateStatus.do?selectIds=' + selectIds + '&id=' + id);
    };

    // 批量删除商品
    this.del = function (selectIds) {
        return $http.get('../goods/del.do?selectIds=' + selectIds);
    }
});