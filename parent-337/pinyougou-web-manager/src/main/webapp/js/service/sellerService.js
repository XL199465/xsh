app.service('sellerService', function ($http) {

    this.add = function (entity) {
        return $http.post('../seller/add.do', entity);
    };

    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../seller/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize, searchEntity);
    };

    this.findOne = function (sellerId) {
        return $http.get('../seller/findOne.do?sellerId=' + sellerId);
    };

    this.updateStatus = function (sellerId, status) {
        return $http.get('../seller/updateStatus.do?sellerId=' + sellerId + '&status=' + status);
    }
});