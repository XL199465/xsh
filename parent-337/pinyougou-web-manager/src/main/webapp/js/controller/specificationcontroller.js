app.controller('specificationController', function ($scope, $controller, specificationService) {
    // 继承
    $controller('baseController', {$scope: $scope});

    // 分页+条件查询规格
    $scope.search = function (pageNum, pageSize) {
        specificationService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                // 修改数据
                $scope.list = response.rows;
                // 修改总条数
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    // 新增规格选项
    $scope.addTableRow = function () {
        // 当点击事件发生时,向specificationOptionList中添加元素 元素为空的{}
        // {}代表空的规格行
        $scope.entity.specificationOptionList.push({});
    }

    // 删除规格行
    $scope.deleteTableRow = function (index) {
        // $index 用于获取ng-repeat指令循环中的索引
        // $scope.entity.specificationOptionList.splice(index, 1);

        // 根据就能获得到位置,然后调用splice方法删除  第一个参数是位置  第二个参数是删除的行数
        var location = $scope.entity.specificationOptionList.indexOf(index);
        $scope.entity.specificationOptionList.splice(location, 1);
    }

    // 添加规格的保存/修改规格的保存
    $scope.save = function () {

        var object = null;
        if ($scope.entity.specification.id != null) {
            object = specificationService.update($scope.entity);
        } else {
            object = specificationService.add($scope.entity);
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
    }

    // 修改规格之数据回显
    $scope.findOne = function (id) {
        specificationService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }

    // 删除规格
    $scope.del = function () {
        if (confirm("你确认删除吗?")) {
            specificationService.del($scope.selectIds).success(
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
    }
});