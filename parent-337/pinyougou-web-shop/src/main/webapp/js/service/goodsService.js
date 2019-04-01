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
    }
<<<<<<< Updated upstream

    //查询全部订单
    this.findAllOrders = function () {
        return $http.get('../itemCat/findAllOrders.do');

=======
    //添加到秒杀表
    this.Add_seconds=function (ids) {
        return $http.post('../goods/addSeconds?ids='+ids);
>>>>>>> Stashed changes
    }
});