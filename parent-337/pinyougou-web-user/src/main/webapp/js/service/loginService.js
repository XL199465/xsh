//服务层
app.service('loginService',function($http){
	//读取列表数据绑定到表单中
	// this.showName=function(){
	// 	return $http.get('../login/name.do');
	// }


    //查询我的订单
    this.findAllOrders=function(){
        return $http.get('../user/findAllOrders.do');
    }

    //查询收货地址
    this.findAllAddress=function(){
        return $http.get('../user/findAllAddress.do');
    }

	
});