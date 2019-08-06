app.controller('orderSeckillController', function ($scope, $controller,orderSeckillService) {
    //继承
    $controller('baseController', {$scope: $scope});

    //分页查询
    $scope.search=function (pageNum,pageSize) {
        orderSeckillService.search(pageNum,pageSize,$scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };


});
