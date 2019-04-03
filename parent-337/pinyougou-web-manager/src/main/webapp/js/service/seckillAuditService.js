app.service('seckillAuditService', function ($http) {
    this.search=function (pageNum, pageSize) {
        return $http.post('../audit/search.do?pageNum='+ pageNum +'&pageSize=' + pageSize);
    };
    this.updateStatus=function (ids) {
        return $http.post('../audit/addAudit.do?ids='+ids);
    };


});