app.controller('goodsPutawayController', function ($scope,$controller,goodsPutawayService) {
    //继承
    $controller('baseController', {$scope: $scope});


    //定义一个查询的对象
    //分页查询
    $scope.searchEntity = {};
    $scope.status = ["未确认", "上架", "下架"];
    $scope.search=function (pageNum,pageSize) {
        goodsPutawayService.search(pageNum,pageSize,$scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    //上架
    $scope.putaway=function () {
        goodsPutawayService.putaway($scope.selectIds).success(
            function (response) {
                if (response.flag){
                    $scope.reloadList();
                }else {
                    alert("后台错误");
                }
            }
        )
    }



}
);