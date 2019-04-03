app.controller('goodsSeckillController', function ($scope, $controller,goodsSeckillService) {
    //继承
    $controller('baseController', {$scope: $scope});
    $scope.entity ={};
    //分页查询
    $scope.entity.status="0";
    $scope.status = ["普通商品", "秒杀商品"];
    $scope.search=function (pageNum,pageSize) {
        goodsSeckillService.search(pageNum,pageSize,$scope.entity.status).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };
});

