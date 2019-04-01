app.service('goodsPutawayService', function ($http) {
    this.search = function (pageNum,pageSize,searchEntity) {
        return $http.post('../goodsPutaway/search.do?pageNum=' + pageNum + '&pageSize='+ pageSize,searchEntity);
    };

    this.putaway=function (selectIds) {
        return $http.post('../goodsPutaway/putaway.do?selectIds='+selectIds);
    }
    this.sold_out=function (selectIds,isMarketable) {
        return $http.post('../goodsPutaway/sold_out.do?selectIds='+selectIds);
    }

}
);