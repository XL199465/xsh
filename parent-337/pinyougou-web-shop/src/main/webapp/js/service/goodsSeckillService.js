app.service('goodsSeckillService', function ($http) {
    this.search=function (pageNum, pageSize, status) {
        return $http.post('../seckill/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize+'&status='+status);

    };


});