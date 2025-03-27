<template>
  <div class="app-container">
    <!-- 搜索和列表部分保持不变 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="支付通道(zfb/vx/sqb)" prop="channelCode">
        <el-input
          v-model="queryParams.channelCode"
          placeholder="请输入支付通道(zfb/vx/sqb)"
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
      <el-table-column label="支付通道" align="center" prop="channelCode" />
      <el-table-column label="支付通道名称" align="center" prop="channelName" />
      <el-table-column label="商户号" align="center" prop="merchantId" />
      <el-table-column label="商户名称" align="center" prop="merchantName" />
      <el-table-column label="通道权重" align="center" prop="weight" />
      <el-table-column label="支付超时时间(秒)" align="center" prop="timeout" />
      <el-table-column label="最大并发待支付订单数" align="center" prop="concurrentLimit" />
      <el-table-column label="是否启用(0-禁用 1-启用)" align="center" prop="enabled" />

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

    <!-- 优化后的添加或修改支付通道配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="支付通道" prop="channelCode">
          <el-select v-model="form.channelCode" placeholder="请选择支付通道" style="width: 100%" @change="handleChannelCodeChange">
            <el-option
              v-for="item in channelOptions"
              :key="item.code"
              :label="item.code"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="支付通道名称" prop="channelName">
          <el-select v-model="form.channelName" placeholder="请选择支付通道名称" style="width: 100%" @change="handleChannelNameChange">
            <el-option
              v-for="item in channelOptions"
              :key="item.name"
              :label="item.name"
              :value="item.name">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商户号" prop="merchantId">
          <el-input v-model="form.merchantId" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="商户名称" prop="merchantName">
          <el-input v-model="form.merchantName" placeholder="请输入商户名称" />
        </el-form-item>

        <el-row>
          <el-col :span="8">
            <el-form-item label="通道权重" prop="weight" label-width="80px">
              <el-input-number v-model="form.weight" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="超时(秒)" prop="timeout" label-width="80px">
              <el-input-number v-model="form.timeout" :min="60" :max="3600" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="并发限制" prop="concurrentLimit" label-width="80px">
              <el-input-number v-model="form.concurrentLimit" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="是否启用" prop="enabled">
              <el-radio-group v-model="form.enabled">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="通道参数配置" prop="channelParams">
          <div class="json-editor-container">
            <div class="json-editor-buttons">
              <el-button size="mini" type="text" icon="el-icon-magic-stick" @click="formatJsonParams">格式化JSON</el-button>
            </div>
            <el-input
              v-model="form.channelParams"
              type="textarea"
              :rows="12"
              class="json-textarea"
              placeholder="请输入JSON格式的通道参数配置"
            />
          </div>
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
      form: {},
      rules: {
        channelCode: [
          { required: true, message: "支付通道不能为空", trigger: "change" }
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
          { required: true, message: "支付超时时间不能为空", trigger: "blur" }
        ],
        concurrentLimit: [
          { required: true, message: "最大并发待支付订单数不能为空", trigger: "blur" }
        ],
        enabled: [
          { required: true, message: "是否启用不能为空", trigger: "change" }
        ],
      },
      // 新增JSON编辑器相关属性
      jsonEditorVisible: false,
      jsonEditorValue: '',
      // 支付通道选项
      channelOptions: [
        { code: "zfb", name: "支付宝", system: 1, value: "1" },
        { code: "vx", name: "微信支付", system: 2, value: "3" },
        { code: "yinghang", name: "银行卡", system: 3, value: "" },
        { code: "qianbao", name: "钱包余额", system: 4, value: "" },
        { code: "zfb", name: "收钱吧", system: 5, value: "18" },
        { code: "zfb", name: "汇付支付宝", system: 6, value: "" },
        { code: "vx", name: "汇付微信", system: 7, value: "" },
        { code: "vx", name: "收钱吧微信", system: 8, value: "" }
      ]
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
        weight: 10,
        timeout: 600,
        concurrentLimit: 10,
        enabled: 1,
        channelParams: null,
        status: "0",
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
        // 格式化JSON以提高可读性
        if (this.form.channelParams) {
          try {
            const jsonObj = JSON.parse(this.form.channelParams);
            this.form.channelParams = JSON.stringify(jsonObj, null, 2);
          } catch (e) {
            // 如果JSON格式不正确，保留原样
          }
        }
        this.open = true;
        this.title = "修改支付通道配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 验证JSON格式
          if (this.form.channelParams) {
            try {
              JSON.parse(this.form.channelParams);
            } catch (e) {
              this.$message.error("通道参数配置JSON格式不正确，请检查");
              return;
            }
          }

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
    },

    /** 格式化JSON参数 */
    formatJsonParams() {
      if (!this.form.channelParams) {
        return;
      }

      try {
        const jsonObj = JSON.parse(this.form.channelParams);
        this.form.channelParams = JSON.stringify(jsonObj, null, 2);
        this.$message.success("JSON格式化成功");
      } catch (e) {
        this.$message.error("JSON格式错误，无法格式化: " + e.message);
      }
    },

    /** 通道代码变更处理 */
    handleChannelCodeChange(code) {
      // 当选择了通道代码时，找到该代码对应的所有可能名称
      const matchingItems = this.channelOptions.filter(item => item.code === code);

      // 如果只有一个匹配项，则自动设置通道名称
      if (matchingItems.length === 1) {
        this.form.channelName = matchingItems[0].name;
      }
        // 如果有多个匹配项（如多个zfb），则保持当前名称不变
      // 或者如果当前名称为空，设置为第一个匹配项的名称
      else if (matchingItems.length > 1 && !this.form.channelName) {
        this.form.channelName = matchingItems[0].name;
      }
    },

    /** 通道名称变更处理 */
    handleChannelNameChange(name) {
      // 通过名称查找对应的通道代码
      const item = this.channelOptions.find(item => item.name === name);
      if (item) {
        this.form.channelCode = item.code;
      }
    }

  }
};
</script>

<style scoped>
.json-editor-container {
  position: relative;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.json-editor-buttons {
  position: absolute;
  right: 8px;
  top: 8px;
  z-index: 1;
}

.json-textarea {
  font-family: "Monaco", "Menlo", "Consolas", "Courier New", monospace;
  font-size: 13px;
}

/* 覆盖el-input textarea样式 */
.json-textarea >>> .el-textarea__inner {
  padding-top: 32px;
  padding-left: 12px;
  padding-right: 12px;
  font-family: "Monaco", "Menlo", "Consolas", "Courier New", monospace;
  font-size: 13px;
  line-height: 1.5;
}
</style>
