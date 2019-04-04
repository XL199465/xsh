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


    //查询全部订单
    this.findAllOrder = function () {
        return $http.get('../itemCat/findAllOrder.do');

    }
        //添加到秒杀表
    this.Add_seconds = function (ids,seckillGoods) {
        return $http.post('../goods/addSeconds.do?ids=' + ids,seckillGoods);
    }

    //订单发货
    this.ordersShipment = function (ids) {
        return $http.post('../itemCat/ordersShipment.do?ids='+ids);
    }
    
    //订单统计
    this.ordersStatistics = function (a) {
        return $http.post('../itemCat/ordersStatistics.do?a='+a);
    }


    this.searchs = function (num, size, a) {
        return $http.post('../itemCat/searchs.do?num=' + num + '&size=' + size + '&a='+a);
    };
});