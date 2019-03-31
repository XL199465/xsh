app.service('goodsPutawayService', function ($http) {
    this.search = function (pageNum,pageSize,searchEntity) {
        return $http.post('../goodsPutaway/search.do?pageNum=' + pageNum + '&pageSize='+ pageSize,searchEntity);
    };

    this.putaway=function (selectIds) {
        return $http.post('../goodsPutaway/putaway.do?selectIds='+selectIds);
    }
}
);