app.service('itemCatService', function ($http) {
    this.findByParentId = function (parentId) {
        return $http.get('../itemCat/findByParentId.do?parentId=' + parentId);
    };

    this.add = function (entity) {
        return $http.post('../itemCat/add.do', entity);
    };

    this.findOne = function (id) {
        return $http.get('../itemCat/findOne.do?id=' + id);
    };

    this.update = function (entity) {
        return $http.post('../itemCat/update.do', entity);
    };

    this.del = function (selectIds) {
        return $http.get('../itemCat/del.do?selectIds=' + selectIds);
    };
});