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

    // 显示商家名和时间
    $scope.showName = function () {
        sellerService.showName().success(
            function (response) {
                $scope.username = response.username;
                $scope.curTime = response.curTime;
            }
        )
    }
});