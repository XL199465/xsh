//首页控制器
app.controller('indexController',function($scope,loginService){
	// $scope.showName=function(){
	// 	alert(1111111)
	// 		loginService.showName().success(
	// 				function(response){
	// 					$scope.loginName=response.loginName;
	// 				}
	// 		);
	// }

    // 查询我的订单
    $scope.findAllOrders = function () {
        loginService.findAllOrders().success(
            function (response) {
                $scope.orderppList = response;
            }
        )
    };
});