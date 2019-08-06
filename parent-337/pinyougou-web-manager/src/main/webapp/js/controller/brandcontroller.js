// 利用模块获取控制器对象
app.controller('brandController', function ($scope, $controller, brandService) {

    $controller('baseController', {$scope: $scope});// 继承basecontroller

    // 查询所有品牌
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    };


    // 分页查询品牌
    $scope.findPage = function (pageNum, pageSize) {
        brandService.findPage(pageNum, pageSize).success(
            function (response) {
                // 改变数据
                $scope.list = response.rows;
                // 改变总条数
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };


    // 新增品牌的保存品牌
    $scope.save = function () {

        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }

        object.success(
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

    // 根据id查询品牌
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };


    // 删除指定品牌
    $scope.del = function () {
        if (confirm('你确认删除吗?')) {
           /* brandService.del($scope.selectIds).success(
                function (response) {
                    if (response.flag) {
                        alert(response.message);
                        $scope.reloadList();
                    } else {
                        alert(response.message);
                    }
                }
            )*/
        }
    };

    // 条件查询
    $scope.search = function (pageNum, pageSize) {
        brandService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                alert(1);
                console.log(response);
                // 改变数据
                $scope.list = response.rows;
                // 改变总条数
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }
});