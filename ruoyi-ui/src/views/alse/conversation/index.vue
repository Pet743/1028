<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会话名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入会话名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户1 ID" prop="userId1">
        <el-input
          v-model="queryParams.userId1"
          placeholder="请输入用户1 ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户2 ID" prop="userId2">
        <el-input
          v-model="queryParams.userId2"
          placeholder="请输入用户2 ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最后一条消息时间" prop="lastMessageTime">
        <el-date-picker clearable
          v-model="queryParams.lastMessageTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择最后一条消息时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="用户1未读消息数" prop="unreadCount1">
        <el-input
          v-model="queryParams.unreadCount1"
          placeholder="请输入用户1未读消息数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户2未读消息数" prop="unreadCount2">
        <el-input
          v-model="queryParams.unreadCount2"
          placeholder="请输入用户2未读消息数"
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
          v-hasPermi="['alse:conversation:add']"
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
          v-hasPermi="['alse:conversation:edit']"
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
          v-hasPermi="['alse:conversation:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alse:conversation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="conversationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="会话ID" align="center" prop="conversationId" />
      <el-table-column label="会话类型：0-单聊，1-群聊" align="center" prop="conversationType" />
      <el-table-column label="会话名称" align="center" prop="name" />
      <el-table-column label="用户1 ID" align="center" prop="userId1" />
      <el-table-column label="用户2 ID" align="center" prop="userId2" />
      <el-table-column label="最后一条消息内容" align="center" prop="lastMessage" />
      <el-table-column label="最后一条消息时间" align="center" prop="lastMessageTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastMessageTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户1未读消息数" align="center" prop="unreadCount1" />
      <el-table-column label="用户2未读消息数" align="center" prop="unreadCount2" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['alse:conversation:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alse:conversation:remove']"
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

    <!-- 添加或修改聊天会话对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="会话名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入会话名称" />
        </el-form-item>
        <el-form-item label="用户1 ID" prop="userId1">
          <el-input v-model="form.userId1" placeholder="请输入用户1 ID" />
        </el-form-item>
        <el-form-item label="用户2 ID" prop="userId2">
          <el-input v-model="form.userId2" placeholder="请输入用户2 ID" />
        </el-form-item>
        <el-form-item label="最后一条消息内容" prop="lastMessage">
          <el-input v-model="form.lastMessage" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="最后一条消息时间" prop="lastMessageTime">
          <el-date-picker clearable
            v-model="form.lastMessageTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择最后一条消息时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="用户1未读消息数" prop="unreadCount1">
          <el-input v-model="form.unreadCount1" placeholder="请输入用户1未读消息数" />
        </el-form-item>
        <el-form-item label="用户2未读消息数" prop="unreadCount2">
          <el-input v-model="form.unreadCount2" placeholder="请输入用户2未读消息数" />
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
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
import { listConversation, getConversation, delConversation, addConversation, updateConversation } from "@/api/alse/conversation";

export default {
  name: "Conversation",
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
      // 聊天会话表格数据
      conversationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        conversationType: null,
        name: null,
        userId1: null,
        userId2: null,
        lastMessage: null,
        lastMessageTime: null,
        unreadCount1: null,
        unreadCount2: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        conversationType: [
          { required: true, message: "会话类型：0-单聊，1-群聊不能为空", trigger: "change" }
        ],
        userId1: [
          { required: true, message: "用户1 ID不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询聊天会话列表 */
    getList() {
      this.loading = true;
      listConversation(this.queryParams).then(response => {
        this.conversationList = response.rows;
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
        conversationId: null,
        conversationType: null,
        name: null,
        userId1: null,
        userId2: null,
        lastMessage: null,
        lastMessageTime: null,
        unreadCount1: null,
        unreadCount2: null,
        createTime: null,
        updateTime: null,
        createBy: null,
        updateBy: null,
        status: null,
        delFlag: null
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
      this.ids = selection.map(item => item.conversationId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加聊天会话";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const conversationId = row.conversationId || this.ids
      getConversation(conversationId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改聊天会话";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.conversationId != null) {
            updateConversation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addConversation(this.form).then(response => {
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
      const conversationIds = row.conversationId || this.ids;
      this.$modal.confirm('是否确认删除聊天会话编号为"' + conversationIds + '"的数据项？').then(function() {
        return delConversation(conversationIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alse/conversation/export', {
        ...this.queryParams
      }, `conversation_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
