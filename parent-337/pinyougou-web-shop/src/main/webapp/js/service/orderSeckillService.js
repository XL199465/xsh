app.service('orderSeckillService', function ($http) {
    this.search=function (pageNum, PageSize,searchEntity) {
     return $http.post('../seckill/orderSearch.do?pageNum='+pageNum+'&pageSize='+PageSize,searchEntity);
    };
});