app.controller('seckillGoodsController',function($scope,$controller,$location,seckillGoodsService){
    //继承
    $controller('baseController', {$scope: $scope});

    //分页查询
    $scope.status = ["未发货", "已发货"];
    $scope.search=function (pageNum,pageSize) {
        seckillGoodsService.search(pageNum,pageSize).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    var absurl = $location.absUrl();

    $scope.onee={};
    $scope.findOne=function () {
        seckillGoodsService.findOne(absurl).success(
            function (response) {
                $scope.onee=response;
            }

        )

    }

});