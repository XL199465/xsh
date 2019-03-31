app.controller('userController', function ($scope, $controller, userService) {

    // 继承
    $controller('baseController', {$scope: $scope});

    // 分页+条件查询
    $scope.searchEntity = {};
    $scope.search = function (pageNum, pageSize) {
        userService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    // 显示用户状态
    $scope.status = ['禁用', '正常'];

    // 冻结用户
    $scope.updateStatus = function () {
        userService.updateStatus($scope.selectIds).success(
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