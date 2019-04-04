app.controller('userController', function ($scope, $controller, userService, excelService) {

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
    };

    // 展示用户活跃度
    $scope.countActivity = function () {
        userService.countActivity().success(
            function (response) {
                $scope.entity = response;
                $scope.entity.userTotalCount = response.userTotalCount;
                $scope.entity.activityUserCount = response.activityUserCount;
                $scope.entity.unactivityUserCount = response.unactivityUserCount;
            }
        )
    };

    // excel导出
    $scope.exportExcel = function (id) {
        excelService.exportExcel(id).success(
            function (response) {
                alert(response);
            }
        )
    }
});