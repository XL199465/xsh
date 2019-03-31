app.service("specificationService", function ($http) {
    // 分页+条件查询规格
    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../specification/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize, searchEntity);
    };

    // 添加规格的保存
    this.add = function (entity) {
        return $http.post('../specification/add.do', entity);
    }

    // 修改规格的保存
    this.update = function (entity) {
        return $http.post('../specification/update.do', entity);
    }

    // 修改规格之数据回显
    this.findOne = function (id) {
        return $http.get('../specification/findOne.do?id=' + id);
    }

    // 删除规格
    this.del = function (ids) {
        return $http.get('../specification/del.do?ids=' + ids);
    }

    // 查询规格id和规格的名字,并存入map集合中
    this.selectOptionList = function () {
        return $http.get('../specification/selectOptionList.do');
    }
});