package cn.ithcast.core.controller;

import cn.ithcast.core.pojo.order.Order;
import cn.ithcast.core.pojo.order.OrderItem;
import cn.ithcast.core.pojo.user.User;
import cn.ithcast.core.pojogroup.Orderpp;
import cn.ithcast.core.pojogroup.UserVo;
import cn.ithcast.core.service.OrderService;
import cn.ithcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@RequestMapping("/user")
@RestController
@SuppressWarnings("all")
public class UserController {

    // 注入UserService对象
    @Reference
    private UserService userService;

    // 注入OrderService对象
    @Reference
    private OrderService orderService;

    /**
     * 分页+条件查询
     */
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody User user) {
        return userService.search(pageNum, pageSize, user);
    }

    /**
     * 冻结
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] selectIds) {
        try {
            userService.updateStatus(selectIds);
            return new Result(true, "冻结用户成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "冻结用户失败");
        }
    }

    /**
     * 用户活跃度统计
     */
    @RequestMapping("/countActivity")
    public UserVo countActivity() {
        return userService.countActivity();
    }

    /**
     * 导出Excel
     */
    @RequestMapping("/exportExcel")
    public String exportExcel(Long id) throws Exception {
        User user = userService.findById(id);
        if (null != user) {
            List<Orderpp> allOrders = orderService.findAllOrders(user.getUsername());
            if (null != allOrders && allOrders.size() > 0) {
                // 遍历, 封装订单集合
                List<Order> orderList = new ArrayList<>();
                List<OrderItem> orderItemList = new ArrayList<>();
                for (Orderpp allOrder : allOrders) {
                    Order order = allOrder.getOrder();
                    orderList.add(order);

                    List<OrderItem> orderItems = allOrder.getOrderitemList();
                    for (OrderItem orderItem : orderItems) {
                        orderItemList.add(orderItem);
                    }
                }

                String orderSavePath = orderList2Excel(orderList);
                String orderItemSavePath = orderItemList2Excel(orderItemList);

                return orderSavePath + orderItemSavePath;
            } else {
                return "查无订单和商品";
            }
        }

        return "查无此人";
    }


    public String orderList2Excel(List<Order> list) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd hhmmss");
        Workbook wb = new XSSFWorkbook();
        //标题行抽出字段
        String[] title = {"序号", "orderId", "实付金额", "支付类型", "状态", "订单创建时间", "订单更新时间", "用户id", "收货人地区名称", "收货人手机", "收货人", "订单来源", "商家ID"};
        //设置sheet名称，并创建新的sheet对象
        String sheetName = "订单信息一览";
        Sheet orderSheet = wb.createSheet(sheetName);
        //获取表头行
        Row titleRow = orderSheet.createRow(0);
        //创建单元格，设置style居中，字体，单元格大小等
        CellStyle style = wb.createCellStyle();
        Cell cell = null;
        //把已经写好的标题行写入excel文件中
        for (int i = 0; i < title.length; i++) {
            cell = titleRow.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //把从数据库中取得的数据一一写入excel文件中
        Row row = null;
        for (int i = 0; i < list.size(); i++) {
            //创建list.size()行数据
            row = orderSheet.createRow(i + 1);
            //把值一一写进单元格里
            //设置第一列为自动递增的序号
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(list.get(i).getOrderId());
            row.createCell(2).setCellValue(list.get(i).getPayment().doubleValue());
            row.createCell(3).setCellValue(list.get(i).getPaymentType());
            row.createCell(4).setCellValue(list.get(i).getStatus());

            //把时间转换为指定格式的字符串再写入excel文件中
            if (list.get(i).getCreateTime() != null) {
                row.createCell(5).setCellValue(sdf.format(list.get(i).getCreateTime()));
            }
            //把时间转换为指定格式的字符串再写入excel文件中
            if (list.get(i).getUpdateTime() != null) {
                row.createCell(6).setCellValue(sdf.format(list.get(i).getUpdateTime()));
            }

            row.createCell(7).setCellValue(list.get(i).getUserId());
            row.createCell(8).setCellValue(list.get(i).getReceiverAreaName());
            row.createCell(9).setCellValue(list.get(i).getReceiverMobile());
            row.createCell(10).setCellValue(list.get(i).getReceiver());
            row.createCell(11).setCellValue(list.get(i).getSourceType());
            row.createCell(12).setCellValue(list.get(i).getSellerId());
        }
        //设置单元格宽度自适应，在此基础上把宽度调至1.5倍
        for (int i = 0; i < title.length; i++) {
            orderSheet.autoSizeColumn(i, true);
            orderSheet.setColumnWidth(i, orderSheet.getColumnWidth(i) * 15 / 10);
        }
        //获取配置文件中保存对应excel文件的路径，本地也可以直接写成F：excel/stuInfoExcel路径
        String folderPath = ResourceBundle.getBundle("systemconfig").getString("downloadOrderFolder") + File.separator + "orderInfoExcel";

        //创建上传文件目录
        File folder = new File(folderPath);
        //如果文件夹不存在创建对应的文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //设置文件名
        String fileName = sdf1.format(new Date()) + sheetName + ".xlsx";
        String savePath = folderPath + File.separator + fileName;
        // System.out.println(savePath);

        OutputStream fileOut = new FileOutputStream(savePath);
        wb.write(fileOut);
        fileOut.close();
        //返回文件保存全路径
        return savePath;
    }


    public String orderItemList2Excel(List<OrderItem> list) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd hhmmss");
        Workbook wb = new XSSFWorkbook();

        //标题行抽出字段
        String[] title = {"序号", "SPU_ID", "商品id", "订单id", "商品标题", "商品单价", "商品购买数量", "商品总金额", "商家ID"};
        //设置sheet名称，并创建新的sheet对象
        String sheetName = "订单详情信息一览";
        Sheet orderItemSheet = wb.createSheet(sheetName);
        //获取表头行
        Row titleRow = orderItemSheet.createRow(0);
        //创建单元格，设置style居中，字体，单元格大小等
        CellStyle style = wb.createCellStyle();
        Cell cell = null;
        //把已经写好的标题行写入excel文件中
        for (int i = 0; i < title.length; i++) {
            cell = titleRow.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //把从数据库中取得的数据一一写入excel文件中
        Row row = null;
        for (int i = 0; i < list.size(); i++) {
            //创建list.size()行数据
            row = orderItemSheet.createRow(i + 1);
            //把值一一写进单元格里
            //设置第一列为自动递增的序号
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(list.get(i).getItemId());
            row.createCell(2).setCellValue(list.get(i).getGoodsId().doubleValue());
            row.createCell(3).setCellValue(list.get(i).getOrderId());
            row.createCell(4).setCellValue(list.get(i).getTitle());

            row.createCell(5).setCellValue(list.get(i).getPrice().doubleValue());
            row.createCell(6).setCellValue(list.get(i).getNum());
            row.createCell(7).setCellValue(list.get(i).getTotalFee().doubleValue());
            row.createCell(8).setCellValue(list.get(i).getSellerId());
        }
        //设置单元格宽度自适应，在此基础上把宽度调至1.5倍
        for (int i = 0; i < title.length; i++) {
            orderItemSheet.autoSizeColumn(i, true);
            orderItemSheet.setColumnWidth(i, orderItemSheet.getColumnWidth(i) * 15 / 10);
        }
        //获取配置文件中保存对应excel文件的路径，本地也可以直接写成F：excel/stuInfoExcel路径
        String folderPath = ResourceBundle.getBundle("systemconfig").getString("downloadOrderItemFolder") + File.separator + "orderItemInfoExcel";

        //创建上传文件目录
        File folder = new File(folderPath);
        //如果文件夹不存在创建对应的文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //设置文件名
        String fileName = sdf1.format(new Date()) + sheetName + ".xlsx";
        String savePath = folderPath + File.separator + fileName;
        // System.out.println(savePath);

        OutputStream fileOut = new FileOutputStream(savePath);
        wb.write(fileOut);
        fileOut.close();
        //返回文件保存全路径
        return savePath;
    }
}
