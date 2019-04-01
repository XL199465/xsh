app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    // 继承
    $controller('baseController', {$scope: $scope});

    // 保存商品
    $scope.save = function () {
        // 提取富文本编辑器中的内容
        $scope.entity.goodsDesc.introduction = editor.html();

        var object = null;
        if ($scope.entity.goods.id != null) {
            object = goodsService.update($scope.entity);
        } else {
            object = goodsService.add($scope.entity);
        }

        object.success(
            function (response) {
                if (response.flag) {
                    alert(response.message);
                    location.href = 'goods.html';
                } else {
                    alert(response.message);
                }
            }
        )
    };

    // 图片上传
    $scope.uploadFile = function () {
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(
            function (response) {
                if (response.flag) {
                    // 获取文件的路径,并赋值给变量
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        )
    };

    // 图片展示在页面
    // $scope.entity = {goods: {}, goodsDesc: {imageDesc: []}} // 定义页面实体的结构
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    // 移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };


    // 一级分类下拉选择框
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    };

    // 二级分类下拉选择框
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        )
    });

    // 三级分类下拉选择框
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        )
    });

    // 三级分类选择后 读取模板ID
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        )
    });

    // 监听模板id之后,获取品牌和扩展属性
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        // 查询品牌和扩展属性
        typeTemplateService.findOne(newValue).success(
            function (response) {
                // 先获取到模板对象
                $scope.typeTemplate = response;

                // 将json格式解析之后,赋值给模板对象的brandIds属性
                // 由json格式转换成为List<Map>集合
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

                if ($location.search['id'] == null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }

            }
        );

        // 查询规格列表
        typeTemplateService.findSpecListById(newValue).success(
            function (response) {
                // 将结果赋值为specList变量
                $scope.specList = response;
            }
        )
    });

    // 保存规格选项到数据库中
    $scope.updateSpecAttribute = function ($event, name, value) {
        // 调用封装的方法判断 勾选的名称是否存在:
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, "attributeName", name);

        if (object != null) {
            // 找到了
            if ($event.target.checked) {
                // 勾选上就添加
                object.attributeValue.push(value);
            } else {
                // 否则移除
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
            }

            // 清空
            if (object.attributeValue.length == 0) {
                var idx = $scope.entity.goodsDesc.specificationItems.indexOf(object);
                $scope.entity.goodsDesc.specificationItems.splice(idx, 1);
            }
        } else {
            // 没找到
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    };


    //创建SKU列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//初始
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    };
    //添加列值
    addColumn = function (list, columnName, conlumnValues) {
        var newList = [];//新的集合
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < conlumnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    };

    // 分页+条件查询
    // 必须初始化searchEntity对象
    $scope.searchEntity = {};
    $scope.search = function (pageNum, pageSize) {
        goodsService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    // 将状态码转为对应的汉字
    $scope.status = ["未审核", "审核通过", "审核未通过", "已驳回"];

    // 将分类id转化为对应的分类名称
    $scope.itemCatList = [];
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    // 将第i号分类作为数组索引,将第i号分类的名称作为值,一一对应
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        )
    };

    // 根据商品id查询包装类对象
    $scope.findOne = function () {
        // 使用$location获取id
        var id = $location.search()['id'];

        // 如果id为空,说明是新增,那么就不需要执行回显方法了, 直接返回
        if (id == null) {
            return;
        }

        goodsService.findOne(id).success(
            function (response) {
                // 将结果赋值给entity变量
                $scope.entity = response;
                // 读取富文本编辑器的内容
                editor.html($scope.entity.goodsDesc.introduction);
                // 显示图片列表
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                // 读取商品扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                // 读取规格列表
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                // 列表规格的格式转换
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec =
                        JSON.parse($scope.entity.itemList[i].spec);
                }

            }
        )
    };

    // 根据规格名称和规格选项来决定复选框的勾选
    $scope.checkAttributeValue = function (specName, optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;

        var object = $scope.searchObjectByKey(items, "attributeName", specName);

        if (object != null) {
            if (object.attributeValue.indexOf(optionName) >= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    //查询订单
    $scope.findAllOrders=function () {
        goodsService.findAllOrders().success(
            function (response) {
                $scope.Orderlist = response;
            }
        )
    }





});
