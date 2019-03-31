app.service('sellerService', function ($http) {

    this.add = function (entity) {
        return $http.post('../seller/add.do', entity);
    };

    this.showName = function () {
        return $http.get('../seller/showName.do');
    };
});