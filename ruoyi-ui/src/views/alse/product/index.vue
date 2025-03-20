<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品标题" prop="productTitle">
        <el-input
          v-model="queryParams.productTitle"
          placeholder="请输入商品标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品类别" prop="productCategory">
        <el-input
          v-model="queryParams.productCategory"
          placeholder="请输入商品类别"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品封面图片路径" prop="productCoverImg">
        <el-input
          v-model="queryParams.productCoverImg"
          placeholder="请输入商品封面图片路径"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="库存数量" prop="stockQuantity">
        <el-input
          v-model="queryParams.stockQuantity"
          placeholder="请输入库存数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品价格" prop="productPrice">
        <el-input
          v-model="queryParams.productPrice"
          placeholder="请输入商品价格"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品发货方式" prop="shippingMethod">
        <el-input
          v-model="queryParams.shippingMethod"
          placeholder="请输入商品发货方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品销售数量" prop="salesCount">
        <el-input
          v-model="queryParams.salesCount"
          placeholder="请输入商品销售数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布人ID" prop="publisherId">
        <el-input
          v-model="queryParams.publisherId"
          placeholder="请输入发布人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布人姓名" prop="publisherName">
        <el-input
          v-model="queryParams.publisherName"
          placeholder="请输入发布人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布人手机号" prop="publisherPhone">
        <el-input
          v-model="queryParams.publisherPhone"
          placeholder="请输入发布人手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品星级" prop="productRating">
        <el-input
          v-model="queryParams.productRating"
          placeholder="请输入商品星级"
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
          v-hasPermi="['alse:product:add']"
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
          v-hasPermi="['alse:product:edit']"
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
          v-hasPermi="['alse:product:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alse:product:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品ID" align="center" prop="productId" />
      <el-table-column label="商品标题" align="center" prop="productTitle" />
      <el-table-column label="商品类别" align="center" prop="productCategory" />
      <el-table-column label="商品封面图片路径" align="center" prop="productCoverImg" />
      <el-table-column label="商品详情图片" align="center" prop="productDetailImgs" />
      <el-table-column label="商品描述" align="center" prop="productDescription" />
      <el-table-column label="库存数量" align="center" prop="stockQuantity" />
      <el-table-column label="商品价格" align="center" prop="productPrice" />
      <el-table-column label="商品发货方式" align="center" prop="shippingMethod" />
      <el-table-column label="商品销售数量" align="center" prop="salesCount" />
      <el-table-column label="商品状态" align="center" prop="productStatus" />
      <el-table-column label="发布人ID" align="center" prop="publisherId" />
      <el-table-column label="发布人姓名" align="center" prop="publisherName" />
      <el-table-column label="发布人手机号" align="center" prop="publisherPhone" />
      <el-table-column label="商品星级" align="center" prop="productRating" />
      <el-table-column label="店铺名称" align="center" prop="shopName" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['alse:product:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alse:product:remove']"
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

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品标题" prop="productTitle">
          <el-input v-model="form.productTitle" placeholder="请输入商品标题" />
        </el-form-item>
        <el-form-item label="商品类别" prop="productCategory">
          <el-input v-model="form.productCategory" placeholder="请输入商品类别" />
        </el-form-item>
        <el-form-item label="商品封面图片路径" prop="productCoverImg">
          <el-input v-model="form.productCoverImg" placeholder="请输入商品封面图片路径" />
        </el-form-item>
        <el-form-item label="商品详情图片" prop="productDetailImgs">
          <el-input v-model="form.productDetailImgs" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="商品描述" prop="productDescription">
          <el-input v-model="form.productDescription" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input v-model="form.stockQuantity" placeholder="请输入库存数量" />
        </el-form-item>
        <el-form-item label="商品价格" prop="productPrice">
          <el-input v-model="form.productPrice" placeholder="请输入商品价格" />
        </el-form-item>
        <el-form-item label="商品发货方式" prop="shippingMethod">
          <el-input v-model="form.shippingMethod" placeholder="请输入商品发货方式" />
        </el-form-item>
        <el-form-item label="商品销售数量" prop="salesCount">
          <el-input v-model="form.salesCount" placeholder="请输入商品销售数量" />
        </el-form-item>
        <el-form-item label="发布人ID" prop="publisherId">
          <el-input v-model="form.publisherId" placeholder="请输入发布人ID" />
        </el-form-item>
        <el-form-item label="发布人姓名" prop="publisherName">
          <el-input v-model="form.publisherName" placeholder="请输入发布人姓名" />
        </el-form-item>
        <el-form-item label="发布人手机号" prop="publisherPhone">
          <el-input v-model="form.publisherPhone" placeholder="请输入发布人手机号" />
        </el-form-item>
        <el-form-item label="商品星级" prop="productRating">
          <el-input v-model="form.productRating" placeholder="请输入商品星级" />
        </el-form-item>
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入店铺名称" />
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
import { listProduct, getProduct, delProduct, addProduct, updateProduct } from "@/api/alse/product";

export default {
  name: "Product",
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
      // 商品表格数据
      productList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productTitle: null,
        productCategory: null,
        productCoverImg: null,
        productDetailImgs: null,
        productDescription: null,
        stockQuantity: null,
        productPrice: null,
        shippingMethod: null,
        salesCount: null,
        productStatus: null,
        publisherId: null,
        publisherName: null,
        publisherPhone: null,
        productRating: null,
        shopName: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productTitle: [
          { required: true, message: "商品标题不能为空", trigger: "blur" }
        ],
        productCategory: [
          { required: true, message: "商品类别不能为空", trigger: "blur" }
        ],
        productCoverImg: [
          { required: true, message: "商品封面图片路径不能为空", trigger: "blur" }
        ],
        productPrice: [
          { required: true, message: "商品价格不能为空", trigger: "blur" }
        ],
        shippingMethod: [
          { required: true, message: "商品发货方式不能为空", trigger: "blur" }
        ],
        publisherId: [
          { required: true, message: "发布人ID不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询商品列表 */
    getList() {
      this.loading = true;
      listProduct(this.queryParams).then(response => {
        this.productList = response.rows;
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
        productId: null,
        productTitle: null,
        productCategory: null,
        productCoverImg: null,
        productDetailImgs: null,
        productDescription: null,
        stockQuantity: null,
        productPrice: null,
        shippingMethod: null,
        salesCount: null,
        productStatus: null,
        publisherId: null,
        publisherName: null,
        publisherPhone: null,
        productRating: null,
        shopName: null,
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
      this.ids = selection.map(item => item.productId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const productId = row.productId || this.ids
      getProduct(productId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改商品";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.productId != null) {
            updateProduct(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProduct(this.form).then(response => {
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
      const productIds = row.productId || this.ids;
      this.$modal.confirm('是否确认删除商品编号为"' + productIds + '"的数据项？').then(function() {
        return delProduct(productIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alse/product/export', {
        ...this.queryParams
      }, `product_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
