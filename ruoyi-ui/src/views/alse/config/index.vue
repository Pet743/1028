<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="支付通道代码(zfb/vx/sqb)" prop="channelCode">
        <el-input
          v-model="queryParams.channelCode"
          placeholder="请输入支付通道代码(zfb/vx/sqb)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付通道名称" prop="channelName">
        <el-input
          v-model="queryParams.channelName"
          placeholder="请输入支付通道名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户号" prop="merchantId">
        <el-input
          v-model="queryParams.merchantId"
          placeholder="请输入商户号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户名称" prop="merchantName">
        <el-input
          v-model="queryParams.merchantName"
          placeholder="请输入商户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="通道权重" prop="weight">
        <el-input
          v-model="queryParams.weight"
          placeholder="请输入通道权重"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付超时时间(秒)" prop="timeout">
        <el-input
          v-model="queryParams.timeout"
          placeholder="请输入支付超时时间(秒)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大并发待支付订单数" prop="concurrentLimit">
        <el-input
          v-model="queryParams.concurrentLimit"
          placeholder="请输入最大并发待支付订单数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否启用(0-禁用 1-启用)" prop="enabled">
        <el-input
          v-model="queryParams.enabled"
          placeholder="请输入是否启用(0-禁用 1-启用)"
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
          v-hasPermi="['alse:config:add']"
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
          v-hasPermi="['alse:config:edit']"
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
          v-hasPermi="['alse:config:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alse:config:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="配置ID" align="center" prop="id" />
      <el-table-column label="支付通道代码(zfb/vx/sqb)" align="center" prop="channelCode" />
      <el-table-column label="支付通道名称" align="center" prop="channelName" />
      <el-table-column label="商户号" align="center" prop="merchantId" />
      <el-table-column label="商户名称" align="center" prop="merchantName" />
      <el-table-column label="通道权重" align="center" prop="weight" />
      <el-table-column label="支付超时时间(秒)" align="center" prop="timeout" />
      <el-table-column label="最大并发待支付订单数" align="center" prop="concurrentLimit" />
      <el-table-column label="是否启用(0-禁用 1-启用)" align="center" prop="enabled" />
      <el-table-column label="通道参数配置(JSON格式)" align="center" prop="channelParams" />
      <el-table-column label="状态(0-正常 1-停用)" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['alse:config:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alse:config:remove']"
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

    <!-- 添加或修改支付通道配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="支付通道代码(zfb/vx/sqb)" prop="channelCode">
          <el-input v-model="form.channelCode" placeholder="请输入支付通道代码(zfb/vx/sqb)" />
        </el-form-item>
        <el-form-item label="支付通道名称" prop="channelName">
          <el-input v-model="form.channelName" placeholder="请输入支付通道名称" />
        </el-form-item>
        <el-form-item label="商户号" prop="merchantId">
          <el-input v-model="form.merchantId" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="商户名称" prop="merchantName">
          <el-input v-model="form.merchantName" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="通道权重" prop="weight">
          <el-input v-model="form.weight" placeholder="请输入通道权重" />
        </el-form-item>
        <el-form-item label="支付超时时间(秒)" prop="timeout">
          <el-input v-model="form.timeout" placeholder="请输入支付超时时间(秒)" />
        </el-form-item>
        <el-form-item label="最大并发待支付订单数" prop="concurrentLimit">
          <el-input v-model="form.concurrentLimit" placeholder="请输入最大并发待支付订单数" />
        </el-form-item>
        <el-form-item label="是否启用(0-禁用 1-启用)" prop="enabled">
          <el-input v-model="form.enabled" placeholder="请输入是否启用(0-禁用 1-启用)" />
        </el-form-item>
        <el-form-item label="通道参数配置(JSON格式)" prop="channelParams">
          <el-input v-model="form.channelParams" type="textarea" placeholder="请输入内容" />
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
import { listConfig, getConfig, delConfig, addConfig, updateConfig } from "@/api/alse/config";

export default {
  name: "Config",
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
      // 支付通道配置表格数据
      configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        channelCode: null,
        channelName: null,
        merchantId: null,
        merchantName: null,
        weight: null,
        timeout: null,
        concurrentLimit: null,
        enabled: null,
        channelParams: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        channelCode: [
          { required: true, message: "支付通道代码(zfb/vx/sqb)不能为空", trigger: "blur" }
        ],
        channelName: [
          { required: true, message: "支付通道名称不能为空", trigger: "blur" }
        ],
        merchantId: [
          { required: true, message: "商户号不能为空", trigger: "blur" }
        ],
        merchantName: [
          { required: true, message: "商户名称不能为空", trigger: "blur" }
        ],
        weight: [
          { required: true, message: "通道权重不能为空", trigger: "blur" }
        ],
        timeout: [
          { required: true, message: "支付超时时间(秒)不能为空", trigger: "blur" }
        ],
        concurrentLimit: [
          { required: true, message: "最大并发待支付订单数不能为空", trigger: "blur" }
        ],
        enabled: [
          { required: true, message: "是否启用(0-禁用 1-启用)不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询支付通道配置列表 */
    getList() {
      this.loading = true;
      listConfig(this.queryParams).then(response => {
        this.configList = response.rows;
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
        id: null,
        channelCode: null,
        channelName: null,
        merchantId: null,
        merchantName: null,
        weight: null,
        timeout: null,
        concurrentLimit: null,
        enabled: null,
        channelParams: null,
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加支付通道配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getConfig(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改支付通道配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addConfig(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除支付通道配置编号为"' + ids + '"的数据项？').then(function() {
        return delConfig(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alse/config/export', {
        ...this.queryParams
      }, `config_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
