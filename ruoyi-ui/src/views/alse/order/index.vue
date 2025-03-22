<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品ID" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品名称" prop="productName">
        <el-input
          v-model="queryParams.productName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品图片URL" prop="productImageUrl">
        <el-input
          v-model="queryParams.productImageUrl"
          placeholder="请输入商品图片URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品单价" prop="productPrice">
        <el-input
          v-model="queryParams.productPrice"
          placeholder="请输入商品单价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易数量" prop="quantity">
        <el-input
          v-model="queryParams.quantity"
          placeholder="请输入交易数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易总金额" prop="totalAmount">
        <el-input
          v-model="queryParams.totalAmount"
          placeholder="请输入交易总金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="付款方式(1:支付宝 2:微信 3:银行卡)" prop="paymentMethod">
        <el-input
          v-model="queryParams.paymentMethod"
          placeholder="请输入付款方式(1:支付宝 2:微信 3:银行卡)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款方式(1:支付宝 2:微信 3:银行卡)" prop="receivingMethod">
        <el-input
          v-model="queryParams.receivingMethod"
          placeholder="请输入收款方式(1:支付宝 2:微信 3:银行卡)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="买家ID" prop="buyerId">
        <el-input
          v-model="queryParams.buyerId"
          placeholder="请输入买家ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="买家电话" prop="buyerPhone">
        <el-input
          v-model="queryParams.buyerPhone"
          placeholder="请输入买家电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="买家姓名" prop="buyerName">
        <el-input
          v-model="queryParams.buyerName"
          placeholder="请输入买家姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="卖家ID" prop="sellerId">
        <el-input
          v-model="queryParams.sellerId"
          placeholder="请输入卖家ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="卖家电话" prop="sellerPhone">
        <el-input
          v-model="queryParams.sellerPhone"
          placeholder="请输入卖家电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="卖家姓名" prop="sellerName">
        <el-input
          v-model="queryParams.sellerName"
          placeholder="请输入卖家姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店铺ID" prop="shopId">
        <el-input
          v-model="queryParams.shopId"
          placeholder="请输入店铺ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店铺名称" prop="shopName">
        <el-input
          v-model="queryParams.shopName"
          placeholder="请输入店铺名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联钱包交易ID" prop="walletTransactionId">
        <el-input
          v-model="queryParams.walletTransactionId"
          placeholder="请输入关联钱包交易ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货地址ID" prop="shippingAddressId">
        <el-input
          v-model="queryParams.shippingAddressId"
          placeholder="请输入收货地址ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物流公司" prop="logisticsCompany">
        <el-input
          v-model="queryParams.logisticsCompany"
          placeholder="请输入物流公司"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物流单号" prop="logisticsNo">
        <el-input
          v-model="queryParams.logisticsNo"
          placeholder="请输入物流单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="付款时间" prop="paymentTime">
        <el-date-picker clearable
          v-model="queryParams.paymentTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择付款时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="发货时间" prop="shippingTime">
        <el-date-picker clearable
          v-model="queryParams.shippingTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发货时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="收货时间" prop="receivingTime">
        <el-date-picker clearable
          v-model="queryParams.receivingTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择收货时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['alse:order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['alse:order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['alse:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alse:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单号" align="center" prop="orderNo" />
      <el-table-column label="商品ID" align="center" prop="productId" />
      <el-table-column label="商品名称" align="center" prop="productName" />
<!--      <el-table-column label="商品图片URL" align="center" prop="productImageUrl" />-->
      <el-table-column label="商品单价" align="center" prop="productPrice" />
      <el-table-column label="交易数量" align="center" prop="quantity" />
      <el-table-column label="交易总金额" align="center" prop="totalAmount" />
      <el-table-column label="付款方式(1:支付宝 2:微信 3:银行卡)" align="center" prop="paymentMethod" />
      <el-table-column label="付款账号信息" align="center" prop="paymentAccount" />
      <el-table-column label="收款方式(1:支付宝 2:微信 3:银行卡)" align="center" prop="receivingMethod" />
<!--      <el-table-column label="收款账号信息" align="center" prop="receivingAccount" />-->
      <el-table-column label="买家ID" align="center" prop="buyerId" />
      <el-table-column label="买家电话" align="center" prop="buyerPhone" />
      <el-table-column label="买家姓名" align="center" prop="buyerName" />
      <el-table-column label="卖家ID" align="center" prop="sellerId" />
      <el-table-column label="卖家电话" align="center" prop="sellerPhone" />
      <el-table-column label="卖家姓名" align="center" prop="sellerName" />
      <el-table-column label="订单状态(1:待付款 2:待发货 3:待收货 4:待评价 5:退款中 6:已关闭)" align="center" prop="orderStatus" />
      <el-table-column label="店铺ID" align="center" prop="shopId" />
      <el-table-column label="店铺名称" align="center" prop="shopName" />
      <el-table-column label="关联钱包交易ID" align="center" prop="walletTransactionId" />
      <el-table-column label="收货地址ID" align="center" prop="shippingAddressId" />
      <el-table-column label="收货地址详情" align="center" prop="shippingAddress" />
      <el-table-column label="物流公司" align="center" prop="logisticsCompany" />
      <el-table-column label="物流单号" align="center" prop="logisticsNo" />
      <el-table-column label="付款时间" align="center" prop="paymentTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.paymentTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发货时间" align="center" prop="shippingTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.shippingTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收货时间" align="center" prop="receivingTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.receivingTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['alse:order:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alse:order:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改商品订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品图片URL" prop="productImageUrl">
          <el-input v-model="form.productImageUrl" placeholder="请输入商品图片URL" />
        </el-form-item>
        <el-form-item label="商品单价" prop="productPrice">
          <el-input v-model="form.productPrice" placeholder="请输入商品单价" />
        </el-form-item>
        <el-form-item label="交易数量" prop="quantity">
          <el-input v-model="form.quantity" placeholder="请输入交易数量" />
        </el-form-item>
        <el-form-item label="交易总金额" prop="totalAmount">
          <el-input v-model="form.totalAmount" placeholder="请输入交易总金额" />
        </el-form-item>
        <el-form-item label="付款方式(1:支付宝 2:微信 3:银行卡)" prop="paymentMethod">
          <el-input v-model="form.paymentMethod" placeholder="请输入付款方式(1:支付宝 2:微信 3:银行卡)" />
        </el-form-item>
        <el-form-item label="付款账号信息" prop="paymentAccount">
          <el-input v-model="form.paymentAccount" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="收款方式(1:支付宝 2:微信 3:银行卡)" prop="receivingMethod">
          <el-input v-model="form.receivingMethod" placeholder="请输入收款方式(1:支付宝 2:微信 3:银行卡)" />
        </el-form-item>
        <el-form-item label="收款账号信息" prop="receivingAccount">
          <el-input v-model="form.receivingAccount" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="买家ID" prop="buyerId">
          <el-input v-model="form.buyerId" placeholder="请输入买家ID" />
        </el-form-item>
        <el-form-item label="买家电话" prop="buyerPhone">
          <el-input v-model="form.buyerPhone" placeholder="请输入买家电话" />
        </el-form-item>
        <el-form-item label="买家姓名" prop="buyerName">
          <el-input v-model="form.buyerName" placeholder="请输入买家姓名" />
        </el-form-item>
        <el-form-item label="卖家ID" prop="sellerId">
          <el-input v-model="form.sellerId" placeholder="请输入卖家ID" />
        </el-form-item>
        <el-form-item label="卖家电话" prop="sellerPhone">
          <el-input v-model="form.sellerPhone" placeholder="请输入卖家电话" />
        </el-form-item>
        <el-form-item label="卖家姓名" prop="sellerName">
          <el-input v-model="form.sellerName" placeholder="请输入卖家姓名" />
        </el-form-item>
        <el-form-item label="店铺ID" prop="shopId">
          <el-input v-model="form.shopId" placeholder="请输入店铺ID" />
        </el-form-item>
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="关联钱包交易ID" prop="walletTransactionId">
          <el-input v-model="form.walletTransactionId" placeholder="请输入关联钱包交易ID" />
        </el-form-item>
        <el-form-item label="收货地址ID" prop="shippingAddressId">
          <el-input v-model="form.shippingAddressId" placeholder="请输入收货地址ID" />
        </el-form-item>
        <el-form-item label="收货地址详情" prop="shippingAddress">
          <el-input v-model="form.shippingAddress" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input v-model="form.logisticsCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="form.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="付款时间" prop="paymentTime">
          <el-date-picker clearable
            v-model="form.paymentTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择付款时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发货时间" prop="shippingTime">
          <el-date-picker clearable
            v-model="form.shippingTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发货时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="收货时间" prop="receivingTime">
          <el-date-picker clearable
            v-model="form.receivingTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择收货时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, delOrder, addOrder, updateOrder } from "@/api/alse/order";

export default {
  name: "Order",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 商品订单表格数据
      orderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: null,
        productId: null,
        productName: null,
        productImageUrl: null,
        productPrice: null,
        quantity: null,
        totalAmount: null,
        paymentMethod: null,
        paymentAccount: null,
        receivingMethod: null,
        receivingAccount: null,
        buyerId: null,
        buyerPhone: null,
        buyerName: null,
        sellerId: null,
        sellerPhone: null,
        sellerName: null,
        orderStatus: null,
        shopId: null,
        shopName: null,
        walletTransactionId: null,
        shippingAddressId: null,
        shippingAddress: null,
        logisticsCompany: null,
        logisticsNo: null,
        paymentTime: null,
        shippingTime: null,
        receivingTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderNo: [
          { required: true, message: "订单号不能为空", trigger: "blur" }
        ],
        productId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        productName: [
          { required: true, message: "商品名称不能为空", trigger: "blur" }
        ],
        productPrice: [
          { required: true, message: "商品单价不能为空", trigger: "blur" }
        ],
        quantity: [
          { required: true, message: "交易数量不能为空", trigger: "blur" }
        ],
        totalAmount: [
          { required: true, message: "交易总金额不能为空", trigger: "blur" }
        ],
        paymentMethod: [
          { required: true, message: "付款方式(1:支付宝 2:微信 3:银行卡)不能为空", trigger: "blur" }
        ],
        receivingMethod: [
          { required: true, message: "收款方式(1:支付宝 2:微信 3:银行卡)不能为空", trigger: "blur" }
        ],
        buyerId: [
          { required: true, message: "买家ID不能为空", trigger: "blur" }
        ],
        sellerId: [
          { required: true, message: "卖家ID不能为空", trigger: "blur" }
        ],
        orderStatus: [
          { required: true, message: "订单状态(1:待付款 2:待发货 3:待收货 4:待评价 5:退款中 6:已关闭)不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询商品订单列表 */
    getList() {
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        orderId: null,
        orderNo: null,
        productId: null,
        productName: null,
        productImageUrl: null,
        productPrice: null,
        quantity: null,
        totalAmount: null,
        paymentMethod: null,
        paymentAccount: null,
        receivingMethod: null,
        receivingAccount: null,
        buyerId: null,
        buyerPhone: null,
        buyerName: null,
        sellerId: null,
        sellerPhone: null,
        sellerName: null,
        orderStatus: null,
        shopId: null,
        shopName: null,
        walletTransactionId: null,
        shippingAddressId: null,
        shippingAddress: null,
        logisticsCompany: null,
        logisticsNo: null,
        paymentTime: null,
        shippingTime: null,
        receivingTime: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品订单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const orderId = row.orderId || this.ids
      getOrder(orderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改商品订单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.orderId != null) {
            updateOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const orderIds = row.orderId || this.ids;
      this.$modal.confirm('是否确认删除商品订单编号为"' + orderIds + '"的数据项？').then(function() {
        return delOrder(orderIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alse/order/export', {
        ...this.queryParams
      }, `order_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
