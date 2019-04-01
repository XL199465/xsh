<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Excel表格数据导入导出</title>
</head>
<body>

<!-- Excel表格导出 -->
<a href="http://localhost/student/findall">导出Excel表格内容</a>
<hr>

<!-- Excel表格导入 -->
<form action="http://localhost/student/addall" method="post" enctype="multipart/form-data">
    上传Excel数据文件<input type="file" name=fileupload>
    <input type="submit" value="submit">
</form>

</body>
</html>
