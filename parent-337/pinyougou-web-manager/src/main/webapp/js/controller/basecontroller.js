app.controller('baseController', function ($scope) {
    // 分页初始化参数和重新加载的方法抽取
    $scope.paginationConf = {
        currentPage: 1, // 当前页
        totalItems: 0, // 总条数
        itemsPerPage: 5, // 每页条数
        perPageOptions: [5, 10, 20, 30, 40, 50], // 每页显示条数的下拉框选择
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    // 改变事件
    $scope.reloadList = function () {
        // 分页查询
        // $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        // 条件查询加分页查询
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    // 定义一个数组,用来存放被选中的复选框对应的品牌的id
    $scope.selectIds = [];

    // 更改复选框的状态
    $scope.updateSelection = function ($event, id) {
        // 判断复选框的状态, 如果状态为checked 那么就将对应的id存入变量数组中
        if ($event.target.checked) {
            $scope.selectIds.push(id);
            console.log($scope.selectIds);
        } else {
            // 说明没有选上,那么从数组中移除
            var idLocation = $scope.selectIds.indexOf(id);
            // 第一个参数是位置  第二个参数是移除的个数
            $scope.selectIds.splice(idLocation, 1);
        }
    };

    // jsonToString
    $scope.jsonToString = function (jsonStr, key) {
        var jsonObj = JSON.parse(jsonStr);

        var value = "";

        for (var i = 0; i < jsonObj.length; i++) {
            if (i > 0) {
                value += ",";
            }

            value += jsonObj[i][key];
        }

        return value;
    }
});