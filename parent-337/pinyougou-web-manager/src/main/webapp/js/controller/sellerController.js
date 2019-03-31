app.controller('sellerController', function ($scope, $controller, sellerService) {

    // 继承
    $controller('baseController', {$scope: $scope});

    // 商家添加
    $scope.add = function () {
        sellerService.add($scope.entity).success(
            function (response) {
                if (response.flag) {
                    alert(response.message);
                    location.href = 'shoplogin.html';
                } else {
                    alert(response.message);
                }
            }
        )
    };

    // 分页+搜索
    $scope.search = function (pageNum, pageSize) {
        sellerService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    // 商家详情
    $scope.findOne = function (sellerId) {
        sellerService.findOne(sellerId).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    // 更改商品状态
    $scope.updateStatus = function (sellerId, status) {
        sellerService.updateStatus(sellerId, status).success(
            function (response) {
                if (response.flag) {
                    alert(response.message);
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    }
});