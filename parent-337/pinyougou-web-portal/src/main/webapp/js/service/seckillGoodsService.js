app.service('seckillGoodsService',function($http){

    this.search=function (pageNum,pageSize) {
        return $http.post('seckillgoods/searchY.do?pageNum='+pageNum+'&pageSize='+pageSize);
    };

    this.findOne=function (id) {
        return $http.post('seckillgoods/findOne.do?ss='+id);
    };

});