app.service('excelService', function ($http) {
    this.exportExcel = function (id) {
        return $http.get('../user/exportExcel.do?id=' + id);
    }
});
