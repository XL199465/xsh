app.service('typeTemplateService', function ($http) {
    // 分页+条件查询
    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../typeTemplate/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize, searchEntity);
    };

    // 新增模板的保存模板
    this.add = function (entity) {
        return $http.post('../typeTemplate/add.do', entity);
    };

    // 修改模板之数据回显
    this.findOne = function (id) {
        return $http.get('../typeTemplate/findOne.do?id=' + id);
    };

    // 修改模板
    this.update = function (entity) {
        return $http.post('../typeTemplate/update.do', entity);
    };

    // 删除模板
    this.del = function (selectIds) {
        return $http.get('../typeTemplate/del.do?selectIds=' + selectIds);
    };

    this.findTypeTemplateList = function () {
        return $http.get('../typeTemplate/findTypeTemplateList.do');
    }
});