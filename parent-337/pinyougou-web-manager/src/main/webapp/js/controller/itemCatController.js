app.controller('itemCatController', function ($scope, $controller, itemCatService, typeTemplateService) {
    $controller('baseController', {$scope: $scope});


    // 定义变量用来保存父id
    $scope.parentId = 0;

    // 根据父id查询
    $scope.findByParentId = function (parentId) {
        // 将父id使用定义好的变量保存起来
        $scope.parentId = parentId;

        itemCatService.findByParentId(parentId).success(
            function (response) {
                $scope.list = response;
            }
        )
    };

    // 初始化级别为1级
    $scope.grade = 1;

    // 改变级别
    $scope.setGrade = function (value) {
        $scope.grade = value;
    };

    // 定义面包屑导航
    $scope.selectList = function (entity) {

        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }

        if ($scope.grade == 2) {
            $scope.entity_1 = entity;
            $scope.entity_2 = null;
        }

        if ($scope.grade == 3) {
            $scope.entity_2 = entity;
        }

        $scope.findByParentId(entity.id);
    };

    $scope.save = function () {
        var object = null;

        if ($scope.entity.id != null) {
            object = itemCatService.update($scope.entity);
        } else {
            $scope.entity.parentId = $scope.parentId;
            object = itemCatService.add($scope.entity);
        }

        object.success(
            function (response) {
                if (response.flag) {
                    alert(response.message);
                    $scope.findByParentId($scope.parentId);
                } else {
                    alert(response.message);
                }
            }
        )
    };

    // 定义变量
    $scope.typeTemplateList = {data: []};
    $scope.findTypeTemplateList = function () {
        typeTemplateService.findTypeTemplateList().success(
            function (response) {
                $scope.typeTemplateList = {data: response};
            }
        )
    };

    // 修改商品分类之数据回显
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    // 删除商品分类
    $scope.del = function () {
        itemCatService.del($scope.selectIds).success(
            function (response) {
                if (response.flag) {
                    alert(response.message);
                    $scope.findByParentId($scope.parentId);
                    $scope.selectIds = [];
                } else {
                    alert(response.message);
                }
            }
        )
    };
});