app.service('userService', function ($http) {

    // 分页+条件查询
    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../user/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize, searchEntity);
    };

    // 冻结
    this.updateStatus = function (selectIds) {
        return $http.get('../user/updateStatus.do?selectIds=' + selectIds);
    };

    // 用户活跃度
    this.countActivity = function () {
        return $http.get('../user/countActivity.do');
    };
});