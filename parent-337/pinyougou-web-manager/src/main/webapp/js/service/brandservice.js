// 自定义服务  可以引入其他服务 比如$http
app.service('brandService', function ($http) {
    // 查询所有品牌
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    };

    // 分页查询品牌
    this.fingPage = function (pageNum, pageSize) {
        return $http.get('../brand/findPage.do?pageNum=' + pageNum + '&pageSize=' + pageSize);
    };

    // 根据id查询品牌
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id)
    };

    // 新建品牌的保存品牌操作
    this.add = function (entity) {
        return $http.post('../brand/add.do', entity)
    };

    // 修改品牌的保存品牌操作
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity)
    };

    // 删除品牌
    this.del = function (ids) {
        return $http.get('../brand/del.do?ids=' + ids)
    };

    // 条件查询
    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../brand/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize, searchEntity)
    };

    // 查询id和name,存入一个map集合 并将多个这样的集合存入list集合中
    this.selectOptionList = function () {
        return $http.get('../brand/selectOptionList.do');
    }
});