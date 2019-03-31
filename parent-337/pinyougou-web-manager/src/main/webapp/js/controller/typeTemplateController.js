app.controller('typeTemplateController', function ($scope, $controller, specificationService, typeTemplateService, brandService) {

    // 继承
    $controller('baseController', {$scope: $scope});

    // 分页+条件查询
    $scope.search = function (pageNum, pageSize) {
        typeTemplateService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    // 定义品牌列表的数据
    // $scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};

    // 查询id和name,存入一个map集合 并将多个这样的集合存入list集合中
    // 定义一个变量 格式为{data:[]}
    /* $scope.brandList = {data: []};
     // 然后执行方法,获取list集合对应的json格式.然后拼接
     $scope.findBrandList = function () {
         brandService.selectOptionList().success(
             function (response) {
                 $scope.brandList = {data: response};
             }
         );
     };

     // 查询规格id和规格的名字,并存入map集合中
     // 定义变量 格式{data:[]}
     $scope.specList = {data: []};
     // 然后执行方法,获取list集合对应的json格式,然后拼接
     $scope.findSpecList = function () {
         specificationService.selectOptionList().success(
             function (response) {
                 $scope.specList = {data: response};
             }
         );
     };

     // 新增扩展属性行
     $scope.addTableRow = function () {
         $scope.entity.customAttributeItems.push({});
     };

     // 删除扩展属性行
     $scope.del = function (index) {
         var location = $scope.entity.customAttributeItems.indexOf(index);
         $scope.entity.customAttributeItems.splice(location);
     };

     // 新增品牌
     $scope.save = function () {
         typeTemplateService.add($scope.entity).success(
             function (response) {
                 if (response.flag) {
                     alert(response.message);
                     $scope.reloadList();
                 } else {
                     alert(response.message);
                 }
             }
         )
     }*/

    // 多选下拉框的小栗子
    // $scope.brandList = {data: [{id: 1, text: '联想'}, {id: 2, text: '华为'}, {id: 3, text: '小米'}]};

    // 品牌多选下拉框
    $scope.brandList = {data: []};
    $scope.findBrandList = function () {
        brandService.selectOptionList().success(
            function (response) {
                $scope.brandList = {data: response};
            }
        )
    };

    // 规格多选下拉框
    $scope.specList = {data: []};
    $scope.findSpecList = function () {
        specificationService.selectOptionList().success(
            function (response) {
                $scope.specList = {data: response};
            }
        )
    };

    // 新增扩展属性行
    $scope.addTableRow = function () {
        $scope.entity.customAttributeItems.push({});
    };

    // 删除扩展属性行
    $scope.deleteTableRow = function (index) {
        var location = $scope.entity.customAttributeItems.indexOf(index);
        $scope.entity.customAttributeItems.splice(location, 1);
    };

    // 新增模板
    $scope.save = function () {

        var object = null;

        if ($scope.entity.id != null) {
            object = typeTemplateService.update($scope.entity);
        } else {
            object = typeTemplateService.add($scope.entity);
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

    // 修改模板之数据回显
    $scope.findOne = function (id) {
        typeTemplateService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);//转换品牌列表
                $scope.entity.specIds = JSON.parse($scope.entity.specIds);//转换规格列表
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);//转换扩展属性
            }
        )
    };

    // 删除模板
    $scope.del = function () {
        typeTemplateService.del($scope.selectIds).success(
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