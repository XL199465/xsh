app.controller('seckillAuditController', function ($scope,$controller,seckillAuditService) {

    $controller('baseController', {$scope: $scope});// 继承basecontroller
    $scope.status=['申请秒杀'];
    $scope.search = function (pageNum, pageSize) {
        seckillAuditService.search(pageNum, pageSize).success(
            function (response) {
                // 改变数据
                $scope.list = response.rows;
                // 改变总条数
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };
    //申请公共秒杀权限
    $scope.updateStatus=function () {
        seckillAuditService.updateStatus($scope.selectIds).success(
            function (response) {
                if (response.falg){
                    alert(response.message);
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }



            }

        )



    }

});